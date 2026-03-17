package com.auction.platform.pattern.observer;

import com.auction.platform.domain.Lot;
import com.auction.platform.domain.Notification;
import com.auction.platform.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseNotificationObserver implements AuctionObserver {

    private final NotificationRepository notificationRepository;

    @Override
    public void onBidPlaced(Lot lot) {
        Notification notif = new Notification();
        notif.setUserId(lot.getSellerId());
        notif.setMessage("Ставка на ваш лот обновлена: " + lot.getCurrentPrice());
        notif.setType("UPDATE");
        notificationRepository.save(notif);
    }
}