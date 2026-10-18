package com.portfoliopro.dto;

/**
 * DTO for pre-trade financial computation including taxes, brokerage, and margin check.
 */
public class QuoteCalculationDTO {
    private String ticker;
    private String companyName;
    private String orderType;
    private Integer quantity;
    private Double unitPrice;
    private Double grossAmount;
    private Double brokerage;
    private Double sttTax;
    private Double gstTax;
    private Double netTotal;
    private Double availableBalance;
    private boolean sufficientMargin;

    public QuoteCalculationDTO() {
    }

    public QuoteCalculationDTO(String ticker, String companyName, String orderType, Integer quantity, 
                               Double unitPrice, Double grossAmount, Double brokerage, Double sttTax, 
                               Double gstTax, Double netTotal, Double availableBalance, boolean sufficientMargin) {
        this.ticker = ticker;
        this.companyName = companyName;
        this.orderType = orderType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.grossAmount = grossAmount;
        this.brokerage = brokerage;
        this.sttTax = sttTax;
        this.gstTax = gstTax;
        this.netTotal = netTotal;
        this.availableBalance = availableBalance;
        this.sufficientMargin = sufficientMargin;
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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(Double grossAmount) {
        this.grossAmount = grossAmount;
    }

    public Double getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(Double brokerage) {
        this.brokerage = brokerage;
    }

    public Double getSttTax() {
        return sttTax;
    }

    public void setSttTax(Double sttTax) {
        this.sttTax = sttTax;
    }

    public Double getGstTax() {
        return gstTax;
    }

    public void setGstTax(Double gstTax) {
        this.gstTax = gstTax;
    }

    public Double getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(Double netTotal) {
        this.netTotal = netTotal;
    }

    public Double getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(Double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public boolean isSufficientMargin() {
        return sufficientMargin;
    }

    public void setSufficientMargin(boolean sufficientMargin) {
        this.sufficientMargin = sufficientMargin;
    }
}
