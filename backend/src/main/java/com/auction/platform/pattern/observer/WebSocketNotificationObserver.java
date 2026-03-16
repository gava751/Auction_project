package com.auction.platform.pattern.observer;

import com.auction.platform.domain.Lot;

public class WebSocketNotificationObserver implements AuctionObserver {
    @Override
    public void onBidPlaced(Lot lot, Long newWinnerId) {
        System.out.println("WebSocket: Ставка на лот " + lot.getId() + " обновлена!");
    }
}
