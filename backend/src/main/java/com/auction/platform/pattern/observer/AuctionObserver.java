package com.auction.platform.pattern.observer;

import com.auction.platform.domain.Lot;

public interface AuctionObserver {
    void onBidPlaced(Lot lot);
}