package com.portfoliopro.service;

import com.portfoliopro.model.Stock;
import com.portfoliopro.model.WatchlistItem;
import com.portfoliopro.repository.StockRepository;
import com.portfoliopro.repository.WatchlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final StockRepository stockRepository;

    public WatchlistService(WatchlistRepository watchlistRepository, StockRepository stockRepository) {
        this.watchlistRepository = watchlistRepository;
        this.stockRepository = stockRepository;
    }

    public List<Stock> getWatchlistStocks(Long userId) {
        List<WatchlistItem> items = watchlistRepository.findByUserIdOrderByAddedAtDesc(userId);
        List<Stock> stocks = new ArrayList<>();
        for (WatchlistItem item : items) {
            stockRepository.findByTicker(item.getTicker()).ifPresent(stocks::add);
        }
        return stocks;
    }

    @Transactional
    public boolean toggleWatchlist(Long userId, String ticker) {
        String cleanTicker = ticker.toUpperCase().trim();
        Optional<WatchlistItem> existing = watchlistRepository.findByUserIdAndTicker(userId, cleanTicker);
        if (existing.isPresent()) {
            watchlistRepository.delete(existing.get());
            return false; // Removed
        } else {
            watchlistRepository.save(new WatchlistItem(userId, cleanTicker));
            return true; // Added
        }
    }

    public boolean isWatched(Long userId, String ticker) {
        return watchlistRepository.existsByUserIdAndTicker(userId, ticker.toUpperCase().trim());
    }
}
