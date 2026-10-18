package com.portfoliopro.dto;

/**
 * Time-series point for rendering price charts and candlesticks.
 */
public class PricePointDTO {
    private String time;
    private Double price;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Long volume;

    public PricePointDTO() {
    }

    public PricePointDTO(String time, Double price, Double open, Double high, Double low, Double close, Long volume) {
        this.time = time;
        this.price = price;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
