package com.auction.platform.listener;

import com.auction.platform.domain.Lot;
import com.auction.platform.dto.LotUpdateMessage;
import com.auction.platform.repository.BidRepository;
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

    @EventListener
    public void handleLotUpdate(Lot lot) {
        log.info("Рассылка обновления по лоту ID: {}", lot.getId());

        // Получаем последнего победителя для отображения в UI
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

        // Отправляем в конкретный топик лота (например, /topic/lot/5)
        messagingTemplate.convertAndSend("/topic/lot/" + lot.getId(), update);

        // Также отправляем в общий топик каталога для обновления цен в списке
        messagingTemplate.convertAndSend("/topic/lots", update);
    }
}