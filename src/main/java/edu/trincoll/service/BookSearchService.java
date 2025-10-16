package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for book search operations.
 * Follows Single Responsibility Principle - handles only search operations.
 * Follows Interface Segregation Principle - provides focused search functionality.
 */
@Service
public class BookSearchService {

    private final BookRepository bookRepository;

    public BookSearchService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Search books by title (case-insensitive partial match)
     */
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Search books by author
     */
    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    /**
     * Search book by ISBN
     */
    public List<Book> searchByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(List::of)
                .orElse(List.of());
    }

    /**
     * Get all available books
     */
    public List<Book> getAvailableBooks() {
        return bookRepository.findByStatus(edu.trincoll.model.BookStatus.AVAILABLE);
    }

    /**
     * Get all checked out books
     */
    public List<Book> getCheckedOutBooks() {
        return bookRepository.findByStatus(edu.trincoll.model.BookStatus.CHECKED_OUT);
    }

    /**
     * Get overdue books
     */
    public List<Book> getOverdueBooks() {
        return bookRepository.findByDueDateBefore(java.time.LocalDate.now());
    }

    /**
     * Get books checked out by a specific member
     */
    public List<Book> getBooksByMember(String memberEmail) {
        return bookRepository.findByCheckedOutBy(memberEmail);
    }

    /**
     * Generic search method that delegates to specific search methods
     */
    public List<Book> searchBooks(String searchTerm, String searchType) {
        return switch (searchType.toLowerCase()) {
            case "title" -> searchByTitle(searchTerm);
            case "author" -> searchByAuthor(searchTerm);
            case "isbn" -> searchByIsbn(searchTerm);
            default -> throw new IllegalArgumentException("Invalid search type: " + searchType);
        };
    }
}
