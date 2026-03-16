package com.auction.platform.repository;

import com.auction.platform.domain.AutoBid;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutoBidRepository extends JpaRepository<AutoBid, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<AutoBid> findByLotIdAndIsActiveTrueOrderByMaxLimitDesc(Long lotId);

    Optional<AutoBid> findByLotIdAndUserId(Long lotId, Long userId);
}