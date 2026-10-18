package com.portfoliopro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Transaction Entity representing an executed buy or sell stock order.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String ticker;

    @Column(nullable = false, length = 150)
    private String companyName;

    @Column(nullable = false, length = 10)
    private String orderType; // BUY, SELL

    @Column(nullable = false, length = 10)
    private String orderCategory; // MARKET, LIMIT

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double executionPrice;

    @Column(nullable = false)
    private Double grossAmount;

    @Column(nullable = false)
    private Double brokerage;

    @Column(nullable = false)
    private Double sttTax; // Securities Transaction Tax (0.1%)

    @Column(nullable = false)
    private Double gstTax; // 18% on Brokerage

    @Column(nullable = false)
    private Double netAmount;

    @Column(nullable = false, length = 20)
    private String status; // EXECUTED, REJECTED, CANCELLED

    @Column(nullable = false)
    private LocalDateTime executedAt;

    public Transaction() {
    }

    public Transaction(String orderId, Long userId, String ticker, String companyName, 
                       String orderType, String orderCategory, Integer quantity, 
                       Double executionPrice, Double grossAmount, Double brokerage, 
                       Double sttTax, Double gstTax, Double netAmount, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.ticker = ticker;
        this.companyName = companyName;
        this.orderType = orderType;
        this.orderCategory = orderCategory;
        this.quantity = quantity;
        this.executionPrice = executionPrice;
        this.grossAmount = grossAmount;
        this.brokerage = brokerage;
        this.sttTax = sttTax;
        this.gstTax = gstTax;
        this.netAmount = netAmount;
        this.status = status;
        this.executedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public Double getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(Double executionPrice) {
        this.executionPrice = executionPrice;
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

    public Double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
}
