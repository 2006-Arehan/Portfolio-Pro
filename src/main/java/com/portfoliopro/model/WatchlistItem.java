package com.portfoliopro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * WatchlistItem Entity representing a pinned stock in user's watchlist.
 */
@Entity
@Table(name = "watchlist_items", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "ticker"})
})
public class WatchlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String ticker;

    @Column(nullable = false)
    private LocalDateTime addedAt;

    public WatchlistItem() {
    }

    public WatchlistItem(Long userId, String ticker) {
        this.userId = userId;
        this.ticker = ticker;
        this.addedAt = LocalDateTime.now();
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

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
