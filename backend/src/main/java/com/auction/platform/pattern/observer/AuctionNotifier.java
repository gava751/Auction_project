package com.auction.platform.pattern.observer;

import com.auction.platform.domain.Lot;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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