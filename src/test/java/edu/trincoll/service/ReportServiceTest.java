package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.model.BookStatus;
import edu.trincoll.model.Member;
import edu.trincoll.model.MembershipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Report Service Tests")
class ReportServiceTest {

    @Mock
    private BookManagementService bookManagementService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private ReportService reportService;

    private Book overdueBook;
    private List<Book> overdueBooks;
    private List<Member> membersWithBooks;

    @BeforeEach
    void setUp() {
        overdueBook = new Book("978-0-123456-78-9", "Clean Code", "Robert Martin",
                LocalDate.of(2008, 8, 1));
        overdueBook.setId(1L);
        overdueBook.setStatus(BookStatus.CHECKED_OUT);
        overdueBook.setCheckedOutBy("john@example.com");
        overdueBook.setDueDate(LocalDate.now().minusDays(5));

        Book anotherOverdueBook = new Book("978-0-987654-32-1", "Effective Java", "Joshua Bloch",
                LocalDate.of(2008, 5, 28));
        anotherOverdueBook.setId(2L);
        anotherOverdueBook.setStatus(BookStatus.CHECKED_OUT);
        anotherOverdueBook.setCheckedOutBy("jane@example.com");
        anotherOverdueBook.setDueDate(LocalDate.now().minusDays(3));

        overdueBooks = List.of(overdueBook, anotherOverdueBook);

        Member activeMember1 = new Member("John Doe", "john@example.com");
        activeMember1.setId(1L);
        activeMember1.setMembershipType(MembershipType.REGULAR);
        activeMember1.setBooksCheckedOut(2);

        Member activeMember2 = new Member("Jane Smith", "jane@example.com");
        activeMember2.setId(2L);
        activeMember2.setMembershipType(MembershipType.PREMIUM);
        activeMember2.setBooksCheckedOut(1);

        membersWithBooks = List.of(activeMember1, activeMember2);
    }

    @Test
    @DisplayName("Should generate overdue books report with books")
    void shouldGenerateOverdueBooksReportWithBooks() {
        // Arrange
        when(bookManagementService.findOverdueBooks()).thenReturn(overdueBooks);

        // Act
        String result = reportService.generateOverdueBooksReport();

        // Assert
        assertThat(result).contains("OVERDUE BOOKS REPORT");
        assertThat(result).contains("Clean Code by Robert Martin");
        assertThat(result).contains("Effective Java by Joshua Bloch");
        assertThat(result).contains("john@example.com");
        assertThat(result).contains("jane@example.com");
        assertThat(result).contains("Total overdue books: 2");
        verify(bookManagementService).findOverdueBooks();
    }

    @Test
    @DisplayName("Should generate overdue books report with no books")
    void shouldGenerateOverdueBooksReportWithNoBooks() {
        // Arrange
        when(bookManagementService.findOverdueBooks()).thenReturn(List.of());

        // Act
        String result = reportService.generateOverdueBooksReport();

        // Assert
        assertThat(result).contains("OVERDUE BOOKS REPORT");
        assertThat(result).contains("No overdue books found.");
        assertThat(result).contains("Total overdue books: 0");
        verify(bookManagementService).findOverdueBooks();
    }

    @Test
    @DisplayName("Should generate available books report")
    void shouldGenerateAvailableBooksReport() {
        // Arrange
        when(bookManagementService.countByStatus(BookStatus.AVAILABLE)).thenReturn(15L);
        when(bookManagementService.findAll()).thenReturn(List.of(overdueBook, new Book("978-0-111111-11-1", "Test Book", "Test Author", LocalDate.now())));

        // Act
        String result = reportService.generateAvailableBooksReport();

        // Assert
        assertThat(result).contains("AVAILABLE BOOKS REPORT");
        assertThat(result).contains("Available books: 15");
        assertThat(result).contains("Total books: 2");
        assertThat(result).contains("Checkout rate:");
        verify(bookManagementService).countByStatus(BookStatus.AVAILABLE);
        verify(bookManagementService).findAll();
    }

    @Test
    @DisplayName("Should generate available books report with zero total books")
    void shouldGenerateAvailableBooksReportWithZeroTotalBooks() {
        // Arrange
        when(bookManagementService.countByStatus(BookStatus.AVAILABLE)).thenReturn(0L);
        when(bookManagementService.findAll()).thenReturn(List.of());

        // Act
        String result = reportService.generateAvailableBooksReport();

        // Assert
        assertThat(result).contains("AVAILABLE BOOKS REPORT");
        assertThat(result).contains("Available books: 0");
        assertThat(result).contains("Total books: 0");
        assertThat(result).contains("Checkout rate: 0.0%");
        verify(bookManagementService).countByStatus(BookStatus.AVAILABLE);
        verify(bookManagementService).findAll();
    }

    @Test
    @DisplayName("Should generate members report")
    void shouldGenerateMembersReport() {
        // Arrange
        when(memberService.count()).thenReturn(10L);
        when(memberService.findMembersWithCheckedOutBooks()).thenReturn(membersWithBooks);

        // Act
        String result = reportService.generateMembersReport();

        // Assert
        assertThat(result).contains("MEMBERS REPORT");
        assertThat(result).contains("Total members: 10");
        assertThat(result).contains("Members with checked out books: 2");
        assertThat(result).contains("Active member rate: 20.0%");
        verify(memberService).count();
        verify(memberService).findMembersWithCheckedOutBooks();
    }

