package com.auction.platform.pattern.strategy;
import java.math.BigDecimal;
@FunctionalInterface
public interface FeeCalculationStrategy {
    BigDecimal calculateFee(BigDecimal finalPrice);
}
