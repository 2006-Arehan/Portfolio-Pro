package com.portfoliopro.service;

import com.portfoliopro.dto.PricePointDTO;
import com.portfoliopro.model.Stock;
import com.portfoliopro.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StockMarketService {

    private static final Logger logger = LoggerFactory.getLogger(StockMarketService.class);
    private final StockRepository stockRepository;
    private final Random random = new Random();

    public StockMarketService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAllByOrderByMarketCapDesc();
    }

    public Optional<Stock> getStockByTicker(String ticker) {
        return stockRepository.findByTickerIgnoreCase(ticker);
    }

    public List<Stock> getTopGainers() {
        List<Stock> all = stockRepository.findAll();
        all.sort((a, b) -> Double.compare(b.getChangePercent(), a.getChangePercent()));
        return all.subList(0, Math.min(5, all.size()));
    }

    public List<Stock> getTopLosers() {
        List<Stock> all = stockRepository.findAll();
        all.sort((a, b) -> Double.compare(a.getChangePercent(), b.getChangePercent()));
        return all.subList(0, Math.min(5, all.size()));
    }

    public List<Stock> searchStocks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStocks();
        }
        return stockRepository.findByTickerContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(query.trim(), query.trim());
    }

    /**
     * Simulates real-time market price movements using a geometric Brownian motion simulation.
     * Fires automatically every 4 seconds to mimic exchange price feed ticks.
     */
    @Scheduled(fixedRate = 4000)
    public void simulateMarketTick() {
        List<Stock> stocks = stockRepository.findAll();
        if (stocks.isEmpty()) {
            return;
        }

        // Randomly select 3 to 6 stocks to fluctuate each tick for realistic market volatility
        int countToUpdate = Math.min(stocks.size(), 3 + random.nextInt(4));
        Collections.shuffle(stocks);

        for (int i = 0; i < countToUpdate; i++) {
            Stock stock = stocks.get(i);
            // Fluctuation percentage between -0.45% and +0.45%
            double deltaPercent = (random.nextDouble() * 0.90) - 0.44;
            double currentPrice = stock.getCurrentPrice();
            double newPrice = currentPrice * (1.0 + (deltaPercent / 100.0));
            newPrice = Math.round(newPrice * 100.0) / 100.0;

            if (newPrice < 1.0) newPrice = 1.0;

            stock.setCurrentPrice(newPrice);
            if (newPrice > stock.getDayHigh()) {
                stock.setDayHigh(newPrice);
            }
            if (newPrice < stock.getDayLow()) {
                stock.setDayLow(newPrice);
            }
            // Increment volume
            stock.setVolume(stock.getVolume() + (long)(50 + random.nextInt(350)));

            stockRepository.save(stock);
        }
    }

    /**
     * Generates realistic historical price points for charting (1D, 1W, 1M, 1Y).
     */
    public List<PricePointDTO> getStockHistory(String ticker, String timeframe) {
        Optional<Stock> stockOpt = stockRepository.findByTickerIgnoreCase(ticker);
        if (stockOpt.isEmpty()) {
            return Collections.emptyList();
        }

        Stock stock = stockOpt.get();
        double currentPrice = stock.getCurrentPrice();
        List<PricePointDTO> points = new ArrayList<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");

        int intervals = 30;
        double volatility = 0.015;

        switch (timeframe.toUpperCase()) {
            case "1D":
                intervals = 25;
                volatility = 0.006;
                LocalDateTime baseTime = LocalDateTime.now().minusHours(6);
                double simPrice1D = stock.getOpenPrice();
                for (int i = 0; i < intervals; i++) {
                    LocalDateTime pointTime = baseTime.plusMinutes(i * 15);
                    double shift = (random.nextDouble() - 0.48) * volatility * simPrice1D;
                    simPrice1D = Math.max(1.0, simPrice1D + shift);
                    if (i == intervals - 1) simPrice1D = currentPrice;
                    double rounded = Math.round(simPrice1D * 100.0) / 100.0;
                    points.add(new PricePointDTO(pointTime.format(timeFormatter), rounded, rounded - 1, rounded + 2, rounded - 2, rounded, (long)(1000 + random.nextInt(5000))));
                }
                break;

            case "1W":
                intervals = 7;
                volatility = 0.02;
                for (int i = intervals - 1; i >= 0; i--) {
                    LocalDateTime pointDate = LocalDateTime.now().minusDays(i);
                    double drift = (1.0 - (i * 0.003)) + ((random.nextDouble() - 0.5) * volatility);
                    double p = Math.round(currentPrice * drift * 100.0) / 100.0;
                    if (i == 0) p = currentPrice;
                    points.add(new PricePointDTO(pointDate.format(dateFormatter), p, p - 3, p + 5, p - 4, p, (long)(50000 + random.nextInt(20000))));
                }
                break;

            case "1M":
                intervals = 30;
                volatility = 0.035;
                for (int i = intervals - 1; i >= 0; i--) {
                    LocalDateTime pointDate = LocalDateTime.now().minusDays(i);
                    double drift = (1.0 - (i * 0.002)) + ((random.nextDouble() - 0.49) * volatility);
                    double p = Math.round(currentPrice * drift * 100.0) / 100.0;
                    if (i == 0) p = currentPrice;
                    points.add(new PricePointDTO(pointDate.format(dateFormatter), p, p - 5, p + 8, p - 6, p, (long)(40000 + random.nextInt(30000))));
                }
                break;

            case "1Y":
            default:
                intervals = 12;
                volatility = 0.05;
                DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
                for (int i = intervals - 1; i >= 0; i--) {
                    LocalDateTime pointDate = LocalDateTime.now().minusMonths(i);
                    double drift = (0.85 + ( (12 - i) * 0.012 )) + ((random.nextDouble() - 0.5) * volatility);
                    double p = Math.round(currentPrice * drift * 100.0) / 100.0;
                    if (i == 0) p = currentPrice;
                    points.add(new PricePointDTO(pointDate.format(monthFormatter), p, p - 10, p + 15, p - 12, p, (long)(200000 + random.nextInt(100000))));
                }
                break;
        }

        return points;
    }
}
