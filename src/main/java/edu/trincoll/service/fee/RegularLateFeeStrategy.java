package edu.trincoll.service.fee;

import org.springframework.stereotype.Component;

/**
 * Regular member late fee strategy implementation.
 * Charges $0.50 per day late.
 */
@Component
public class RegularLateFeeStrategy implements LateFeeStrategy {
    private static final double DAILY_RATE = 0.50;

    @Override
    public double calculateLateFee(long daysLate) {
        return daysLate * DAILY_RATE;
    }
}