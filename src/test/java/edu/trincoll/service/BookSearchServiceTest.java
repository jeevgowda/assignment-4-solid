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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Book Search Service Tests")
class BookSearchServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookSearchService bookSearchService;

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
    @DisplayName("Should search books by title")
    void shouldSearchBooksByTitle() {
        // Arrange
        String searchTerm = "Clean";
        List<Book> searchResults = List.of(testBook);
        when(bookRepository.findByTitleContainingIgnoreCase(searchTerm)).thenReturn(searchResults);

        // Act
        List<Book> result = bookSearchService.searchByTitle(searchTerm);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains("Clean");
        verify(bookRepository).findByTitleContainingIgnoreCase(searchTerm);
    }

    @Test
    @DisplayName("Should search books by author")
    void shouldSearchBooksByAuthor() {
        // Arrange
        String searchTerm = "Robert Martin";
        List<Book> searchResults = List.of(testBook);
        when(bookRepository.findByAuthor(searchTerm)).thenReturn(searchResults);

        // Act
        List<Book> result = bookSearchService.searchByAuthor(searchTerm);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthor()).isEqualTo(searchTerm);
        verify(bookRepository).findByAuthor(searchTerm);
    }

    @Test
    @DisplayName("Should search book by ISBN")
    void shouldSearchBookByIsbn() {
        // Arrange
        String isbn = testBook.getIsbn();
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(testBook));

        // Act
        List<Book> result = bookSearchService.searchByIsbn(isbn);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsbn()).isEqualTo(isbn);
        verify(bookRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("Should return empty list when book not found by ISBN")
    void shouldReturnEmptyListWhenBookNotFoundByIsbn() {
        // Arrange
        String isbn = "invalid-isbn";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // Act
        List<Book> result = bookSearchService.searchByIsbn(isbn);

        // Assert
        assertThat(result).isEmpty();
        verify(bookRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("Should get available books")
    void shouldGetAvailableBooks() {
        // Arrange
        List<Book> availableBooks = List.of(testBook);
        when(bookRepository.findByStatus(BookStatus.AVAILABLE)).thenReturn(availableBooks);

        // Act
        List<Book> result = bookSearchService.getAvailableBooks();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(BookStatus.AVAILABLE);
        verify(bookRepository).findByStatus(BookStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should get checked out books")
    void shouldGetCheckedOutBooks() {
        // Arrange
        List<Book> checkedOutBooks = List.of(testBooks.get(1));
        when(bookRepository.findByStatus(BookStatus.CHECKED_OUT)).thenReturn(checkedOutBooks);

        // Act
        List<Book> result = bookSearchService.getCheckedOutBooks();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(BookStatus.CHECKED_OUT);
        verify(bookRepository).findByStatus(BookStatus.CHECKED_OUT);
    }

    @Test
    @DisplayName("Should get overdue books")
    void shouldGetOverdueBooks() {
        // Arrange
        List<Book> overdueBooks = List.of(testBook);
        when(bookRepository.findByDueDateBefore(java.time.LocalDate.now())).thenReturn(overdueBooks);

        // Act
        List<Book> result = bookSearchService.getOverdueBooks();

        // Assert
        assertThat(result).hasSize(1);
        verify(bookRepository).findByDueDateBefore(java.time.LocalDate.now());
    }

    @Test
    @DisplayName("Should get books by member")
    void shouldGetBooksByMember() {
        // Arrange
        String memberEmail = "test@example.com";
        List<Book> memberBooks = List.of(testBook);
        when(bookRepository.findByCheckedOutBy(memberEmail)).thenReturn(memberBooks);

        // Act
        List<Book> result = bookSearchService.getBooksByMember(memberEmail);

        // Assert
        assertThat(result).hasSize(1);
        verify(bookRepository).findByCheckedOutBy(memberEmail);
    }

    @Test
    @DisplayName("Should search books with title type")
    void shouldSearchBooksWithTitleType() {
        // Arrange
        String searchTerm = "Clean";
        List<Book> searchResults = List.of(testBook);
        when(bookRepository.findByTitleContainingIgnoreCase(searchTerm)).thenReturn(searchResults);

        // Act
        List<Book> result = bookSearchService.searchBooks(searchTerm, "title");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains("Clean");
        verify(bookRepository).findByTitleContainingIgnoreCase(searchTerm);
    }

    @Test
    @DisplayName("Should search books with author type")
    void shouldSearchBooksWithAuthorType() {
        // Arrange
        String searchTerm = "Robert Martin";
        List<Book> searchResults = List.of(testBook);
        when(bookRepository.findByAuthor(searchTerm)).thenReturn(searchResults);

        // Act
        List<Book> result = bookSearchService.searchBooks(searchTerm, "author");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthor()).isEqualTo(searchTerm);
        verify(bookRepository).findByAuthor(searchTerm);
    }

    @Test
    @DisplayName("Should search books with ISBN type")
    void shouldSearchBooksWithIsbnType() {
        // Arrange
        String searchTerm = testBook.getIsbn();
        when(bookRepository.findByIsbn(searchTerm)).thenReturn(Optional.of(testBook));

        // Act
        List<Book> result = bookSearchService.searchBooks(searchTerm, "isbn");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsbn()).isEqualTo(searchTerm);
        verify(bookRepository).findByIsbn(searchTerm);
    }

    @Test
    @DisplayName("Should handle case insensitive search type")
    void shouldHandleCaseInsensitiveSearchType() {
        // Arrange
        String searchTerm = "Clean";
        List<Book> searchResults = List.of(testBook);
        when(bookRepository.findByTitleContainingIgnoreCase(searchTerm)).thenReturn(searchResults);

        // Act
        List<Book> result = bookSearchService.searchBooks(searchTerm, "TITLE");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains("Clean");
        verify(bookRepository).findByTitleContainingIgnoreCase(searchTerm);
    }

    @Test
    @DisplayName("Should throw exception for invalid search type")
    void shouldThrowExceptionForInvalidSearchType() {
        // Act & Assert
        assertThatThrownBy(() -> bookSearchService.searchBooks("test", "invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid search type: invalid");
    }

    @Test
    @DisplayName("Should return empty list when no books found")
    void shouldReturnEmptyListWhenNoBooksFound() {
        // Arrange
        String searchTerm = "Nonexistent";
        when(bookRepository.findByTitleContainingIgnoreCase(searchTerm)).thenReturn(List.of());

        // Act
        List<Book> result = bookSearchService.searchByTitle(searchTerm);

        // Assert
        assertThat(result).isEmpty();
        verify(bookRepository).findByTitleContainingIgnoreCase(searchTerm);
    }
}
