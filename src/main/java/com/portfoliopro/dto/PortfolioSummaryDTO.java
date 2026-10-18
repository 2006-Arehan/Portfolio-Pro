package com.portfoliopro.dto;

/**
 * Summary metrics of the user's trading portfolio.
 */
public class PortfolioSummaryDTO {
    private Double totalPortfolioValue;
    private Double totalInvested;
    private Double unrealizedPnl;
    private Double unrealizedPnlPercent;
    private Double totalRealizedPnl;
    private Double cashBalance;
    private Double totalNetWorth;
    private Double dayPnl;
    private Double dayPnlPercent;
    private Integer holdingsCount;

    public PortfolioSummaryDTO() {
    }

    public PortfolioSummaryDTO(Double totalPortfolioValue, Double totalInvested, Double unrealizedPnl, 
                               Double unrealizedPnlPercent, Double totalRealizedPnl, Double cashBalance, 
                               Double totalNetWorth, Double dayPnl, Double dayPnlPercent, Integer holdingsCount) {
        this.totalPortfolioValue = totalPortfolioValue;
        this.totalInvested = totalInvested;
        this.unrealizedPnl = unrealizedPnl;
        this.unrealizedPnlPercent = unrealizedPnlPercent;
        this.totalRealizedPnl = totalRealizedPnl;
        this.cashBalance = cashBalance;
        this.totalNetWorth = totalNetWorth;
        this.dayPnl = dayPnl;
        this.dayPnlPercent = dayPnlPercent;
        this.holdingsCount = holdingsCount;
    }

    public Double getTotalPortfolioValue() {
        return totalPortfolioValue;
    }

    public void setTotalPortfolioValue(Double totalPortfolioValue) {
        this.totalPortfolioValue = totalPortfolioValue;
    }

    public Double getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(Double totalInvested) {
        this.totalInvested = totalInvested;
    }

    public Double getUnrealizedPnl() {
        return unrealizedPnl;
    }

    public void setUnrealizedPnl(Double unrealizedPnl) {
        this.unrealizedPnl = unrealizedPnl;
    }

    public Double getUnrealizedPnlPercent() {
        return unrealizedPnlPercent;
    }

    public void setUnrealizedPnlPercent(Double unrealizedPnlPercent) {
        this.unrealizedPnlPercent = unrealizedPnlPercent;
    }

    public Double getTotalRealizedPnl() {
        return totalRealizedPnl;
    }

    public void setTotalRealizedPnl(Double totalRealizedPnl) {
        this.totalRealizedPnl = totalRealizedPnl;
    }

    public Double getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(Double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public Double getTotalNetWorth() {
        return totalNetWorth;
    }

    public void setTotalNetWorth(Double totalNetWorth) {
        this.totalNetWorth = totalNetWorth;
    }

    public Double getDayPnl() {
        return dayPnl;
    }

    public void setDayPnl(Double dayPnl) {
        this.dayPnl = dayPnl;
    }

    public Double getDayPnlPercent() {
        return dayPnlPercent;
    }

    public void setDayPnlPercent(Double dayPnlPercent) {
        this.dayPnlPercent = dayPnlPercent;
    }

    public Integer getHoldingsCount() {
        return holdingsCount;
    }

    public void setHoldingsCount(Integer holdingsCount) {
        this.holdingsCount = holdingsCount;
    }
}
