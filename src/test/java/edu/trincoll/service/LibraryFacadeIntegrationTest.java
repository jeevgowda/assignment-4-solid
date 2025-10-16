package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.model.BookStatus;
import edu.trincoll.model.Member;
import edu.trincoll.model.MembershipType;
import edu.trincoll.repository.BookRepository;
import edu.trincoll.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Library Facade Integration Tests")
class LibraryFacadeIntegrationTest {

    @Autowired
    private LibraryFacade libraryFacade;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Book testBook;
    private Member testMember;

    @BeforeEach
    void setUp() {
        // Clean up any existing data
        bookRepository.deleteAll();
        memberRepository.deleteAll();

        // Create test book
        testBook = new Book("978-0-123456-78-9", "Clean Code", "Robert Martin",
                LocalDate.of(2008, 8, 1));
        testBook.setStatus(BookStatus.AVAILABLE);
        testBook = bookRepository.save(testBook);

        // Create test member
        testMember = new Member("John Doe", "john@example.com");
        testMember.setMembershipType(MembershipType.REGULAR);
        testMember = memberRepository.save(testMember);
    }

    @Test
    @DisplayName("Should complete full checkout and return cycle")
    void shouldCompleteFullCheckoutAndReturnCycle() {
        // Act - Checkout book
        String checkoutResult = libraryFacade.checkoutBook(testBook.getIsbn(), testMember.getEmail());

        // Assert - Checkout successful
        assertThat(checkoutResult).contains("Book checked out successfully");
        assertThat(checkoutResult).contains("Due date:");

        // Verify book status changed
        Book updatedBook = bookRepository.findByIsbn(testBook.getIsbn()).orElseThrow();
        assertThat(updatedBook.getStatus()).isEqualTo(BookStatus.CHECKED_OUT);
        assertThat(updatedBook.getCheckedOutBy()).isEqualTo(testMember.getEmail());
        assertThat(updatedBook.getDueDate()).isNotNull();

        // Verify member's checked out count increased
        Member updatedMember = memberRepository.findByEmail(testMember.getEmail()).orElseThrow();
        assertThat(updatedMember.getBooksCheckedOut()).isEqualTo(1);

        // Act - Return book
        String returnResult = libraryFacade.returnBook(testBook.getIsbn());

        // Assert - Return successful
        assertThat(returnResult).isEqualTo("Book returned successfully");

        // Verify book status restored
        Book returnedBook = bookRepository.findByIsbn(testBook.getIsbn()).orElseThrow();
        assertThat(returnedBook.getStatus()).isEqualTo(BookStatus.AVAILABLE);
        assertThat(returnedBook.getCheckedOutBy()).isNull();
        assertThat(returnedBook.getDueDate()).isNull();

        // Verify member's checked out count decreased
        Member returnedMember = memberRepository.findByEmail(testMember.getEmail()).orElseThrow();
        assertThat(returnedMember.getBooksCheckedOut()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle different membership types correctly")
    void shouldHandleDifferentMembershipTypesCorrectly() {
        // Create premium member
        Member premiumMember = new Member("Jane Smith", "jane@example.com");
        premiumMember.setMembershipType(MembershipType.PREMIUM);
        premiumMember = memberRepository.save(premiumMember);

        // Act - Checkout book for premium member
        String checkoutResult = libraryFacade.checkoutBook(testBook.getIsbn(), premiumMember.getEmail());

        // Assert - Premium member gets longer loan period
        assertThat(checkoutResult).contains("Book checked out successfully");
        Book updatedBook = bookRepository.findByIsbn(testBook.getIsbn()).orElseThrow();
        assertThat(updatedBook.getDueDate()).isEqualTo(LocalDate.now().plusDays(30)); // Premium gets 30 days
    }

    @Test
    @DisplayName("Should enforce checkout limits")
    void shouldEnforceCheckoutLimits() {
        // Create additional books
        Book book2 = new Book("978-0-111111-11-1", "Book 2", "Author 2", LocalDate.now());
        book2.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book2);

        Book book3 = new Book("978-0-222222-22-2", "Book 3", "Author 3", LocalDate.now());
        book3.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book3);

        Book book4 = new Book("978-0-333333-33-3", "Book 4", "Author 4", LocalDate.now());
        book4.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book4);

        // Checkout 3 books (regular member limit)
        libraryFacade.checkoutBook(testBook.getIsbn(), testMember.getEmail());
        libraryFacade.checkoutBook(book2.getIsbn(), testMember.getEmail());
        libraryFacade.checkoutBook(book3.getIsbn(), testMember.getEmail());

