package com.auction.platform.service;

import com.auction.platform.domain.Lot;
import com.auction.platform.domain.LotStatus;
import com.auction.platform.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionScheduler {

    private final LotRepository lotRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void closeExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();

        List<Lot> expiredLots = lotRepository.findByStatusAndEndTimeBefore(LotStatus.ACTIVE, now);

        if (!expiredLots.isEmpty()) {
            log.info("Найдено {} лотов для закрытия", expiredLots.size());

            for (Lot lot : expiredLots) {
                lot.setStatus(LotStatus.COMPLETED);
                lotRepository.save(lot);
            }
        }
    }
}