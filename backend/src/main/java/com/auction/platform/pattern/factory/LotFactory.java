package com.auction.platform.pattern.factory;

import com.auction.platform.domain.Lot;
import com.auction.platform.dto.LotResponse;

public class LotFactory {
    public static LotResponse createResponse(Lot lot) {
        return new LotResponse(
                lot.getId(),
                lot.getTitle(),
                lot.getCurrentPrice(),
                null,
                lot.getEndTime(),
                lot.getStatus().name()
        );
    }
}