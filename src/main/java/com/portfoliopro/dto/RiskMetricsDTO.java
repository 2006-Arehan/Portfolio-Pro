package com.portfoliopro.dto;

/**
 * Advanced risk analytics and financial indicators for portfolio health assessment.
 */
public class RiskMetricsDTO {
    private Double sharpeRatio;
    private Double portfolioBeta;
    private Double volatility;
    private Integer diversificationScore; // 0 to 100
    private String riskRating;
    private Double valueAtRisk95; // 95% 1-day VaR
    private String recommendation;

    public RiskMetricsDTO() {
    }

    public RiskMetricsDTO(Double sharpeRatio, Double portfolioBeta, Double volatility, 
                          Integer diversificationScore, String riskRating, Double valueAtRisk95, 
                          String recommendation) {
        this.sharpeRatio = sharpeRatio;
        this.portfolioBeta = portfolioBeta;
        this.volatility = volatility;
        this.diversificationScore = diversificationScore;
        this.riskRating = riskRating;
        this.valueAtRisk95 = valueAtRisk95;
        this.recommendation = recommendation;
    }

    public Double getSharpeRatio() {
        return sharpeRatio;
    }

    public void setSharpeRatio(Double sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    public Double getPortfolioBeta() {
        return portfolioBeta;
    }

    public void setPortfolioBeta(Double portfolioBeta) {
        this.portfolioBeta = portfolioBeta;
    }

    public Double getVolatility() {
        return volatility;
    }

    public void setVolatility(Double volatility) {
        this.volatility = volatility;
    }

    public Integer getDiversificationScore() {
        return diversificationScore;
    }

    public void setDiversificationScore(Integer diversificationScore) {
        this.diversificationScore = diversificationScore;
    }

    public String getRiskRating() {
        return riskRating;
    }

    public void setRiskRating(String riskRating) {
        this.riskRating = riskRating;
    }

    public Double getValueAtRisk95() {
        return valueAtRisk95;
    }

    public void setValueAtRisk95(Double valueAtRisk95) {
        this.valueAtRisk95 = valueAtRisk95;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
