package com.auction.platform.pattern.observer;

import com.auction.platform.domain.Lot;
import com.auction.platform.dto.LotUpdateMessage;
import com.auction.platform.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketNotificationObserver implements AuctionObserver {

    private final SimpMessagingTemplate messagingTemplate;
    private final BidRepository bidRepository;

    @Override
    public void onBidPlaced(Lot lot) {
        String lastBidder = bidRepository.findTopByLotIdOrderByAmountDesc(lot.getId())
                .map(bid -> bid.getUser().getEmail())
                .orElse("Ставок нет");

        LotUpdateMessage update = new LotUpdateMessage(
                lot.getId(), lot.getCurrentPrice(), lastBidder, lot.getEndTime(), lot.getStatus().name()
        );

        messagingTemplate.convertAndSend("/topic/lot/" + lot.getId(), update);
        messagingTemplate.convertAndSend("/topic/lots", update);
    }
}