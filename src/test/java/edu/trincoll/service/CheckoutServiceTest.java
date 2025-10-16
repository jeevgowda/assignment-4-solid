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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Checkout Service Tests")
class CheckoutServiceTest {

    @Mock
    private BookManagementService bookManagementService;

    @Mock
    private MemberService memberService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private LateFeeStrategyFactory lateFeeStrategyFactory;

    @InjectMocks
    private CheckoutService checkoutService;

    private Book availableBook;
    private Member regularMember;
    private Member premiumMember;
    private Member studentMember;

    @BeforeEach
    void setUp() {
        availableBook = new Book("978-0-123456-78-9", "Clean Code", "Robert Martin",
                LocalDate.of(2008, 8, 1));
        availableBook.setId(1L);
        availableBook.setStatus(BookStatus.AVAILABLE);

        regularMember = new Member("John Doe", "john@example.com");
        regularMember.setId(1L);
        regularMember.setMembershipType(MembershipType.REGULAR);
        regularMember.setBooksCheckedOut(0);

        premiumMember = new Member("Jane Smith", "jane@example.com");
        premiumMember.setId(2L);
        premiumMember.setMembershipType(MembershipType.PREMIUM);
        premiumMember.setBooksCheckedOut(0);

        studentMember = new Member("Bob Student", "bob@example.com");
        studentMember.setId(3L);
        studentMember.setMembershipType(MembershipType.STUDENT);
        studentMember.setBooksCheckedOut(0);
    }

    @Test
    @DisplayName("Should checkout book successfully for regular member")
    void shouldCheckoutBookForRegularMember() {
        // Arrange
        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);
        when(memberService.findByEmailOrThrow(regularMember.getEmail())).thenReturn(regularMember);
        when(bookManagementService.checkoutBook(any(Book.class), anyString(), any(LocalDate.class))).thenReturn(availableBook);
        when(memberService.incrementBooksCheckedOut(any(Member.class))).thenReturn(regularMember);

        // Act
        String result = checkoutService.checkoutBook(availableBook.getIsbn(), regularMember.getEmail());

