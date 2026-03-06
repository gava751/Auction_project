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
@Getter // Lombok: автоматически создаст getTitle(), getId() и т.д.
@Setter // Lombok: автоматически создаст сеттеры
@NoArgsConstructor // Lombok: создаст пустой конструктор для JPA
public class Lot extends BaseEntity<Long> {

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_price", nullable = false)
    private BigDecimal startPrice;

    @Column(name = "current_price", nullable = false)
    private BigDecimal currentPrice;

    @Column(name = "bid_step", nullable = false)
    private BigDecimal bidStep;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false)
    private LotStatus status;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Version
    private Integer version; // Optimistic Locking

    public Lot(Long sellerId, String title, BigDecimal startPrice, BigDecimal bidStep, LocalDateTime endTime) {
        this.sellerId = sellerId;
        this.title = title;
        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        this.bidStep = bidStep;
        this.endTime = endTime;
        this.status = LotStatus.ACTIVE;
    }
}