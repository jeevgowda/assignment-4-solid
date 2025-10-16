package edu.trincoll.service.fee;

import edu.trincoll.model.MembershipType;
import org.springframework.stereotype.Component;

/**
 * Factory for creating appropriate late fee strategy based on membership type.
 * This follows the Open-Closed Principle by allowing new strategies to be added
 * without modifying existing code.
 */
@Component
public class LateFeeStrategyFactory {
    private final RegularLateFeeStrategy regularStrategy;
    private final PremiumLateFeeStrategy premiumStrategy;
    private final StudentLateFeeStrategy studentStrategy;

    public LateFeeStrategyFactory(RegularLateFeeStrategy regularStrategy,
                                 PremiumLateFeeStrategy premiumStrategy,
                                 StudentLateFeeStrategy studentStrategy) {
        this.regularStrategy = regularStrategy;
        this.premiumStrategy = premiumStrategy;
        this.studentStrategy = studentStrategy;
    }

    public LateFeeStrategy getStrategy(MembershipType membershipType) {
        return switch (membershipType) {
            case REGULAR -> regularStrategy;
            case PREMIUM -> premiumStrategy;
            case STUDENT -> studentStrategy;
        };
    }
}