        // Assert
        assertThat(result).contains("Book checked out successfully");
        assertThat(result).contains("Due date:");
        verify(bookManagementService).findByIsbnOrThrow(availableBook.getIsbn());
        verify(memberService).findByEmailOrThrow(regularMember.getEmail());
        verify(bookManagementService).checkoutBook(eq(availableBook), eq(regularMember.getEmail()), any(LocalDate.class));
        verify(memberService).incrementBooksCheckedOut(regularMember);
        verify(notificationService).sendCheckoutNotification(eq(regularMember.getEmail()), eq(availableBook.getTitle()), any(LocalDate.class));
    }

    @Test
    @DisplayName("Should apply correct loan period for premium member")
    void shouldApplyPremiumLoanPeriod() {
        // Arrange
        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);
        when(memberService.findByEmailOrThrow(premiumMember.getEmail())).thenReturn(premiumMember);
        when(bookManagementService.checkoutBook(any(Book.class), anyString(), any(LocalDate.class))).thenReturn(availableBook);
        when(memberService.incrementBooksCheckedOut(any(Member.class))).thenReturn(premiumMember);

        // Act
        checkoutService.checkoutBook(availableBook.getIsbn(), premiumMember.getEmail());

        // Assert
        verify(bookManagementService).checkoutBook(eq(availableBook), eq(premiumMember.getEmail()), 
                argThat(dueDate -> dueDate.equals(LocalDate.now().plusDays(30))));
    }

    @Test
    @DisplayName("Should apply correct loan period for student member")
    void shouldApplyStudentLoanPeriod() {
        // Arrange
        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);
        when(memberService.findByEmailOrThrow(studentMember.getEmail())).thenReturn(studentMember);
        when(bookManagementService.checkoutBook(any(Book.class), anyString(), any(LocalDate.class))).thenReturn(availableBook);
        when(memberService.incrementBooksCheckedOut(any(Member.class))).thenReturn(studentMember);

        // Act
        checkoutService.checkoutBook(availableBook.getIsbn(), studentMember.getEmail());

        // Assert
        verify(bookManagementService).checkoutBook(eq(availableBook), eq(studentMember.getEmail()), 
                argThat(dueDate -> dueDate.equals(LocalDate.now().plusDays(21))));
    }

    @Test
    @DisplayName("Should enforce checkout limit for regular member")
    void shouldEnforceCheckoutLimitForRegularMember() {
        // Arrange
        regularMember.setBooksCheckedOut(3); // At limit
        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);
        when(memberService.findByEmailOrThrow(regularMember.getEmail())).thenReturn(regularMember);

        // Act
        String result = checkoutService.checkoutBook(availableBook.getIsbn(), regularMember.getEmail());

        // Assert
        assertThat(result).isEqualTo("Member has reached checkout limit");
        verify(bookManagementService, never()).checkoutBook(any(), any(), any());
        verify(memberService, never()).incrementBooksCheckedOut(any());
        verify(notificationService, never()).sendCheckoutNotification(any(), any(), any());
    }

    @Test
    @DisplayName("Should enforce checkout limit for premium member")
    void shouldEnforceCheckoutLimitForPremiumMember() {
        // Arrange
        premiumMember.setBooksCheckedOut(10); // At limit
        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);
        when(memberService.findByEmailOrThrow(premiumMember.getEmail())).thenReturn(premiumMember);

        // Act
        String result = checkoutService.checkoutBook(availableBook.getIsbn(), premiumMember.getEmail());

        // Assert
        assertThat(result).isEqualTo("Member has reached checkout limit");
        verify(bookManagementService, never()).checkoutBook(any(), any(), any());
        verify(memberService, never()).incrementBooksCheckedOut(any());
        verify(notificationService, never()).sendCheckoutNotification(any(), any(), any());
    }

    @Test
    @DisplayName("Should enforce checkout limit for student member")
    void shouldEnforceCheckoutLimitForStudentMember() {
        // Arrange
        studentMember.setBooksCheckedOut(5); // At limit
        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);
        when(memberService.findByEmailOrThrow(studentMember.getEmail())).thenReturn(studentMember);

        // Act
        String result = checkoutService.checkoutBook(availableBook.getIsbn(), studentMember.getEmail());

        // Assert
        assertThat(result).isEqualTo("Member has reached checkout limit");
        verify(bookManagementService, never()).checkoutBook(any(), any(), any());
        verify(memberService, never()).incrementBooksCheckedOut(any());
        verify(notificationService, never()).sendCheckoutNotification(any(), any(), any());
    }

    @Test
    @DisplayName("Should not checkout unavailable book")
    void shouldNotCheckoutUnavailableBook() {
        // Arrange
        availableBook.setStatus(BookStatus.CHECKED_OUT);
        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);
        when(memberService.findByEmailOrThrow(regularMember.getEmail())).thenReturn(regularMember);

        // Act
        String result = checkoutService.checkoutBook(availableBook.getIsbn(), regularMember.getEmail());

        // Assert
        assertThat(result).isEqualTo("Book is not available");
        verify(bookManagementService, never()).checkoutBook(any(), any(), any());
        verify(memberService, never()).incrementBooksCheckedOut(any());
        verify(notificationService, never()).sendCheckoutNotification(any(), any(), any());
    }

    @Test
    @DisplayName("Should return book successfully")
    void shouldReturnBookSuccessfully() {
        // Arrange
        availableBook.setStatus(BookStatus.CHECKED_OUT);
        availableBook.setCheckedOutBy(regularMember.getEmail());
        availableBook.setDueDate(LocalDate.now().plusDays(7));
        regularMember.setBooksCheckedOut(1);

        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);
        when(memberService.findByEmailOrThrow(regularMember.getEmail())).thenReturn(regularMember);
        when(bookManagementService.returnBook(any(Book.class))).thenReturn(availableBook);
        when(memberService.decrementBooksCheckedOut(any(Member.class))).thenReturn(regularMember);

        // Act
        String result = checkoutService.returnBook(availableBook.getIsbn());

        // Assert
        assertThat(result).isEqualTo("Book returned successfully");
        verify(bookManagementService).findByIsbnOrThrow(availableBook.getIsbn());
        verify(memberService).findByEmailOrThrow(regularMember.getEmail());
        verify(bookManagementService).returnBook(availableBook);
        verify(memberService).decrementBooksCheckedOut(regularMember);
        verify(notificationService).sendReturnNotification(eq(regularMember.getEmail()), eq(availableBook.getTitle()), eq(0.0));
    }

    @Test
    @DisplayName("Should not return book that is not checked out")
    void shouldNotReturnBookThatIsNotCheckedOut() {
        // Arrange
        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);

        // Act
        String result = checkoutService.returnBook(availableBook.getIsbn());

        // Assert
        assertThat(result).isEqualTo("Book is not checked out");
        verify(memberService, never()).findByEmailOrThrow(anyString());
        verify(bookManagementService, never()).returnBook(any(Book.class));
        verify(memberService, never()).decrementBooksCheckedOut(any(Member.class));
        verify(notificationService, never()).sendReturnNotification(anyString(), anyString(), anyDouble());
    }

    @Test
    @DisplayName("Should calculate late fee for regular member using strategy")
    void shouldCalculateLateFeeForRegularMember() {
        // Arrange
        availableBook.setStatus(BookStatus.CHECKED_OUT);
        availableBook.setCheckedOutBy(regularMember.getEmail());
        availableBook.setDueDate(LocalDate.now().minusDays(5)); // 5 days late
        regularMember.setBooksCheckedOut(1);
        
        RegularLateFeeStrategy regularStrategy = new RegularLateFeeStrategy();
        when(lateFeeStrategyFactory.getStrategy(MembershipType.REGULAR)).thenReturn(regularStrategy);

        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);
        when(memberService.findByEmailOrThrow(regularMember.getEmail())).thenReturn(regularMember);
        when(bookManagementService.returnBook(any(Book.class))).thenReturn(availableBook);
        when(memberService.decrementBooksCheckedOut(any(Member.class))).thenReturn(regularMember);

        // Act
        String result = checkoutService.returnBook(availableBook.getIsbn());

        // Assert
        assertThat(result).contains("Late fee: $2.50"); // 5 days * $0.50
        verify(notificationService).sendReturnNotification(eq(regularMember.getEmail()), eq(availableBook.getTitle()), eq(2.50));
    }

    @Test
    @DisplayName("Should not charge late fee for premium member using strategy")
    void shouldNotChargeLateFeeForPremiumMember() {
        // Arrange
        availableBook.setStatus(BookStatus.CHECKED_OUT);
        availableBook.setCheckedOutBy(premiumMember.getEmail());
        availableBook.setDueDate(LocalDate.now().minusDays(5)); // 5 days late
        premiumMember.setBooksCheckedOut(1);
        
        PremiumLateFeeStrategy premiumStrategy = new PremiumLateFeeStrategy();
        when(lateFeeStrategyFactory.getStrategy(MembershipType.PREMIUM)).thenReturn(premiumStrategy);

        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);
        when(memberService.findByEmailOrThrow(premiumMember.getEmail())).thenReturn(premiumMember);
        when(bookManagementService.returnBook(any(Book.class))).thenReturn(availableBook);
        when(memberService.decrementBooksCheckedOut(any(Member.class))).thenReturn(premiumMember);

        // Act
        String result = checkoutService.returnBook(availableBook.getIsbn());

        // Assert
        assertThat(result).isEqualTo("Book returned successfully");
        assertThat(result).doesNotContain("Late fee");
        verify(notificationService).sendReturnNotification(eq(premiumMember.getEmail()), eq(availableBook.getTitle()), eq(0.0));
    }

    @Test
    @DisplayName("Should calculate late fee for student member using strategy")
    void shouldCalculateLateFeeForStudentMember() {
        // Arrange
        availableBook.setStatus(BookStatus.CHECKED_OUT);
        availableBook.setCheckedOutBy(studentMember.getEmail());
        availableBook.setDueDate(LocalDate.now().minusDays(4)); // 4 days late
        studentMember.setBooksCheckedOut(1);
        
        StudentLateFeeStrategy studentStrategy = new StudentLateFeeStrategy();
        when(lateFeeStrategyFactory.getStrategy(MembershipType.STUDENT)).thenReturn(studentStrategy);

        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);
        when(memberService.findByEmailOrThrow(studentMember.getEmail())).thenReturn(studentMember);
        when(bookManagementService.returnBook(any(Book.class))).thenReturn(availableBook);
        when(memberService.decrementBooksCheckedOut(any(Member.class))).thenReturn(studentMember);

        // Act
        String result = checkoutService.returnBook(availableBook.getIsbn());

        // Assert
        assertThat(result).contains("Late fee: $1.00"); // 4 days * $0.25
        verify(notificationService).sendReturnNotification(eq(studentMember.getEmail()), eq(availableBook.getTitle()), eq(1.00));
    }

    @Test
    @DisplayName("Should not charge late fee when book is returned on time")
    void shouldNotChargeLateFeeWhenBookReturnedOnTime() {
        // Arrange
        availableBook.setStatus(BookStatus.CHECKED_OUT);
        availableBook.setCheckedOutBy(regularMember.getEmail());
        availableBook.setDueDate(LocalDate.now().plusDays(2)); // Not late yet
        regularMember.setBooksCheckedOut(1);

        when(bookManagementService.findByIsbnOrThrow(availableBook.getIsbn())).thenReturn(availableBook);
        when(memberService.findByEmailOrThrow(regularMember.getEmail())).thenReturn(regularMember);
        when(bookManagementService.returnBook(any(Book.class))).thenReturn(availableBook);
        when(memberService.decrementBooksCheckedOut(any(Member.class))).thenReturn(regularMember);

        // Act
        String result = checkoutService.returnBook(availableBook.getIsbn());

        // Assert
        assertThat(result).isEqualTo("Book returned successfully");
        assertThat(result).doesNotContain("Late fee");
        verify(notificationService).sendReturnNotification(eq(regularMember.getEmail()), eq(availableBook.getTitle()), eq(0.0));
    }
}
