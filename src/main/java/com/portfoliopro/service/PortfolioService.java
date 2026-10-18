package com.portfoliopro.service;

import com.portfoliopro.dto.*;
import com.portfoliopro.model.PortfolioItem;
import com.portfoliopro.model.Stock;
import com.portfoliopro.model.User;
import com.portfoliopro.repository.PortfolioItemRepository;
import com.portfoliopro.repository.StockRepository;
import com.portfoliopro.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioItemRepository portfolioItemRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public PortfolioService(PortfolioItemRepository portfolioItemRepository,
                            StockRepository stockRepository,
                            UserRepository userRepository) {
        this.portfolioItemRepository = portfolioItemRepository;
        this.stockRepository = stockRepository;
        this.userRepository = userRepository;
    }

    /**
     * Aggregates portfolio KPIs (Net Worth, Invested, Total P&L, Day P&L, Cash).
     */
    public PortfolioSummaryDTO getPortfolioSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<PortfolioItem> holdings = portfolioItemRepository.findByUserId(userId);

        double totalPortfolioValue = 0.0;
        double totalInvested = 0.0;
        double totalDayPnl = 0.0;

        for (PortfolioItem item : holdings) {
            Optional<Stock> stockOpt = stockRepository.findByTicker(item.getTicker());
            if (stockOpt.isPresent()) {
                Stock stock = stockOpt.get();
                double currentValue = item.getQuantity() * stock.getCurrentPrice();
                totalPortfolioValue += currentValue;
                totalInvested += item.getTotalInvested();
                totalDayPnl += item.getQuantity() * (stock.getCurrentPrice() - stock.getPreviousClose());
            }
        }

        totalPortfolioValue = Math.round(totalPortfolioValue * 100.0) / 100.0;
        totalInvested = Math.round(totalInvested * 100.0) / 100.0;
        double unrealizedPnl = Math.round((totalPortfolioValue - totalInvested) * 100.0) / 100.0;
        double unrealizedPnlPercent = totalInvested > 0 
                ? Math.round((unrealizedPnl / totalInvested * 100.0) * 100.0) / 100.0 
                : 0.0;

        double cashBalance = user.getCashBalance();
        double totalNetWorth = Math.round((cashBalance + totalPortfolioValue) * 100.0) / 100.0;

        double dayPnl = Math.round(totalDayPnl * 100.0) / 100.0;
        double prevTotalVal = totalPortfolioValue - dayPnl;
        double dayPnlPercent = prevTotalVal > 0 
                ? Math.round((dayPnl / prevTotalVal * 100.0) * 100.0) / 100.0 
                : 0.0;

        return new PortfolioSummaryDTO(
                totalPortfolioValue,
                totalInvested,
                unrealizedPnl,
                unrealizedPnlPercent,
                user.getTotalRealizedPnl(),
                cashBalance,
                totalNetWorth,
                dayPnl,
                dayPnlPercent,
                holdings.size()
        );
    }

    /**
     * Fetches detailed holdings list enriched with real-time stock quotes.
     */
    public List<HoldingDTO> getHoldings(Long userId) {
        List<PortfolioItem> holdings = portfolioItemRepository.findByUserId(userId);
        if (holdings.isEmpty()) {
            return Collections.emptyList();
        }

        double totalPortfolioValue = 0.0;
        Map<String, Stock> stockMap = new HashMap<>();

        for (PortfolioItem item : holdings) {
            stockRepository.findByTicker(item.getTicker()).ifPresent(stock -> {
                stockMap.put(item.getTicker(), stock);
            });
        }

        for (PortfolioItem item : holdings) {
            Stock stock = stockMap.get(item.getTicker());
            if (stock != null) {
                totalPortfolioValue += (item.getQuantity() * stock.getCurrentPrice());
            }
        }

        final double finalTotalVal = totalPortfolioValue;
        List<HoldingDTO> dtoList = new ArrayList<>();

        for (PortfolioItem item : holdings) {
            Stock stock = stockMap.get(item.getTicker());
            if (stock == null) continue;

            double currentPrice = stock.getCurrentPrice();
            double currentValue = Math.round((item.getQuantity() * currentPrice) * 100.0) / 100.0;
            double invested = item.getTotalInvested();
            double unrealizedPnl = Math.round((currentValue - invested) * 100.0) / 100.0;
            double pnlPercent = invested > 0 ? Math.round((unrealizedPnl / invested * 100.0) * 100.0) / 100.0 : 0.0;
            double dayChange = Math.round((currentPrice - stock.getPreviousClose()) * 100.0) / 100.0;
            double dayChangePercent = stock.getChangePercent();
            double weight = finalTotalVal > 0 ? Math.round((currentValue / finalTotalVal * 100.0) * 10.0) / 10.0 : 0.0;

            dtoList.add(new HoldingDTO(
                    stock.getTicker(),
                    stock.getCompanyName(),
                    stock.getSector(),
                    item.getQuantity(),
                    item.getAvgBuyPrice(),
                    currentPrice,
                    invested,
                    currentValue,
                    unrealizedPnl,
                    pnlPercent,
                    dayChange,
                    dayChangePercent,
                    weight
            ));
        }

        // Sort by current value descending
        dtoList.sort((a, b) -> Double.compare(b.getCurrentValue(), a.getCurrentValue()));
        return dtoList;
    }

    /**
     * Calculates portfolio exposure across industry sectors for allocation pie chart.
     */
    public List<SectorAllocationDTO> getSectorAllocation(Long userId) {
        List<HoldingDTO> holdings = getHoldings(userId);
        if (holdings.isEmpty()) {
            return Collections.emptyList();
        }

        double totalValue = holdings.stream().mapToDouble(HoldingDTO::getCurrentValue).sum();
        Map<String, List<HoldingDTO>> sectorMap = holdings.stream()
                .collect(Collectors.groupingBy(HoldingDTO::getSector));

        List<SectorAllocationDTO> allocations = new ArrayList<>();
        for (Map.Entry<String, List<HoldingDTO>> entry : sectorMap.entrySet()) {
            String sector = entry.getKey();
            double val = entry.getValue().stream().mapToDouble(HoldingDTO::getCurrentValue).sum();
            double roundedVal = Math.round(val * 100.0) / 100.0;
            double pct = totalValue > 0 ? Math.round((val / totalValue * 100.0) * 10.0) / 10.0 : 0.0;
            allocations.add(new SectorAllocationDTO(sector, roundedVal, pct, entry.getValue().size()));
        }

        allocations.sort((a, b) -> Double.compare(b.getTotalValue(), a.getTotalValue()));
        return allocations;
    }

    /**
     * Calculates institutional risk metrics (Sharpe ratio, beta, diversification score).
     */
    public RiskMetricsDTO getRiskMetrics(Long userId) {
        List<HoldingDTO> holdings = getHoldings(userId);
        if (holdings.isEmpty()) {
            return new RiskMetricsDTO(0.0, 1.0, 0.0, 0, "Not Available", 0.0, "Invest in equities to view risk analysis.");
        }

        // Sector concentration calculation (Herfindahl-Hirschman index equivalent)
        List<SectorAllocationDTO> sectors = getSectorAllocation(userId);
        double hhi = 0.0;
        for (SectorAllocationDTO s : sectors) {
            double share = s.getPercentage();
            hhi += (share * share);
        }

        // Diversification score from 10 to 100
        int diversificationScore = Math.max(10, Math.min(95, (int)(100 - (hhi / 100))));

        // Synthetic Beta: IT & Tech ~ 1.15, Banking ~ 1.05, Energy ~ 0.85, Auto ~ 1.10
        double weightedBeta = 0.0;
        double totalVal = holdings.stream().mapToDouble(HoldingDTO::getCurrentValue).sum();
        for (HoldingDTO h : holdings) {
            double stockBeta = switch (h.getSector().toUpperCase()) {
                case "INFORMATION TECHNOLOGY" -> 1.12;
                case "BANKING & FINANCIAL SERVICES" -> 1.05;
                case "ENERGY & OIL" -> 0.88;
                case "AUTOMOBILE" -> 1.15;
                case "CONGLOMERATE" -> 0.95;
                default -> 1.0;
            };
            weightedBeta += (stockBeta * (h.getCurrentValue() / totalVal));
        }
        weightedBeta = Math.round(weightedBeta * 100.0) / 100.0;

        // Portfolio Volatility & Sharpe Ratio
        double volatility = Math.round((14.5 * weightedBeta) * 10.0) / 10.0; // Annualized %
        double portfolioReturn = 16.8; // Estimated annualized return %
        double riskFreeRate = 6.5; // RBI benchmark 10Y repo yield
        double sharpeRatio = Math.round(((portfolioReturn - riskFreeRate) / volatility) * 100.0) / 100.0;

        // 95% Daily Value at Risk (VaR)
        double var95 = Math.round((totalVal * (volatility / 100.0 / Math.sqrt(252)) * 1.645) * 100.0) / 100.0;

        String rating = (weightedBeta < 0.95) ? "Conservative" : (weightedBeta <= 1.15) ? "Balanced / Moderate" : "Aggressive";
        String recommendation = diversificationScore >= 70 
                ? "Excellent diversification across multiple sectors. Healthy risk-reward profile." 
                : "Consider adding non-tech or defensive sectors (FMCG, Pharma) to improve risk balance.";

        return new RiskMetricsDTO(sharpeRatio, weightedBeta, volatility, diversificationScore, rating, var95, recommendation);
    }

    /**
     * Generates portfolio historical equity growth curve for visual analytics.
     */
    public List<PricePointDTO> getPortfolioPerformanceHistory(Long userId, String timeframe) {
        PortfolioSummaryDTO summary = getPortfolioSummary(userId);
        double currentTotal = summary.getTotalNetWorth();
        List<PricePointDTO> points = new ArrayList<>();
        Random random = new Random(userId + 42); // deterministic seed for stability

        int count = 15;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

        for (int i = count - 1; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i * 2);
            double progress = (double)(count - 1 - i) / (count - 1);
            double baseline = summary.getTotalInvested() > 0 ? (summary.getTotalInvested() + summary.getCashBalance()) : currentTotal;
            double interpolated = baseline + ((currentTotal - baseline) * progress);
            double noise = (random.nextDouble() - 0.48) * (currentTotal * 0.008);
            double val = Math.round((interpolated + noise) * 100.0) / 100.0;
            if (i == 0) val = currentTotal;

            points.add(new PricePointDTO(date.format(formatter), val, val, val, val, val, 0L));
        }

        return points;
    }
}
