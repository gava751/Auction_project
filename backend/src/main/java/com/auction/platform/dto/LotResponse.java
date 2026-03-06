package com.auction.platform.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LotResponse(
        Long id,
        String title,
        BigDecimal currentPrice,
        LocalDateTime endTime,
        String status
) {}