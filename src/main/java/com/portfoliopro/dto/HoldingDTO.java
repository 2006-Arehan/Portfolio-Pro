package com.portfoliopro.dto;

/**
 * Enriched holding view combining portfolio items with real-time stock pricing.
 */
public class HoldingDTO {
    private String ticker;
    private String companyName;
    private String sector;
    private Integer quantity;
    private Double avgBuyPrice;
    private Double currentPrice;
    private Double investedAmount;
    private Double currentValue;
    private Double unrealizedPnl;
    private Double unrealizedPnlPercent;
    private Double dayChange;
    private Double dayChangePercent;
    private Double portfolioWeightPercent;

    public HoldingDTO() {
    }

    public HoldingDTO(String ticker, String companyName, String sector, Integer quantity, 
                      Double avgBuyPrice, Double currentPrice, Double investedAmount, 
                      Double currentValue, Double unrealizedPnl, Double unrealizedPnlPercent, 
                      Double dayChange, Double dayChangePercent, Double portfolioWeightPercent) {
        this.ticker = ticker;
        this.companyName = companyName;
        this.sector = sector;
        this.quantity = quantity;
        this.avgBuyPrice = avgBuyPrice;
        this.currentPrice = currentPrice;
        this.investedAmount = investedAmount;
        this.currentValue = currentValue;
        this.unrealizedPnl = unrealizedPnl;
        this.unrealizedPnlPercent = unrealizedPnlPercent;
        this.dayChange = dayChange;
        this.dayChangePercent = dayChangePercent;
        this.portfolioWeightPercent = portfolioWeightPercent;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public void setAvgBuyPrice(Double avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getInvestedAmount() {
        return investedAmount;
    }

    public void setInvestedAmount(Double investedAmount) {
        this.investedAmount = investedAmount;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
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

    public Double getDayChange() {
        return dayChange;
    }

    public void setDayChange(Double dayChange) {
        this.dayChange = dayChange;
    }

    public Double getDayChangePercent() {
        return dayChangePercent;
    }

    public void setDayChangePercent(Double dayChangePercent) {
        this.dayChangePercent = dayChangePercent;
    }

    public Double getPortfolioWeightPercent() {
        return portfolioWeightPercent;
    }

    public void setPortfolioWeightPercent(Double portfolioWeightPercent) {
        this.portfolioWeightPercent = portfolioWeightPercent;
    }
}
