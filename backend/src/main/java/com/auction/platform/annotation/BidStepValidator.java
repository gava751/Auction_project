package com.auction.platform.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class BidStepValidator implements ConstraintValidator<ValidBidStep, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotNull должен обрабатывать проверку на null отдельно
        }
        return value.scale() <= 2;
    }
}