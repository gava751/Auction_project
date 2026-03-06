package com.auction.platform.pattern.strategy;
import java.math.BigDecimal;
public class StandardFeeStrategy implements FeeCalculationStrategy {
    @Override
    public BigDecimal calculateFee(BigDecimal finalPrice) {
        return finalPrice.multiply(new BigDecimal("0.05")); // 5% комиссия
    }
}
