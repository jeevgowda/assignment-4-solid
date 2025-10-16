package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.model.BookStatus;
import edu.trincoll.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Book Management Service Tests")
class BookManagementServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookManagementService bookManagementService;

    private Book testBook;
    private List<Book> testBooks;

    @BeforeEach
    void setUp() {
        testBook = new Book("978-0-123456-78-9", "Clean Code", "Robert Martin",
                LocalDate.of(2008, 8, 1));
        testBook.setId(1L);
        testBook.setStatus(BookStatus.AVAILABLE);

        Book anotherBook = new Book("978-0-987654-32-1", "Effective Java", "Joshua Bloch",
                LocalDate.of(2008, 5, 28));
        anotherBook.setId(2L);
        anotherBook.setStatus(BookStatus.CHECKED_OUT);

        testBooks = List.of(testBook, anotherBook);
    }

    @Test
    @DisplayName("Should find book by ISBN successfully")
    void shouldFindBookByIsbn() {
        // Arrange
        when(bookRepository.findByIsbn(testBook.getIsbn())).thenReturn(Optional.of(testBook));

        // Act
        Optional<Book> result = bookManagementService.findByIsbn(testBook.getIsbn());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getIsbn()).isEqualTo(testBook.getIsbn());
        verify(bookRepository).findByIsbn(testBook.getIsbn());
    }

    @Test
    @DisplayName("Should return empty optional when book not found by ISBN")
    void shouldReturnEmptyWhenBookNotFoundByIsbn() {
        // Arrange
        when(bookRepository.findByIsbn("invalid-isbn")).thenReturn(Optional.empty());

        // Act
        Optional<Book> result = bookManagementService.findByIsbn("invalid-isbn");

        // Assert
        assertThat(result).isEmpty();
        verify(bookRepository).findByIsbn("invalid-isbn");
    }

    @Test
    @DisplayName("Should find book by ISBN or throw exception")
    void shouldFindBookByIsbnOrThrow() {
        // Arrange
        when(bookRepository.findByIsbn(testBook.getIsbn())).thenReturn(Optional.of(testBook));

        // Act
        Book result = bookManagementService.findByIsbnOrThrow(testBook.getIsbn());

        // Assert
        assertThat(result).isEqualTo(testBook);
        verify(bookRepository).findByIsbn(testBook.getIsbn());
    }

    @Test
    @DisplayName("Should throw exception when book not found by ISBN")
    void shouldThrowExceptionWhenBookNotFoundByIsbn() {
        // Arrange
        when(bookRepository.findByIsbn("invalid-isbn")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookManagementService.findByIsbnOrThrow("invalid-isbn"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book not found");
        verify(bookRepository).findByIsbn("invalid-isbn");
    }

    @Test
    @DisplayName("Should find books by status")
    void shouldFindBooksByStatus() {
        // Arrange
        List<Book> availableBooks = List.of(testBook);
        when(bookRepository.findByStatus(BookStatus.AVAILABLE)).thenReturn(availableBooks);

        // Act
        List<Book> result = bookManagementService.findByStatus(BookStatus.AVAILABLE);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(BookStatus.AVAILABLE);
        verify(bookRepository).findByStatus(BookStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should find available books")
    void shouldFindAvailableBooks() {
        // Arrange
        List<Book> availableBooks = List.of(testBook);
        when(bookRepository.findByStatus(BookStatus.AVAILABLE)).thenReturn(availableBooks);

        // Act
        List<Book> result = bookManagementService.findAvailableBooks();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(BookStatus.AVAILABLE);
        verify(bookRepository).findByStatus(BookStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should find overdue books")
    void shouldFindOverdueBooks() {
        // Arrange
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Book> overdueBooks = List.of(testBook);
        when(bookRepository.findByDueDateBefore(LocalDate.now())).thenReturn(overdueBooks);

        // Act
        List<Book> result = bookManagementService.findOverdueBooks();

        // Assert
        assertThat(result).hasSize(1);
        verify(bookRepository).findByDueDateBefore(LocalDate.now());
    }

    @Test
    @DisplayName("Should find books by member")
    void shouldFindBooksByMember() {
        // Arrange
        String memberEmail = "test@example.com";
        List<Book> memberBooks = List.of(testBook);
        when(bookRepository.findByCheckedOutBy(memberEmail)).thenReturn(memberBooks);

        // Act
        List<Book> result = bookManagementService.findBooksByMember(memberEmail);

        // Assert
        assertThat(result).hasSize(1);
        verify(bookRepository).findByCheckedOutBy(memberEmail);
    }

    @Test
    @DisplayName("Should count books by status")
    void shouldCountBooksByStatus() {
        // Arrange
        when(bookRepository.countByStatus(BookStatus.AVAILABLE)).thenReturn(5L);

        // Act
        long result = bookManagementService.countByStatus(BookStatus.AVAILABLE);

        // Assert
        assertThat(result).isEqualTo(5L);
        verify(bookRepository).countByStatus(BookStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should save book")
    void shouldSaveBook() {
        // Arrange
        when(bookRepository.save(testBook)).thenReturn(testBook);

        // Act
        Book result = bookManagementService.save(testBook);

        // Assert
        assertThat(result).isEqualTo(testBook);
        verify(bookRepository).save(testBook);
    }

    @Test
    @DisplayName("Should check if book is available")
    void shouldCheckIfBookIsAvailable() {
        // Arrange
        when(bookRepository.findByIsbn(testBook.getIsbn())).thenReturn(Optional.of(testBook));

        // Act
        boolean result = bookManagementService.isBookAvailable(testBook.getIsbn());

        // Assert
        assertThat(result).isTrue();
        verify(bookRepository).findByIsbn(testBook.getIsbn());
    }

    @Test
    @DisplayName("Should return false when book is not available")
    void shouldReturnFalseWhenBookNotAvailable() {
        // Arrange
        testBook.setStatus(BookStatus.CHECKED_OUT);
        when(bookRepository.findByIsbn(testBook.getIsbn())).thenReturn(Optional.of(testBook));

        // Act
        boolean result = bookManagementService.isBookAvailable(testBook.getIsbn());

        // Assert
        assertThat(result).isFalse();
        verify(bookRepository).findByIsbn(testBook.getIsbn());
    }

    @Test
    @DisplayName("Should return false when book not found")
    void shouldReturnFalseWhenBookNotFound() {
        // Arrange
        when(bookRepository.findByIsbn("invalid-isbn")).thenReturn(Optional.empty());

        // Act
        boolean result = bookManagementService.isBookAvailable("invalid-isbn");

        // Assert
        assertThat(result).isFalse();
        verify(bookRepository).findByIsbn("invalid-isbn");
    }

    @Test
    @DisplayName("Should checkout book successfully")
    void shouldCheckoutBookSuccessfully() {
        // Arrange
        String memberEmail = "test@example.com";
        LocalDate dueDate = LocalDate.now().plusDays(14);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        Book result = bookManagementService.checkoutBook(testBook, memberEmail, dueDate);

        // Assert
        assertThat(testBook.getStatus()).isEqualTo(BookStatus.CHECKED_OUT);
        assertThat(testBook.getCheckedOutBy()).isEqualTo(memberEmail);
        assertThat(testBook.getDueDate()).isEqualTo(dueDate);
        verify(bookRepository).save(testBook);
    }

    @Test
    @DisplayName("Should return book successfully")
    void shouldReturnBookSuccessfully() {
        // Arrange
        testBook.setStatus(BookStatus.CHECKED_OUT);
        testBook.setCheckedOutBy("test@example.com");
        testBook.setDueDate(LocalDate.now().plusDays(5));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        Book result = bookManagementService.returnBook(testBook);

        // Assert
        assertThat(testBook.getStatus()).isEqualTo(BookStatus.AVAILABLE);
        assertThat(testBook.getCheckedOutBy()).isNull();
        assertThat(testBook.getDueDate()).isNull();
        verify(bookRepository).save(testBook);
    }

    @Test
    @DisplayName("Should find all books")
    void shouldFindAllBooks() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(testBooks);

        // Act
        List<Book> result = bookManagementService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("Should delete book by ID")
    void shouldDeleteBookById() {
        // Arrange
        Long bookId = 1L;

        // Act
        bookManagementService.deleteById(bookId);

        // Assert
        verify(bookRepository).deleteById(bookId);
    }
}
