package edu.trincoll.service;

import edu.trincoll.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Facade service that coordinates between different specialized services.
 * This demonstrates the proper use of the refactored services while maintaining
 * backward compatibility with the original LibraryService interface.
 * 
 * This class follows the Facade pattern and delegates to appropriate services
 * based on Single Responsibility Principle.
 */
@Service
public class LibraryFacade {

    private final BookManagementService bookManagementService;
    private final MemberService memberService;
    private final CheckoutService checkoutService;
    private final BookSearchService bookSearchService;
    private final ReportService reportService;

    public LibraryFacade(BookManagementService bookManagementService,
                        MemberService memberService,
                        CheckoutService checkoutService,
                        BookSearchService bookSearchService,
                        ReportService reportService) {
        this.bookManagementService = bookManagementService;
        this.memberService = memberService;
        this.checkoutService = checkoutService;
        this.bookSearchService = bookSearchService;
        this.reportService = reportService;
    }

    /**
     * Checkout a book for a member
     * Delegates to CheckoutService which handles the complete checkout process
     */
    public String checkoutBook(String isbn, String memberEmail) {
        return checkoutService.checkoutBook(isbn, memberEmail);
    }

    /**
     * Return a book
     * Delegates to CheckoutService which handles the complete return process
     */
    public String returnBook(String isbn) {
        return checkoutService.returnBook(isbn);
    }

    /**
     * Search books
     * Delegates to BookSearchService which handles all search operations
     */
    public List<Book> searchBooks(String searchTerm, String searchType) {
        return bookSearchService.searchBooks(searchTerm, searchType);
    }

    /**
     * Generate reports
     * Delegates to ReportService which handles all report generation
     */
    public String generateReport(String reportType) {
        return reportService.generateReport(reportType);
    }

    // Additional convenience methods that demonstrate the power of the refactored services

    /**
     * Get all available books
     */
    public List<Book> getAvailableBooks() {
        return bookManagementService.findAvailableBooks();
    }

    /**
     * Get all overdue books
     */
    public List<Book> getOverdueBooks() {
        return bookManagementService.findOverdueBooks();
    }

    /**
     * Get books checked out by a member
     */
    public List<Book> getMemberBooks(String memberEmail) {
        return bookManagementService.findBooksByMember(memberEmail);
    }

    /**
     * Check if a member can checkout more books
     */
    public boolean canMemberCheckoutMoreBooks(String memberEmail) {
        try {
            var member = memberService.findByEmailOrThrow(memberEmail);
            var maxLimit = getMaxCheckoutLimit(member.getMembershipType());
            return member.getBooksCheckedOut() < maxLimit;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Get member's checkout limit
     */
    private int getMaxCheckoutLimit(edu.trincoll.model.MembershipType membershipType) {
        return switch (membershipType) {
            case REGULAR -> 3;
            case PREMIUM -> 10;
            case STUDENT -> 5;
        };
    }

    /**
     * Get library statistics
     */
    public String getLibraryStatistics() {
        return reportService.generateLibrarySummaryReport();
    }
}
