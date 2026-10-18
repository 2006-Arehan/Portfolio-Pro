package com.portfoliopro.repository;

import com.portfoliopro.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByTicker(String ticker);
    Optional<Stock> findByTickerIgnoreCase(String ticker);
    List<Stock> findBySector(String sector);
    List<Stock> findByTickerContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(String ticker, String companyName);
    List<Stock> findAllByOrderByMarketCapDesc();

    @Query("SELECT s FROM Stock s ORDER BY ((s.currentPrice - s.previousClose) / s.previousClose) DESC")
    List<Stock> findTopGainers();

    @Query("SELECT s FROM Stock s ORDER BY ((s.currentPrice - s.previousClose) / s.previousClose) ASC")
    List<Stock> findTopLosers();
}
