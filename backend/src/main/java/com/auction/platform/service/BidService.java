package com.auction.platform.service;

import com.auction.platform.domain.AutoBid;
import com.auction.platform.domain.Bid;
import com.auction.platform.domain.Lot;
import com.auction.platform.domain.User;
import com.auction.platform.dto.BidRequest;
import com.auction.platform.exception.InvalidBidException;
import com.auction.platform.repository.AutoBidRepository;
import com.auction.platform.repository.BidRepository;
import com.auction.platform.repository.LotRepository;
import com.auction.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BidService {

    private final LotRepository lotRepository;
    private final BidRepository bidRepository;
    private final AutoBidRepository autoBidRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher; // Для паттерна Observer (WebSockets)

    @Transactional
    public void placeManualBid(String userEmail, BidRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 1. ПЕССИМИСТИЧНАЯ БЛОКИРОВКА: Блокируем лот для других транзакций
        Lot lot = lotRepository.findByIdWithPessimisticLock(request.lotId())
                .orElseThrow(() -> new InvalidBidException("Лот не найден"));

        // 2. Валидация бизнес-правил
        validateLotIsActive(lot);
        BigDecimal requiredMinBid = lot.getCurrentPrice().add(lot.getBidStep());
        if (request.amount().compareTo(requiredMinBid) < 0) {
            throw new InvalidBidException("Ставка должна быть не меньше " + requiredMinBid);
        }

        // 3. Сохранение ручной ставки
        saveBidAndUpdateLot(lot, user, request.amount());

        // 4. Триггер Авто-биддера (Сражение лимитов)
        processAutoBids(lot, user.getId());

        // 5. Оповещение слушателей (WebSocket, Email)
        eventPublisher.publishEvent(lot);
    }

    @Transactional
    public void setupAutoBid(String userEmail, Long lotId, BigDecimal maxLimit) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Lot lot = lotRepository.findByIdWithPessimisticLock(lotId).orElseThrow();

        validateLotIsActive(lot);

        AutoBid autoBid = autoBidRepository.findByLotIdAndUserId(lotId, user.getId())
                .orElse(new AutoBid());
        autoBid.setLot(lot);
        autoBid.setUser(user);
        autoBid.setMaxLimit(maxLimit);
        autoBid.setIsActive(true);
        autoBidRepository.save(autoBid);

        // Сразу запускаем проверку, может ли новый авто-бид перебить текущую цену
        processAutoBids(lot, null);
        eventPublisher.publishEvent(lot);
    }

    /**
     * Ядро логики прокси-ставок (как на eBay).
     */
    private void processAutoBids(Lot lot, Long currentLeaderId) {
        // Подтягиваем все активные авто-ставки для лота (отсортированы по убыванию лимита)
        List<AutoBid> autoBids = autoBidRepository.findByLotIdAndIsActiveTrueOrderByMaxLimitDesc(lot.getId());

        if (autoBids.isEmpty()) return;

        // Если есть только один авто-биддер, и он не текущий лидер
        if (autoBids.size() == 1) {
            AutoBid topBidder = autoBids.get(0);
            if (!topBidder.getUser().getId().equals(currentLeaderId)) {
                BigDecimal neededAmount = lot.getCurrentPrice().add(lot.getBidStep());
                if (topBidder.getMaxLimit().compareTo(neededAmount) >= 0) {
                    saveBidAndUpdateLot(lot, topBidder.getUser(), neededAmount);
                } else {
                    topBidder.setIsActive(false); // Лимит исчерпан
                }
            }
            return;
        }

        // Если авто-биддеров 2 и более, они "сражаются"
        AutoBid highest = autoBids.get(0);
        AutoBid secondHighest = autoBids.get(1);

        // Если текущий лидер уже обладает самым высоким авто-бидом, он делает минимальный шаг над вторым местом
        BigDecimal newPrice = secondHighest.getMaxLimit().add(lot.getBidStep());

        // Ограничиваем новую цену максимальным лимитом победителя
        if (newPrice.compareTo(highest.getMaxLimit()) > 0) {
            newPrice = highest.getMaxLimit();
        }

        // Если новая цена больше текущей, обновляем лидера
        if (newPrice.compareTo(lot.getCurrentPrice()) > 0 && highest.getMaxLimit().compareTo(lot.getCurrentPrice()) > 0) {
            saveBidAndUpdateLot(lot, highest.getUser(), newPrice);
            log.info("Авто-биддер пользователя {} перебил цену до {}", highest.getUser().getId(), newPrice);
        }

        // Деактивируем проигравшего
        secondHighest.setIsActive(false);
    }

    private void saveBidAndUpdateLot(Lot lot, User user, BigDecimal amount) {
        Bid bid = new Bid();
        bid.setLot(lot);
        bid.setUser(user);
        bid.setAmount(amount);
        bidRepository.save(bid);

        lot.setCurrentPrice(amount);
        lotRepository.save(lot);
    }

    private void validateLotIsActive(Lot lot) {
        if (!lot.getStatus().name().equals("ACTIVE") || lot.getEndTime().isBefore(LocalDateTime.now())) {
            throw new InvalidBidException("Торги по данному лоту завершены");
        }
    }
}