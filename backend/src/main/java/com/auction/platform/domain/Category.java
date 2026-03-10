package com.auction.platform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter @Setter
public class Category extends BaseEntity<Long> {

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}