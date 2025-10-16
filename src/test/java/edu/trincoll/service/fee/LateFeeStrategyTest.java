package edu.trincoll.service.fee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.trincoll.model.MembershipType;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LateFeeStrategyTest {

    @Autowired
    private LateFeeStrategyFactory lateFeeStrategyFactory;

    @Test
    void testRegularLateFeeStrategy() {
        LateFeeStrategy strategy = lateFeeStrategyFactory.getStrategy(MembershipType.REGULAR);
        assertEquals(2.50, strategy.calculateLateFee(5), 0.01);
        assertEquals(5.00, strategy.calculateLateFee(10), 0.01);
        assertEquals(0.00, strategy.calculateLateFee(0), 0.01);
    }

    @Test
    void testPremiumLateFeeStrategy() {
        LateFeeStrategy strategy = lateFeeStrategyFactory.getStrategy(MembershipType.PREMIUM);
        assertEquals(0.00, strategy.calculateLateFee(5), 0.01);
        assertEquals(0.00, strategy.calculateLateFee(10), 0.01);
        assertEquals(0.00, strategy.calculateLateFee(0), 0.01);
    }

    @Test
    void testStudentLateFeeStrategy() {
        LateFeeStrategy strategy = lateFeeStrategyFactory.getStrategy(MembershipType.STUDENT);
        assertEquals(1.25, strategy.calculateLateFee(5), 0.01);
        assertEquals(2.50, strategy.calculateLateFee(10), 0.01);
        assertEquals(0.00, strategy.calculateLateFee(0), 0.01);
    }

    @Test
    void testStrategyFactoryReturnsCorrectImplementations() {
        assertTrue(lateFeeStrategyFactory.getStrategy(MembershipType.REGULAR) instanceof RegularLateFeeStrategy);
        assertTrue(lateFeeStrategyFactory.getStrategy(MembershipType.PREMIUM) instanceof PremiumLateFeeStrategy);
        assertTrue(lateFeeStrategyFactory.getStrategy(MembershipType.STUDENT) instanceof StudentLateFeeStrategy);
    }
}