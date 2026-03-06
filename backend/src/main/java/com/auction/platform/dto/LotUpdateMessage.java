package com.auction.platform.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LotUpdateMessage(
        Long lotId,
        BigDecimal currentPrice,
        String lastBidderEmail,
        LocalDateTime endTime,
        String status
) {}