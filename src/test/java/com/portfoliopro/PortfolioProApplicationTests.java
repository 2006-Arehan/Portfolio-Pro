package com.portfoliopro;

import com.portfoliopro.dto.OrderRequest;
import com.portfoliopro.dto.PortfolioSummaryDTO;
import com.portfoliopro.model.Stock;
import com.portfoliopro.model.Transaction;
import com.portfoliopro.model.User;
import com.portfoliopro.service.PortfolioService;
import com.portfoliopro.service.StockMarketService;
import com.portfoliopro.service.TradingService;
import com.portfoliopro.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PortfolioProApplicationTests {

    @Autowired
    private StockMarketService stockMarketService;

    @Autowired
    private TradingService tradingService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Context loads successfully")
    void contextLoads() {
        assertNotNull(stockMarketService);
        assertNotNull(tradingService);
        assertNotNull(portfolioService);
        assertNotNull(userService);
    }

    @Test
    @DisplayName("Verify Bluechip stocks database seeding includes HCLTECH")
    void verifyStocksSeeded() {
        List<Stock> stocks = stockMarketService.getAllStocks();
        assertFalse(stocks.isEmpty(), "Stocks list should not be empty");
        
        boolean hasHclTech = stocks.stream().anyMatch(s -> "HCLTECH".equalsIgnoreCase(s.getTicker()));
        assertTrue(hasHclTech, "HCLTECH equity stock should be present in seeded market database");
    }

    @Test
    @DisplayName("Verify user demo account initialization with capital")
    void verifyUserInitialization() {
        User user = userService.getOrCreateDefaultUser();
        assertNotNull(user);
        assertTrue(user.getCashBalance() > 0, "User should have available cash balance");
    }

    @Test
    @DisplayName("Verify Portfolio Summary calculation")
    void verifyPortfolioSummary() {
        User user = userService.getOrCreateDefaultUser();
        PortfolioSummaryDTO summary = portfolioService.getPortfolioSummary(user.getId());
        assertNotNull(summary);
        assertTrue(summary.getTotalPortfolioValue() > 0, "Portfolio value should be calculated");
        assertTrue(summary.getTotalNetWorth() > 0, "Net worth should be calculated");
    }

    @Test
    @DisplayName("Verify stock order execution - Buy and Sell lifecycle")
    void verifyOrderExecution() {
        User user = userService.getOrCreateDefaultUser();
        double initialCash = user.getCashBalance();

        // 1. Execute Buy order for 5 shares of HCLTECH
        OrderRequest buyRequest = new OrderRequest("HCLTECH", "BUY", "MARKET", 5, null);
        Transaction buyTx = tradingService.executeOrder(buyRequest, user.getId());
        assertNotNull(buyTx);
        assertEquals("BUY", buyTx.getOrderType());
        assertEquals("EXECUTED", buyTx.getStatus());
        assertEquals(5, buyTx.getQuantity());

        // Refresh user
        User afterBuyUser = userService.getOrCreateDefaultUser();
        assertTrue(afterBuyUser.getCashBalance() < initialCash, "Cash balance should decrease after Buy");

        // 2. Execute Sell order for 5 shares of HCLTECH
        OrderRequest sellRequest = new OrderRequest("HCLTECH", "SELL", "MARKET", 5, null);
        Transaction sellTx = tradingService.executeOrder(sellRequest, user.getId());
        assertNotNull(sellTx);
        assertEquals("SELL", sellTx.getOrderType());
        assertEquals("EXECUTED", sellTx.getStatus());
    }

    @Autowired
    private com.portfoliopro.service.AuthService authService;

    @Test
    @DisplayName("Verify user registration and authentication lifecycle")
    void verifyAuthenticationLifecycle() {
        String testUser = "hcl_trader_" + System.currentTimeMillis();
        com.portfoliopro.dto.RegisterRequest regReq = new com.portfoliopro.dto.RegisterRequest(
                testUser, "Test Trader", testUser + "@hcl.com", "Password@123"
        );
        com.portfoliopro.dto.AuthResponse regRes = authService.register(regReq);
        assertNotNull(regRes);
        assertNotNull(regRes.getToken());
        assertEquals(testUser, regRes.getUser().getUsername());
        assertEquals(1000000.00, regRes.getUser().getInitialCapital());

        // Test Login
        com.portfoliopro.dto.AuthRequest loginReq = new com.portfoliopro.dto.AuthRequest(testUser, "Password@123");
        com.portfoliopro.dto.AuthResponse loginRes = authService.login(loginReq);
        assertNotNull(loginRes);
        assertNotNull(loginRes.getToken());
        assertEquals(testUser, loginRes.getUser().getUsername());
    }
}
