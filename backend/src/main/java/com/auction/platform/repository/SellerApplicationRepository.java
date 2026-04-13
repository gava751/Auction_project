package com.auction.platform.repository;

import com.auction.platform.domain.SellerApplication;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerApplicationRepository extends JpaRepository<SellerApplication, Long> {
  List<SellerApplication> findByStatus(String status);

  boolean existsByUserIdAndStatus(Long userId, String status);
}
