package com.portfoliopro.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for placing Buy or Sell stock orders.
 */
public class OrderRequest {

    @NotBlank(message = "Stock ticker symbol is required")
    private String ticker;

    @NotBlank(message = "Order type is required (BUY or SELL)")
    private String orderType; // BUY or SELL

    private String orderCategory = "MARKET"; // MARKET or LIMIT

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private Double limitPrice;

    public OrderRequest() {
    }

    public OrderRequest(String ticker, String orderType, String orderCategory, Integer quantity, Double limitPrice) {
        this.ticker = ticker;
        this.orderType = orderType;
        this.orderCategory = orderCategory;
        this.quantity = quantity;
        this.limitPrice = limitPrice;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(Double limitPrice) {
        this.limitPrice = limitPrice;
    }
}
