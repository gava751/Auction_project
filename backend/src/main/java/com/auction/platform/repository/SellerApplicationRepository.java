package com.auction.platform.repository;

import com.auction.platform.domain.SellerApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SellerApplicationRepository extends JpaRepository<SellerApplication, Long> {
    List<SellerApplication> findByStatus(String status);
    boolean existsByUserIdAndStatus(Long userId, String status);
}