package edu.trincoll.service.fee;

import org.springframework.stereotype.Component;

/**
 * Student member late fee strategy implementation.
 * Charges $0.25 per day late.
 */
@Component
public class StudentLateFeeStrategy implements LateFeeStrategy {
    private static final double DAILY_RATE = 0.25;

    @Override
    public double calculateLateFee(long daysLate) {
        return daysLate * DAILY_RATE;
    }
}