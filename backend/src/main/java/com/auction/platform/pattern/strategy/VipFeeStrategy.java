package com.auction.platform.pattern.strategy;

import java.math.BigDecimal;

public class VipFeeStrategy implements FeeCalculationStrategy {
    @Override
    public BigDecimal calculateFee(BigDecimal finalPrice) {
        return finalPrice.multiply(new BigDecimal("0.02")); // 2% комиссия
    }
}
