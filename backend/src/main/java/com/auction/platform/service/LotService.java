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

@Service
@RequiredArgsConstructor
public class LotService {

    private final LotRepository lotRepository;

    @Transactional(readOnly = true)
    // Кэшируем результаты первой страницы каталога для снижения нагрузки на БД
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
        // Дополнительные проверки бизнес-логики (например, дата завершения в будущем)
        Lot savedLot = lotRepository.save(lot);
        return LotFactory.createResponse(savedLot);
    }

    @Transactional(readOnly = true)
    public Lot getLotEntityById(Long lotId) {
        return lotRepository.findById(lotId)
                .orElseThrow(() -> new AuctionException("Лот не найден: " + lotId) {});
    }
}