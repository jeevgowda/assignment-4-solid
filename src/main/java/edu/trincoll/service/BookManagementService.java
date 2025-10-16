package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.model.BookStatus;
import edu.trincoll.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for book management operations.
 * Follows Single Responsibility Principle - handles only book-related operations.
 */
@Service
@Transactional
public class BookManagementService {

    private final BookRepository bookRepository;

    public BookManagementService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Find a book by ISBN
     */
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * Find a book by ISBN or throw exception if not found
     */
    public Book findByIsbnOrThrow(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    /**
     * Get all books by status
     */
    public List<Book> findByStatus(BookStatus status) {
        return bookRepository.findByStatus(status);
    }

    /**
     * Get all available books
     */
    public List<Book> findAvailableBooks() {
        return bookRepository.findByStatus(BookStatus.AVAILABLE);
    }

    /**
     * Get all checked out books
     */
    public List<Book> findCheckedOutBooks() {
        return bookRepository.findByStatus(BookStatus.CHECKED_OUT);
    }

    /**
     * Get overdue books
     */
    public List<Book> findOverdueBooks() {
        return bookRepository.findByDueDateBefore(LocalDate.now());
    }

    /**
     * Get books checked out by a specific member
     */
    public List<Book> findBooksByMember(String memberEmail) {
        return bookRepository.findByCheckedOutBy(memberEmail);
    }

    /**
     * Count books by status
     */
    public long countByStatus(BookStatus status) {
        return bookRepository.countByStatus(status);
    }

    /**
     * Save a book
     */
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    /**
     * Check if a book is available for checkout
     */
    public boolean isBookAvailable(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(book -> book.getStatus() == BookStatus.AVAILABLE)
                .orElse(false);
    }

    /**
     * Update book status to checked out
     */
    public Book checkoutBook(Book book, String memberEmail, LocalDate dueDate) {
        book.setStatus(BookStatus.CHECKED_OUT);
        book.setCheckedOutBy(memberEmail);
        book.setDueDate(dueDate);
        return bookRepository.save(book);
    }

    /**
     * Update book status to available
     */
    public Book returnBook(Book book) {
        book.setStatus(BookStatus.AVAILABLE);
        book.setCheckedOutBy(null);
        book.setDueDate(null);
        return bookRepository.save(book);
    }

    /**
     * Get all books
     */
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    /**
     * Delete a book
     */
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
