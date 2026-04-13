package com.auction.platform.repository;

import com.auction.platform.domain.Bid;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findTopByLotIdOrderByAmountDesc(Long lotId);
}