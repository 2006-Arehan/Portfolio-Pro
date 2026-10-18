package com.portfoliopro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * PortfolioItem Entity representing an equity holding in a user's portfolio.
 */
@Entity
@Table(name = "portfolio_items", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "ticker"})
})
public class PortfolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String ticker;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double avgBuyPrice;

    @Column(nullable = false)
    private Double totalInvested;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public PortfolioItem() {
    }

    public PortfolioItem(Long userId, String ticker, Integer quantity, Double avgBuyPrice) {
        this.userId = userId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.avgBuyPrice = avgBuyPrice;
        this.totalInvested = quantity * avgBuyPrice;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public Double getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public void setAvgBuyPrice(Double avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice;
        this.updatedAt = LocalDateTime.now();
    }

    public Double getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(Double totalInvested) {
        this.totalInvested = totalInvested;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
