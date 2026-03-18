package com.auction.platform.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LotResponse(
        Long id,
        String title,
        java.math.BigDecimal currentPrice,
        java.math.BigDecimal eurPrice,
        java.time.LocalDateTime endTime,
        String status,
        String imagePath,
        String winnerEmail
) {}