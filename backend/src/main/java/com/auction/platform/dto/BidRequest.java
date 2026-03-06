package com.auction.platform.dto;

import com.auction.platform.annotation.ValidBidStep;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BidRequest(
        @NotNull(message = "ID лота обязателен")
        Long lotId,

        @NotNull(message = "Сумма ставки обязательна")
        @DecimalMin(value = "0.01", message = "Ставка должна быть больше нуля")
        @ValidBidStep
        BigDecimal amount
) {}