        // Try to checkout 4th book - should fail
        String result = libraryFacade.checkoutBook(book4.getIsbn(), testMember.getEmail());
        assertThat(result).isEqualTo("Member has reached checkout limit");
    }

    @Test
    @DisplayName("Should search books correctly")
    void shouldSearchBooksCorrectly() {
        // Create additional books
        Book book2 = new Book("978-0-111111-11-1", "Effective Java", "Joshua Bloch", LocalDate.now());
        book2.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book2);

        // Search by title
        List<Book> titleResults = libraryFacade.searchBooks("Clean", "title");
        assertThat(titleResults).hasSize(1);
        assertThat(titleResults.get(0).getTitle()).contains("Clean");

        // Search by author
        List<Book> authorResults = libraryFacade.searchBooks("Joshua Bloch", "author");
        assertThat(authorResults).hasSize(1);
        assertThat(authorResults.get(0).getAuthor()).isEqualTo("Joshua Bloch");

        // Search by ISBN
        List<Book> isbnResults = libraryFacade.searchBooks(testBook.getIsbn(), "isbn");
        assertThat(isbnResults).hasSize(1);
        assertThat(isbnResults.get(0).getIsbn()).isEqualTo(testBook.getIsbn());
    }

    @Test
    @DisplayName("Should generate reports correctly")
    void shouldGenerateReportsCorrectly() {
        // Create additional data for comprehensive reports
        Member member2 = new Member("Jane Smith", "jane@example.com");
        member2.setMembershipType(MembershipType.PREMIUM);
        memberRepository.save(member2);

        Book overdueBook = new Book("978-0-999999-99-9", "Overdue Book", "Overdue Author", LocalDate.now());
        overdueBook.setStatus(BookStatus.CHECKED_OUT);
        overdueBook.setCheckedOutBy(testMember.getEmail());
        overdueBook.setDueDate(LocalDate.now().minusDays(5));
        bookRepository.save(overdueBook);

        // Generate overdue report
        String overdueReport = libraryFacade.generateReport("overdue");
        assertThat(overdueReport).contains("OVERDUE BOOKS REPORT");
        assertThat(overdueReport).contains("Overdue Book");

        // Generate available report
        String availableReport = libraryFacade.generateReport("available");
        assertThat(availableReport).contains("AVAILABLE BOOKS REPORT");

        // Generate members report
        String membersReport = libraryFacade.generateReport("members");
        assertThat(membersReport).contains("MEMBERS REPORT");
        assertThat(membersReport).contains("Total members: 2");

        // Generate summary report
        String summaryReport = libraryFacade.generateReport("summary");
        assertThat(summaryReport).contains("LIBRARY SUMMARY REPORT");
        assertThat(summaryReport).contains("Total books:");
        assertThat(summaryReport).contains("Total members:");
    }

    @Test
    @DisplayName("Should provide library statistics")
    void shouldProvideLibraryStatistics() {
        // Act
        String statistics = libraryFacade.getLibraryStatistics();

        // Assert
        assertThat(statistics).contains("LIBRARY SUMMARY REPORT");
        assertThat(statistics).contains("Total books: 1");
        assertThat(statistics).contains("Total members: 1");
        assertThat(statistics).contains("Report generated on:");
    }

    @Test
    @DisplayName("Should check member checkout eligibility")
    void shouldCheckMemberCheckoutEligibility() {
        // Initially should be able to checkout
        assertThat(libraryFacade.canMemberCheckoutMoreBooks(testMember.getEmail())).isTrue();

        // Checkout a book
        libraryFacade.checkoutBook(testBook.getIsbn(), testMember.getEmail());

        // Should still be able to checkout (regular member limit is 3)
        assertThat(libraryFacade.canMemberCheckoutMoreBooks(testMember.getEmail())).isTrue();

        // Create and checkout more books to reach limit
        Book book2 = new Book("978-0-111111-11-1", "Book 2", "Author 2", LocalDate.now());
        book2.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book2);

        Book book3 = new Book("978-0-222222-22-2", "Book 3", "Author 3", LocalDate.now());
        book3.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book3);

        Book book4 = new Book("978-0-333333-33-3", "Book 4", "Author 4", LocalDate.now());
        book4.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book4);

        libraryFacade.checkoutBook(book2.getIsbn(), testMember.getEmail());
        libraryFacade.checkoutBook(book3.getIsbn(), testMember.getEmail());

        // Now should not be able to checkout more
        assertThat(libraryFacade.canMemberCheckoutMoreBooks(testMember.getEmail())).isFalse();
    }

    @Test
    @DisplayName("Should handle non-existent member gracefully")
    void shouldHandleNonExistentMemberGracefully() {
        // Act
        boolean canCheckout = libraryFacade.canMemberCheckoutMoreBooks("nonexistent@example.com");

        // Assert
        assertThat(canCheckout).isFalse();
    }
}
