package com.auction.platform.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bids")
@Getter
@Setter
public class Bid extends BaseEntity<Long> {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lot_id", nullable = false)
  private Lot lot;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private BigDecimal amount;
}
