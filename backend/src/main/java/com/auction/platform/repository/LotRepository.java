package com.auction.platform.repository;

import com.auction.platform.domain.Lot;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LotRepository extends JpaRepository<Lot, Long> {

    @Query("SELECT l FROM Lot l WHERE l.status = 'ACTIVE' AND l.endTime > CURRENT_TIMESTAMP " +
            "AND (:categoryId IS NULL OR l.categoryId = :categoryId) " +
            "AND (:search IS NULL OR LOWER(l.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY CASE WHEN :userId IS NOT NULL AND EXISTS (SELECT 1 FROM Bid b WHERE b.lot.id = l.id AND b.user.id = :userId) THEN 0 ELSE 1 END ASC, l.endTime ASC")
    Page<Lot> findActiveLotsWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("search") String search,
            @Param("userId") Long userId,
            Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lot l WHERE l.id = :id")
    Optional<Lot> findByIdWithPessimisticLock(@Param("id") Long id);

    List<Lot> findBySellerId(Long sellerId);

    List<com.auction.platform.domain.Lot> findByStatusAndEndTimeBefore(
            com.auction.platform.domain.LotStatus status,
            java.time.LocalDateTime dateTime
    );
    @Query("SELECT l FROM Lot l WHERE l.status = 'COMPLETED' " +
            "AND EXISTS (SELECT b FROM Bid b WHERE b.lot.id = l.id AND b.user.id = :userId " +
            "AND b.amount = l.currentPrice)")
    List<Lot> findWonLots(@Param("userId") Long userId);
}