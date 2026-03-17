package com.auction.platform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lots")
@Getter
@Setter
@NoArgsConstructor
public class Lot extends BaseEntity<Long> {
    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_price", nullable = false)
    private BigDecimal startPrice;

    @Column(name = "current_price", nullable = false)
    private BigDecimal currentPrice;

    @Column(name = "bid_step", nullable = false)
    private BigDecimal bidStep;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false)
    private LotStatus status;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Version
    private Integer version;

    public Lot(Long sellerId, Long categoryId, String title, BigDecimal startPrice, BigDecimal bidStep, LocalDateTime endTime) {
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.title = title;
        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        this.bidStep = bidStep;
        this.endTime = endTime;
        this.status = LotStatus.ACTIVE;
    }
}