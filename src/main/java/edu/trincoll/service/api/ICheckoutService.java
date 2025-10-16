package edu.trincoll.service.api;

/**
 * Interface defining checkout operations
 */
public interface ICheckoutService {
    String checkoutBook(String isbn, String memberEmail);
    String returnBook(String isbn);
}