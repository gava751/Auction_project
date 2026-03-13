package com.auction.platform.listener;

import com.auction.platform.domain.Lot;
import com.auction.platform.domain.Notification;
import com.auction.platform.dto.LotUpdateMessage;
import com.auction.platform.repository.BidRepository;
import com.auction.platform.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final BidRepository bidRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleLotUpdate(Lot lot) {
        log.info("Рассылка обновления по лоту ID: {}", lot.getId());

        String lastBidder = bidRepository.findTopByLotIdOrderByAmountDesc(lot.getId())
                .map(bid -> bid.getUser().getEmail())
                .orElse("Ставок нет");

        LotUpdateMessage update = new LotUpdateMessage(
                lot.getId(),
                lot.getCurrentPrice(),
                lastBidder,
                lot.getEndTime(),
                lot.getStatus().name()
        );

        // Рассылка в WebSockets
        messagingTemplate.convertAndSend("/topic/lot/" + lot.getId(), update);
        messagingTemplate.convertAndSend("/topic/lots", update);

        // Сохранение уведомления в БД
        Notification notif = new Notification();
        notif.setUserId(lot.getSellerId());
        notif.setMessage("Ставка на ваш лот обновлена: " + lot.getCurrentPrice());
        notif.setType("UPDATE");
        notificationRepository.save(notif);
    }
}