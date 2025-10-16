package edu.trincoll.service.api;

import edu.trincoll.model.Book;
import edu.trincoll.model.Member;

import java.time.LocalDate;

/**
 * Interface defining book management operations
 */
public interface IBookManagementService {
    Book findByIsbnOrThrow(String isbn);
    void checkoutBook(Book book, String memberEmail, LocalDate dueDate);
    void returnBook(Book book);
    Book addBook(String isbn, String title, String author);
    void removeBook(String isbn);
}