package com.auction.platform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seller_applications")
@Getter @Setter
public class SellerApplication extends BaseEntity<Long> {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "passport_data", nullable = false)
    private String passportData;

    @Column(name = "document_path", nullable = false)
    private String documentPath;

    @Column(nullable = false)
    private String status = "PENDING";
}