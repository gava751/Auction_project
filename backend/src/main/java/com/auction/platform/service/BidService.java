package com.auction.platform.service;

import com.auction.platform.domain.AutoBid;
import com.auction.platform.domain.Bid;
import com.auction.platform.domain.Lot;
import com.auction.platform.domain.User;
import com.auction.platform.dto.BidRequest;
import com.auction.platform.exception.InvalidBidException;
import com.auction.platform.pattern.observer.AuctionNotifier;
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
    private final AuctionNotifier auctionNotifier;

    @Transactional
    public void placeManualBid(String userEmail, BidRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Lot lot = lotRepository.findByIdWithPessimisticLock(request.lotId())
                .orElseThrow(() -> new InvalidBidException("Лот не найден"));

        validateLotIsActive(lot);
        BigDecimal requiredMinBid = lot.getCurrentPrice().add(lot.getBidStep());
        if (request.amount().compareTo(requiredMinBid) < 0) {
            throw new InvalidBidException("Ставка должна быть не меньше " + requiredMinBid);
        }

        saveBidAndUpdateLot(lot, user, request.amount());

        processAutoBids(lot, user.getId());

        auctionNotifier.notifyObservers(lot);
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

        processAutoBids(lot, null);
        auctionNotifier.notifyObservers(lot);
    }

    private void processAutoBids(Lot lot, Long currentLeaderId) {
        List<AutoBid> autoBids = autoBidRepository.findByLotIdAndIsActiveTrueOrderByMaxLimitDesc(lot.getId());

        if (autoBids.isEmpty()) return;

        if (autoBids.size() == 1) {
            AutoBid topBidder = autoBids.get(0);
            if (!topBidder.getUser().getId().equals(currentLeaderId)) {
                BigDecimal neededAmount = lot.getCurrentPrice().add(lot.getBidStep());
                if (topBidder.getMaxLimit().compareTo(neededAmount) >= 0) {
                    saveBidAndUpdateLot(lot, topBidder.getUser(), neededAmount);
                } else {
                    topBidder.setIsActive(false);
                }
            }
            return;
        }

        AutoBid highest = autoBids.get(0);
        AutoBid secondHighest = autoBids.get(1);

        BigDecimal newPrice = secondHighest.getMaxLimit().add(lot.getBidStep());

        if (newPrice.compareTo(highest.getMaxLimit()) > 0) {
            newPrice = highest.getMaxLimit();
        }

        if (newPrice.compareTo(lot.getCurrentPrice()) > 0 && highest.getMaxLimit().compareTo(lot.getCurrentPrice()) > 0) {
            saveBidAndUpdateLot(lot, highest.getUser(), newPrice);
            log.info("Авто-биддер пользователя {} перебил цену до {}", highest.getUser().getId(), newPrice);
        }

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