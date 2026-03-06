package com.auction.platform.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BidStepValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBidStep {
    String message() default "Неверный формат ставки (слишком много знаков после запятой)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}