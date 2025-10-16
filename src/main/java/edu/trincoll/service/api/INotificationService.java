package edu.trincoll.service.api;

import java.time.LocalDate;

/**
 * Interface defining notification operations
 */
public interface INotificationService {
    void sendCheckoutNotification(String memberEmail, String bookTitle, LocalDate dueDate);
    void sendReturnNotification(String memberEmail, String bookTitle, double lateFee);
    void sendOverdueNotification(String memberEmail, String bookTitle, LocalDate dueDate);
    void sendNotification(String memberEmail, String subject, String message);
}