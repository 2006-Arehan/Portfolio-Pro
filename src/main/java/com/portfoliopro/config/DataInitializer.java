package com.portfoliopro.config;

import com.portfoliopro.model.Stock;
import com.portfoliopro.repository.StockRepository;
import com.portfoliopro.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final StockRepository stockRepository;
    private final UserService userService;

    public DataInitializer(StockRepository stockRepository, UserService userService) {
        this.stockRepository = stockRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (stockRepository.count() == 0) {
            logger.info("Initializing blue-chip stock equities market database...");

            List<Stock> stocks = List.of(
                    // Flagship HCL Stock
                    new Stock("HCLTECH", "HCL Technologies Ltd.", "Information Technology",
                            1785.40, 1752.10, 1755.00, 1798.50, 1750.20, 3425000L, 484500.0, 28.4, 2.92, 1860.0, 1140.0),

                    // Top IT Peers
                    new Stock("TCS", "Tata Consultancy Services Ltd.", "Information Technology",
                            4215.00, 4180.50, 4190.00, 4240.00, 4175.00, 1820000L, 1525000.0, 31.8, 1.45, 4592.0, 3310.0),
                    new Stock("INFY", "Infosys Ltd.", "Information Technology",
                            1892.50, 1870.00, 1875.00, 1910.00, 1868.00, 4520000L, 785000.0, 27.2, 2.10, 1991.0, 1358.0),
                    new Stock("WIPRO", "Wipro Ltd.", "Information Technology",
                            532.80, 528.40, 530.00, 538.50, 526.00, 2980000L, 278000.0, 23.5, 0.95, 580.0, 375.0),

                    // Banking & Finance
                    new Stock("HDFCBANK", "HDFC Bank Ltd.", "Banking & Financial Services",
                            1682.30, 1668.00, 1670.00, 1695.00, 1662.00, 8950000L, 1280000.0, 19.6, 1.15, 1794.0, 1363.0),
                    new Stock("ICICIBANK", "ICICI Bank Ltd.", "Banking & Financial Services",
                            1235.60, 1222.00, 1225.00, 1245.00, 1218.00, 6800000L, 870000.0, 18.2, 0.85, 1300.0, 915.0),
                    new Stock("SBIN", "State Bank of India", "Banking & Financial Services",
                            848.20, 856.40, 855.00, 862.00, 842.00, 11200000L, 756000.0, 11.4, 1.62, 912.0, 555.0),

                    // Conglomerate / Energy
                    new Stock("RELIANCE", "Reliance Industries Ltd.", "Energy & Conglomerate",
                            2955.00, 2920.00, 2925.00, 2978.00, 2915.00, 5600000L, 1998000.0, 26.5, 0.35, 3217.0, 2220.0),

                    // Automobile
                    new Stock("TATAMOTORS", "Tata Motors Ltd.", "Automobile",
                            968.40, 955.00, 958.00, 978.00, 952.00, 7400000L, 355000.0, 10.8, 0.62, 1179.0, 615.0),
                    new Stock("MARUTI", "Maruti Suzuki India Ltd.", "Automobile",
                            12480.00, 12350.00, 12380.00, 12590.00, 12310.00, 420000L, 392000.0, 27.4, 1.00, 13680.0, 9500.0),

                    // FMCG & Infrastructure
                    new Stock("ITC", "ITC Ltd.", "FMCG",
                            498.20, 502.10, 501.00, 505.00, 496.00, 9100000L, 622000.0, 29.1, 2.75, 528.0, 399.0),
                    new Stock("LT", "Larsen & Toubro Ltd.", "Engineering & Infrastructure",
                            3672.00, 3640.00, 3650.00, 3710.00, 3635.00, 1350000L, 504000.0, 37.6, 0.92, 3919.0, 2870.0),
                    new Stock("BHARTIARTL", "Bharti Airtel Ltd.", "Telecommunications",
                            1655.00, 1638.00, 1642.00, 1668.00, 1635.00, 3800000L, 985000.0, 65.2, 0.50, 1779.0, 898.0),

                    // Global High-Tech Equities
                    new Stock("AAPL", "Apple Inc.", "Global Technology",
                            19450.00, 19300.00, 19350.00, 19600.00, 19280.00, 850000L, 2980000.0, 33.4, 0.55, 20100.0, 15500.0),
                    new Stock("MSFT", "Microsoft Corp.", "Global Technology",
                            36850.00, 36500.00, 36600.00, 37150.00, 36450.00, 620000L, 2750000.0, 35.1, 0.72, 38900.0, 27800.0),
                    new Stock("NVDA", "NVIDIA Corporation", "Global AI & Semiconductors",
                            11850.00, 11520.00, 11600.00, 12100.00, 11500.00, 1450000L, 2890000.0, 48.6, 0.08, 12800.0, 3900.0)
            );

            stockRepository.saveAll(stocks);
            logger.info("Successfully seeded {} stock equities.", stocks.size());
        }

        // Initialize default user and initial holdings if needed
        var user = userService.getOrCreateDefaultUser();
        if (!userService.hasHoldings(user.getId())) {
            userService.resetDemoAccount(user.getId());
            logger.info("PortfolioPro initialization complete. Default demo account ready with ₹10,00,000 capital.");
        } else {
            logger.info("PortfolioPro ready. Existing demo account found with active holdings.");
        }
    }
}
