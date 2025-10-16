package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.model.BookStatus;
import edu.trincoll.model.Member;
import edu.trincoll.model.MembershipType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service responsible for book checkout and return operations.
 * Follows Single Responsibility Principle - handles only checkout/return operations.
 */
@Service
@Transactional
public class CheckoutService implements ICheckoutService {

    private final BookManagementService bookManagementService;
    private final MemberService memberService;
    private final NotificationService notificationService;
    private final LateFeeStrategyFactory lateFeeStrategyFactory;

    public CheckoutService(BookManagementService bookManagementService, 
                          MemberService memberService,
                          NotificationService notificationService,
                          LateFeeStrategyFactory lateFeeStrategyFactory) {
        this.bookManagementService = bookManagementService;
        this.memberService = memberService;
        this.notificationService = notificationService;
        this.lateFeeStrategyFactory = lateFeeStrategyFactory;
    }

    /**
     * Checkout a book for a member
     */
    public String checkoutBook(String isbn, String memberEmail) {
        // Find book and member
        Book book = bookManagementService.findByIsbnOrThrow(isbn);
        Member member = memberService.findByEmailOrThrow(memberEmail);

        // Validate checkout eligibility
        String validationResult = validateCheckoutEligibility(book, member);
        if (validationResult != null) {
            return validationResult;
        }

        // Calculate loan period based on membership type
        int loanPeriodDays = getLoanPeriodDays(member.getMembershipType());
        LocalDate dueDate = LocalDate.now().plusDays(loanPeriodDays);

        // Update book status
        bookManagementService.checkoutBook(book, member.getEmail(), dueDate);

        // Update member's checked out count
        memberService.incrementBooksCheckedOut(member);

        // Send notification
        notificationService.sendCheckoutNotification(member.getEmail(), book.getTitle(), dueDate);

        return "Book checked out successfully. Due date: " + dueDate;
    }

    /**
     * Return a book
     */
    public String returnBook(String isbn) {
        // Find book
        Book book = bookManagementService.findByIsbnOrThrow(isbn);

        if (book.getStatus() != BookStatus.CHECKED_OUT) {
            return "Book is not checked out";
        }

        // Find member
        String memberEmail = book.getCheckedOutBy();
        Member member = memberService.findByEmailOrThrow(memberEmail);

        // Calculate late fee
        double lateFee = calculateLateFee(book, member);

        // Update book status
        bookManagementService.returnBook(book);

        // Update member's checked out count
        memberService.decrementBooksCheckedOut(member);

        // Send notification
        notificationService.sendReturnNotification(member.getEmail(), book.getTitle(), lateFee);

        if (lateFee > 0) {
            return "Book returned. Late fee: $" + String.format("%.2f", lateFee);
        }

        return "Book returned successfully";
    }

    /**
     * Validate if checkout is eligible
     */
    private String validateCheckoutEligibility(Book book, Member member) {
        // Check if book is available
        if (book.getStatus() != BookStatus.AVAILABLE) {
            return "Book is not available";
        }

        // Check checkout limits
        int maxBooks = getMaxCheckoutLimit(member.getMembershipType());
        if (member.getBooksCheckedOut() >= maxBooks) {
            return "Member has reached checkout limit";
        }

        return null; // Eligible for checkout
    }

    /**
     * Get maximum checkout limit based on membership type
     */
    private int getMaxCheckoutLimit(MembershipType membershipType) {
        return switch (membershipType) {
            case REGULAR -> 3;
            case PREMIUM -> 10;
            case STUDENT -> 5;
        };
    }

    /**
     * Get loan period in days based on membership type
     */
    private int getLoanPeriodDays(MembershipType membershipType) {
        return switch (membershipType) {
            case REGULAR -> 14;
            case PREMIUM -> 30;
            case STUDENT -> 21;
        };
    }

    /**
     * Calculate late fee using the appropriate strategy based on membership type
     */
    private double calculateLateFee(Book book, Member member) {
        if (book.getDueDate().isBefore(LocalDate.now())) {
            long daysLate = LocalDate.now().toEpochDay() - book.getDueDate().toEpochDay();
            LateFeeStrategy strategy = lateFeeStrategyFactory.getStrategy(member.getMembershipType());
            return strategy.calculateLateFee(daysLate);
        }
        return 0.0;
    }
}
