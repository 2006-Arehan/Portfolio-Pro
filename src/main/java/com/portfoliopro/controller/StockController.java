package com.portfoliopro.controller;

import com.portfoliopro.dto.ApiResponse;
import com.portfoliopro.dto.PricePointDTO;
import com.portfoliopro.model.Stock;
import com.portfoliopro.service.StockMarketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*")
public class StockController {

    private final StockMarketService stockMarketService;

    public StockController(StockMarketService stockMarketService) {
        this.stockMarketService = stockMarketService;
    }

    @GetMapping
    public ApiResponse<List<Stock>> getAllStocks() {
        return ApiResponse.success("Stocks retrieved successfully", stockMarketService.getAllStocks());
    }

    @GetMapping("/{ticker}")
    public ApiResponse<Stock> getStockByTicker(@PathVariable String ticker) {
        return stockMarketService.getStockByTicker(ticker)
                .map(stock -> ApiResponse.success("Stock details found", stock))
                .orElse(ApiResponse.error("Stock ticker not found: " + ticker));
    }

    @GetMapping("/{ticker}/history")
    public ApiResponse<List<PricePointDTO>> getStockHistory(
            @PathVariable String ticker,
            @RequestParam(defaultValue = "1D") String timeframe) {
        return ApiResponse.success("Price history retrieved", stockMarketService.getStockHistory(ticker, timeframe));
    }

    @GetMapping("/top-gainers")
    public ApiResponse<List<Stock>> getTopGainers() {
        return ApiResponse.success("Top gainers retrieved", stockMarketService.getTopGainers());
    }

    @GetMapping("/top-losers")
    public ApiResponse<List<Stock>> getTopLosers() {
        return ApiResponse.success("Top losers retrieved", stockMarketService.getTopLosers());
    }

    @GetMapping("/search")
    public ApiResponse<List<Stock>> searchStocks(@RequestParam(required = false, defaultValue = "") String q) {
        return ApiResponse.success("Search results retrieved", stockMarketService.searchStocks(q));
    }
}
