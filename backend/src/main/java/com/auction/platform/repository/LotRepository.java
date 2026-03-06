package com.auction.platform.repository;

import com.auction.platform.domain.Lot;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LotRepository extends JpaRepository<Lot, Long> {

    // Кэшируемый запрос для каталога (активные лоты)
    @Query("SELECT l FROM Lot l WHERE l.status = 'ACTIVE' AND l.endTime > CURRENT_TIMESTAMP")
    Page<Lot> findActiveLots(Pageable pageable);

    // СТРОГАЯ БЛОКИРОВКА: Используется при обработке ставки авто-биддером.
    // Запрещает другим транзакциям читать/писать эту строку, пока мы не обновим цену лота.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lot l WHERE l.id = :id")
    Optional<Lot> findByIdWithPessimisticLock(@Param("id") Long id);
}