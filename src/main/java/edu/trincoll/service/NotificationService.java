package edu.trincoll.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service responsible for sending notifications.
 * Follows Single Responsibility Principle - handles only notification operations.
 * Follows Dependency Inversion Principle - depends on abstraction, not concrete implementation.
 */
@Service
public class NotificationService implements INotificationService {

    /**
     * Send checkout notification to member
     */
    public void sendCheckoutNotification(String memberEmail, String bookTitle, LocalDate dueDate) {
        System.out.println("Sending email to: " + memberEmail);
        System.out.println("Subject: Book checked out");
        System.out.println("Message: You have checked out " + bookTitle + ". Due date: " + dueDate);
        
        // In a real application, this would integrate with an email service
        // For now, we're just printing to console as per the original implementation
    }

    /**
     * Send return notification to member
     */
    public void sendReturnNotification(String memberEmail, String bookTitle, double lateFee) {
        System.out.println("Sending email to: " + memberEmail);
        System.out.println("Subject: Book returned");
        
        String message = "You have returned " + bookTitle;
        if (lateFee > 0) {
            message += ". Late fee: $" + String.format("%.2f", lateFee);
        }
        
        System.out.println("Message: " + message);
        
        // In a real application, this would integrate with an email service
        // For now, we're just printing to console as per the original implementation
    }

    /**
     * Send overdue notification to member
     */
    public void sendOverdueNotification(String memberEmail, String bookTitle, LocalDate dueDate) {
        System.out.println("Sending email to: " + memberEmail);
        System.out.println("Subject: Book overdue");
        System.out.println("Message: Your book " + bookTitle + " was due on " + dueDate + " and is now overdue.");
        
        // In a real application, this would integrate with an email service
    }

    /**
     * Send general notification to member
     */
    public void sendNotification(String memberEmail, String subject, String message) {
        System.out.println("Sending email to: " + memberEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        
        // In a real application, this would integrate with an email service
    }
}
