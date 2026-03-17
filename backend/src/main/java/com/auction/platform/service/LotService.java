package com.auction.platform.service;

import com.auction.platform.domain.Lot;
import com.auction.platform.dto.LotResponse;
import com.auction.platform.exception.AuctionException;
import com.auction.platform.pattern.factory.LotFactory;
import com.auction.platform.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.auction.platform.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LotService {

    private final LotRepository lotRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "activeLots", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<LotResponse> getActiveLots(Pageable pageable) {
        return lotRepository.findActiveLots(pageable)
                .map(LotFactory::createResponse);
    }

    @Transactional
    public void deleteLot(Long id) {
        lotRepository.deleteById(id);
    }

    @Transactional
    public LotResponse createLot(Lot lot) {
        if (lot.getCurrentPrice() == null) {
            lot.setCurrentPrice(lot.getStartPrice());
        }
        Lot savedLot = lotRepository.save(lot);
        return LotFactory.createResponse(savedLot);
    }

    @Transactional
    public void deleteLot(Long id, String sellerEmail) {
        Lot lot = lotRepository.findById(id).orElseThrow();
        if (!lot.getSellerId().equals(userRepository.findByEmail(sellerEmail).get().getId())) {
            throw new RuntimeException("Нет прав на удаление этого лота");
        }
        lotRepository.delete(lot);
    }

    @Transactional(readOnly = true)
    public Lot getLotEntityById(Long lotId) {
        return lotRepository.findById(lotId)
                .orElseThrow(() -> new AuctionException("Лот не найден: " + lotId) {});
    }
}