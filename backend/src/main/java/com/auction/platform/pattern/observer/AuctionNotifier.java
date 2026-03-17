package com.auction.platform.pattern.observer;

import com.auction.platform.domain.Lot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuctionNotifier {
    private final List<AuctionObserver> observers;

    public void notifyObservers(Lot lot) {
        for (AuctionObserver observer : observers) {
            observer.onBidPlaced(lot);
        }
    }
}