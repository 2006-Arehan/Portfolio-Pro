package com.portfoliopro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stock Entity representing a financial equity instrument traded on the exchange.
 */
@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String ticker;

    @Column(nullable = false, length = 150)
    private String companyName;

    @Column(nullable = false, length = 80)
    private String sector;

    @Column(nullable = false)
    private Double currentPrice;

    @Column(nullable = false)
    private Double previousClose;

    @Column(nullable = false)
    private Double openPrice;

    @Column(nullable = false)
    private Double dayHigh;

    @Column(nullable = false)
    private Double dayLow;

    @Column(nullable = false)
    private Long volume;

    @Column(nullable = false)
    private Double marketCap; // in INR Crores

    @Column(nullable = false)
    private Double peRatio;

    @Column(nullable = false)
    private Double dividendYield; // percentage e.g. 2.45

    @Column(nullable = false)
    private Double fiftyTwoWeekHigh;

    @Column(nullable = false)
    private Double fiftyTwoWeekLow;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Stock() {
    }

    public Stock(String ticker, String companyName, String sector, Double currentPrice, 
                 Double previousClose, Double openPrice, Double dayHigh, Double dayLow, 
                 Long volume, Double marketCap, Double peRatio, Double dividendYield, 
                 Double fiftyTwoWeekHigh, Double fiftyTwoWeekLow) {
        this.ticker = ticker;
        this.companyName = companyName;
        this.sector = sector;
        this.currentPrice = currentPrice;
        this.previousClose = previousClose;
        this.openPrice = openPrice;
        this.dayHigh = dayHigh;
        this.dayLow = dayLow;
        this.volume = volume;
        this.marketCap = marketCap;
        this.peRatio = peRatio;
        this.dividendYield = dividendYield;
        this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
        this.fiftyTwoWeekLow = fiftyTwoWeekLow;
        this.updatedAt = LocalDateTime.now();
    }

    // Calculated helper methods
    public Double getChange() {
        if (previousClose == null || previousClose == 0.0) return 0.0;
        return Math.round((currentPrice - previousClose) * 100.0) / 100.0;
    }

    public Double getChangePercent() {
        if (previousClose == null || previousClose == 0.0) return 0.0;
        return Math.round(((currentPrice - previousClose) / previousClose * 100.0) * 100.0) / 100.0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = Math.round(currentPrice * 100.0) / 100.0;
        this.updatedAt = LocalDateTime.now();
    }

    public Double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(Double previousClose) {
        this.previousClose = previousClose;
    }

    public Double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Double openPrice) {
        this.openPrice = openPrice;
    }

    public Double getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(Double dayHigh) {
        this.dayHigh = dayHigh;
    }

    public Double getDayLow() {
        return dayLow;
    }

    public void setDayLow(Double dayLow) {
        this.dayLow = dayLow;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(Double marketCap) {
        this.marketCap = marketCap;
    }

    public Double getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(Double peRatio) {
        this.peRatio = peRatio;
    }

    public Double getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(Double dividendYield) {
        this.dividendYield = dividendYield;
    }

    public Double getFiftyTwoWeekHigh() {
        return fiftyTwoWeekHigh;
    }

    public void setFiftyTwoWeekHigh(Double fiftyTwoWeekHigh) {
        this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
    }

    public Double getFiftyTwoWeekLow() {
        return fiftyTwoWeekLow;
    }

    public void setFiftyTwoWeekLow(Double fiftyTwoWeekLow) {
        this.fiftyTwoWeekLow = fiftyTwoWeekLow;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
