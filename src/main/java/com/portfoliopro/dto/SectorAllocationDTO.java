package com.portfoliopro.dto;

/**
 * DTO for sector-wise portfolio distribution.
 */
public class SectorAllocationDTO {
    private String sector;
    private Double totalValue;
    private Double percentage;
    private Integer stockCount;

    public SectorAllocationDTO() {
    }

    public SectorAllocationDTO(String sector, Double totalValue, Double percentage, Integer stockCount) {
        this.sector = sector;
        this.totalValue = totalValue;
        this.percentage = percentage;
        this.stockCount = stockCount;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }
}
