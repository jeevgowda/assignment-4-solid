package edu.trincoll.service.fee;

/**
 * Strategy interface for calculating late fees.
 * This interface follows the Open-Closed Principle by allowing new fee calculation
 * strategies to be added without modifying existing code.
 */
public interface LateFeeStrategy {
    /**
     * Calculate the late fee based on the number of days late
     * @param daysLate number of days the book is overdue
     * @return the calculated late fee
     */
    double calculateLateFee(long daysLate);
}