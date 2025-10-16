package edu.trincoll.service.fee;

import org.springframework.stereotype.Component;

/**
 * Premium member late fee strategy implementation.
 * Premium members don't pay late fees.
 */
@Component
public class PremiumLateFeeStrategy implements LateFeeStrategy {
    @Override
    public double calculateLateFee(long daysLate) {
        return 0.0;
    }
}