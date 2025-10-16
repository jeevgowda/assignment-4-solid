package edu.trincoll.service.api;

import edu.trincoll.model.Book;
import java.util.List;

/**
 * Interface defining book search operations
 */
public interface IBookSearchService {
    List<Book> searchByTitle(String title);
    List<Book> searchByAuthor(String author);
    List<Book> searchByTitleAndAuthor(String title, String author);
}