    @Test
    @DisplayName("Should generate members report with zero total members")
    void shouldGenerateMembersReportWithZeroTotalMembers() {
        // Arrange
        when(memberService.count()).thenReturn(0L);
        when(memberService.findMembersWithCheckedOutBooks()).thenReturn(List.of());

        // Act
        String result = reportService.generateMembersReport();

        // Assert
        assertThat(result).contains("MEMBERS REPORT");
        assertThat(result).contains("Total members: 0");
        assertThat(result).contains("Members with checked out books: 0");
        assertThat(result).contains("Active member rate: 0.0%");
        verify(memberService).count();
        verify(memberService).findMembersWithCheckedOutBooks();
    }

    @Test
    @DisplayName("Should generate library summary report")
    void shouldGenerateLibrarySummaryReport() {
        // Arrange
        List<Book> allBooks = List.of(overdueBook, new Book("978-0-111111-11-1", "Test Book", "Test Author", LocalDate.now()));
        when(bookManagementService.findAll()).thenReturn(allBooks);
        when(bookManagementService.countByStatus(BookStatus.AVAILABLE)).thenReturn(1L);
        when(bookManagementService.countByStatus(BookStatus.CHECKED_OUT)).thenReturn(1L);
        when(bookManagementService.findOverdueBooks()).thenReturn(List.of(overdueBook));
        when(memberService.count()).thenReturn(5L);

        // Act
        String result = reportService.generateLibrarySummaryReport();

        // Assert
        assertThat(result).contains("LIBRARY SUMMARY REPORT");
        assertThat(result).contains("Total books: 2");
        assertThat(result).contains("Available books: 1");
        assertThat(result).contains("Checked out books: 1");
        assertThat(result).contains("Overdue books: 1");
        assertThat(result).contains("Total members: 5");
        assertThat(result).contains("Report generated on: " + LocalDate.now());
        verify(bookManagementService).findAll();
        verify(bookManagementService).countByStatus(BookStatus.AVAILABLE);
        verify(bookManagementService).countByStatus(BookStatus.CHECKED_OUT);
        verify(bookManagementService).findOverdueBooks();
        verify(memberService).count();
    }

    @Test
    @DisplayName("Should generate report by type - overdue")
    void shouldGenerateReportByTypeOverdue() {
        // Arrange
        when(bookManagementService.findOverdueBooks()).thenReturn(overdueBooks);

        // Act
        String result = reportService.generateReport("overdue");

        // Assert
        assertThat(result).contains("OVERDUE BOOKS REPORT");
        verify(bookManagementService).findOverdueBooks();
    }

    @Test
    @DisplayName("Should generate report by type - available")
    void shouldGenerateReportByTypeAvailable() {
        // Arrange
        when(bookManagementService.countByStatus(BookStatus.AVAILABLE)).thenReturn(10L);
        when(bookManagementService.findAll()).thenReturn(List.of());

        // Act
        String result = reportService.generateReport("available");

        // Assert
        assertThat(result).contains("AVAILABLE BOOKS REPORT");
        verify(bookManagementService).countByStatus(BookStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should generate report by type - members")
    void shouldGenerateReportByTypeMembers() {
        // Arrange
        when(memberService.count()).thenReturn(5L);
        when(memberService.findMembersWithCheckedOutBooks()).thenReturn(List.of());

        // Act
        String result = reportService.generateReport("members");

        // Assert
        assertThat(result).contains("MEMBERS REPORT");
        verify(memberService).count();
    }

    @Test
    @DisplayName("Should generate report by type - summary")
    void shouldGenerateReportByTypeSummary() {
        // Arrange
        when(bookManagementService.findAll()).thenReturn(List.of());
        when(bookManagementService.countByStatus(BookStatus.AVAILABLE)).thenReturn(0L);
        when(bookManagementService.countByStatus(BookStatus.CHECKED_OUT)).thenReturn(0L);
        when(bookManagementService.findOverdueBooks()).thenReturn(List.of());
        when(memberService.count()).thenReturn(0L);

        // Act
        String result = reportService.generateReport("summary");

        // Assert
        assertThat(result).contains("LIBRARY SUMMARY REPORT");
        verify(bookManagementService).findAll();
    }

    @Test
    @DisplayName("Should handle case insensitive report type")
    void shouldHandleCaseInsensitiveReportType() {
        // Arrange
        when(bookManagementService.findOverdueBooks()).thenReturn(overdueBooks);

        // Act
        String result = reportService.generateReport("OVERDUE");

        // Assert
        assertThat(result).contains("OVERDUE BOOKS REPORT");
        verify(bookManagementService).findOverdueBooks();
    }

    @Test
    @DisplayName("Should throw exception for invalid report type")
    void shouldThrowExceptionForInvalidReportType() {
        // Act & Assert
        assertThatThrownBy(() -> reportService.generateReport("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid report type: invalid");
    }
}
