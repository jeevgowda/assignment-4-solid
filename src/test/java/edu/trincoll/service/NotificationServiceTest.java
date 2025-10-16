package edu.trincoll.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Notification Service Tests")
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Should send checkout notification")
    void shouldSendCheckoutNotification() {
        // Arrange
        String memberEmail = "test@example.com";
        String bookTitle = "Clean Code";
        LocalDate dueDate = LocalDate.now().plusDays(14);

        // Act & Assert - No exceptions should be thrown
        assertDoesNotThrow(() -> {
            notificationService.sendCheckoutNotification(memberEmail, bookTitle, dueDate);
        });
    }

    @Test
    @DisplayName("Should send return notification without late fee")
    void shouldSendReturnNotificationWithoutLateFee() {
        // Arrange
        String memberEmail = "test@example.com";
        String bookTitle = "Clean Code";
        double lateFee = 0.0;

        // Act & Assert - No exceptions should be thrown
        assertDoesNotThrow(() -> {
            notificationService.sendReturnNotification(memberEmail, bookTitle, lateFee);
        });
    }

    @Test
    @DisplayName("Should send return notification with late fee")
    void shouldSendReturnNotificationWithLateFee() {
        // Arrange
        String memberEmail = "test@example.com";
        String bookTitle = "Clean Code";
        double lateFee = 2.50;

        // Act & Assert - No exceptions should be thrown
        assertDoesNotThrow(() -> {
            notificationService.sendReturnNotification(memberEmail, bookTitle, lateFee);
        });
    }

    @Test
    @DisplayName("Should send overdue notification")
    void shouldSendOverdueNotification() {
        // Arrange
        String memberEmail = "test@example.com";
        String bookTitle = "Clean Code";
        LocalDate dueDate = LocalDate.now().minusDays(5);

        // Act & Assert - No exceptions should be thrown
        assertDoesNotThrow(() -> {
            notificationService.sendOverdueNotification(memberEmail, bookTitle, dueDate);
        });
    }

    @Test
    @DisplayName("Should send general notification")
    void shouldSendGeneralNotification() {
        // Arrange
        String memberEmail = "test@example.com";
        String subject = "Library Notice";
        String message = "This is a test message";

        // Act & Assert - No exceptions should be thrown
        assertDoesNotThrow(() -> {
            notificationService.sendNotification(memberEmail, subject, message);
        });
    }

    @Test
    @DisplayName("Should handle null parameters gracefully")
    void shouldHandleNullParametersGracefully() {
        // Act & Assert - No exceptions should be thrown even with null parameters
        assertDoesNotThrow(() -> {
            notificationService.sendCheckoutNotification(null, null, null);
        });

        assertDoesNotThrow(() -> {
            notificationService.sendReturnNotification(null, null, 0.0);
        });

        assertDoesNotThrow(() -> {
            notificationService.sendOverdueNotification(null, null, null);
        });

        assertDoesNotThrow(() -> {
            notificationService.sendNotification(null, null, null);
        });
    }

    @Test
    @DisplayName("Should handle empty strings gracefully")
    void shouldHandleEmptyStringsGracefully() {
        // Arrange
        String emptyEmail = "";
        String emptyTitle = "";
        LocalDate dueDate = LocalDate.now();

        // Act & Assert - No exceptions should be thrown
        assertDoesNotThrow(() -> {
            notificationService.sendCheckoutNotification(emptyEmail, emptyTitle, dueDate);
        });

        assertDoesNotThrow(() -> {
            notificationService.sendReturnNotification(emptyEmail, emptyTitle, 0.0);
        });

        assertDoesNotThrow(() -> {
            notificationService.sendOverdueNotification(emptyEmail, emptyTitle, dueDate);
        });

        assertDoesNotThrow(() -> {
            notificationService.sendNotification(emptyEmail, emptyEmail, emptyEmail);
        });
    }

    @Test
    @DisplayName("Should handle negative late fee")
    void shouldHandleNegativeLateFee() {
        // Arrange
        String memberEmail = "test@example.com";
        String bookTitle = "Clean Code";
        double negativeLateFee = -1.0;

        // Act & Assert - No exceptions should be thrown
        assertDoesNotThrow(() -> {
            notificationService.sendReturnNotification(memberEmail, bookTitle, negativeLateFee);
        });
    }

    @Test
    @DisplayName("Should handle large late fee")
    void shouldHandleLargeLateFee() {
        // Arrange
        String memberEmail = "test@example.com";
        String bookTitle = "Clean Code";
        double largeLateFee = 999.99;

        // Act & Assert - No exceptions should be thrown
        assertDoesNotThrow(() -> {
            notificationService.sendReturnNotification(memberEmail, bookTitle, largeLateFee);
        });
    }
}
