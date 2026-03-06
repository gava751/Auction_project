package com.auction.platform.repository;

import com.auction.platform.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findTopByLotIdOrderByAmountDesc(Long lotId);
}