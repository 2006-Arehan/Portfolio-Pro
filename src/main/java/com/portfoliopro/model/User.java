package com.portfoliopro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * User Entity representing an investor/trader in PortfolioPro.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 120)
    private String passwordHash = "demo123_hash";

    @Column(nullable = false)
    private Double cashBalance;

    @Column(nullable = false)
    private Double initialCapital;

    @Column(nullable = false)
    private Double totalRealizedPnl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public User() {
    }

    public User(String username, String fullName, String email, String passwordHash, Double initialCapital) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.initialCapital = initialCapital;
        this.cashBalance = initialCapital;
        this.totalRealizedPnl = 0.0;
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String fullName, String email, Double initialCapital) {
        this(username, fullName, email, "demo123_hash", initialCapital);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Double getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(Double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public Double getInitialCapital() {
        return initialCapital;
    }

    public void setInitialCapital(Double initialCapital) {
        this.initialCapital = initialCapital;
    }

    public Double getTotalRealizedPnl() {
        return totalRealizedPnl;
    }

    public void setTotalRealizedPnl(Double totalRealizedPnl) {
        this.totalRealizedPnl = totalRealizedPnl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
