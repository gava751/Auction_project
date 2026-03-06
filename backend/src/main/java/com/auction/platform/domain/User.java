package com.auction.platform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "users")
@Getter @Setter
public class User extends BaseEntity<Long> {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String role; // ROLE_BUYER, ROLE_SELLER, ROLE_ADMIN

    @Column(nullable = false)
    private String status; // ACTIVE, BANNED
}