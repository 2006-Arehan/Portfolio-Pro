package com.portfoliopro.service;

import com.portfoliopro.model.PortfolioItem;
import com.portfoliopro.model.Transaction;
import com.portfoliopro.model.User;
import com.portfoliopro.model.WatchlistItem;
import com.portfoliopro.repository.PortfolioItemRepository;
import com.portfoliopro.repository.TransactionRepository;
import com.portfoliopro.repository.UserRepository;
import com.portfoliopro.repository.WatchlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final TransactionRepository transactionRepository;
    private final WatchlistRepository watchlistRepository;

    public UserService(UserRepository userRepository,
                       PortfolioItemRepository portfolioItemRepository,
                       TransactionRepository transactionRepository,
                       WatchlistRepository watchlistRepository) {
        this.userRepository = userRepository;
        this.portfolioItemRepository = portfolioItemRepository;
        this.transactionRepository = transactionRepository;
        this.watchlistRepository = watchlistRepository;
    }

    public boolean hasHoldings(Long userId) {
        return !portfolioItemRepository.findByUserId(userId).isEmpty();
    }

    public User getOrCreateDefaultUser() {
        return userRepository.findByUsername("hcl_student_demo").orElseGet(() -> {
            User newUser = new User(
                    "hcl_student_demo",
                    "Aarav Sharma (HCL Candidate)",
                    "aarav.sharma@hclproject.edu",
                    1000000.00 // ₹10,00,000 Initial Virtual Cash
            );
            return userRepository.save(newUser);
        });
    }

    /**
     * Resets the demo account to clean state for live viva demonstrations.
     */
    @Transactional
    public User resetDemoAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Clear existing transactions, portfolio items, watchlists for this user and flush
        portfolioItemRepository.deleteAll(portfolioItemRepository.findByUserId(userId));
        portfolioItemRepository.flush();

        transactionRepository.deleteAll(transactionRepository.findByUserIdOrderByExecutedAtDesc(userId));
        transactionRepository.flush();

        watchlistRepository.deleteAll(watchlistRepository.findByUserIdOrderByAddedAtDesc(userId));
        watchlistRepository.flush();

        // Reset balance to ₹10 Lakhs and PnL to 0
        user.setCashBalance(1000000.00);
        user.setInitialCapital(1000000.00);
        user.setTotalRealizedPnl(0.0);
        userRepository.saveAndFlush(user);

        // Seed initial portfolio for immediate viva presentation
        seedInitialDemoHoldings(userId);

        return user;
    }

    private void seedInitialDemoHoldings(Long userId) {
        // Holding 1: HCLTECH (The flagship holding for HCL evaluation!)
        portfolioItemRepository.save(new PortfolioItem(userId, "HCLTECH", 75, 1720.50));
        // Holding 2: TCS
        portfolioItemRepository.save(new PortfolioItem(userId, "TCS", 25, 4150.00));
        // Holding 3: RELIANCE
        portfolioItemRepository.save(new PortfolioItem(userId, "RELIANCE", 40, 2850.00));
        // Holding 4: HDFCBANK
        portfolioItemRepository.save(new PortfolioItem(userId, "HDFCBANK", 60, 1620.00));
        // Holding 5: TATAMOTORS
        portfolioItemRepository.save(new PortfolioItem(userId, "TATAMOTORS", 80, 930.00));

        // Deduct invested amount from cash
        double invested = (75 * 1720.50) + (25 * 4150.00) + (40 * 2850.00) + (60 * 1620.00) + (80 * 930.00);
        User user = userRepository.findById(userId).get();
        user.setCashBalance(Math.round((user.getCashBalance() - invested) * 100.0) / 100.0);
        userRepository.save(user);

        // Add to watchlist
        watchlistRepository.save(new WatchlistItem(userId, "HCLTECH"));
        watchlistRepository.save(new WatchlistItem(userId, "INFY"));
        watchlistRepository.save(new WatchlistItem(userId, "NVDA"));

        // Add sample seed transactions with user-specific unique order IDs
        transactionRepository.save(new Transaction(
                "ORD-SEED-" + userId + "-001", userId, "HCLTECH", "HCL Technologies Ltd.",
                "BUY", "MARKET", 75, 1720.50, 129037.50, 20.0, 129.04, 3.6, 129190.14, "EXECUTED"
        ));
        transactionRepository.save(new Transaction(
                "ORD-SEED-" + userId + "-002", userId, "TCS", "Tata Consultancy Services Ltd.",
                "BUY", "MARKET", 25, 4150.00, 103750.00, 20.0, 103.75, 3.6, 103877.35, "EXECUTED"
        ));
        transactionRepository.save(new Transaction(
                "ORD-SEED-" + userId + "-003", userId, "RELIANCE", "Reliance Industries Ltd.",
                "BUY", "MARKET", 40, 2850.00, 114000.00, 20.0, 114.00, 3.6, 114137.60, "EXECUTED"
        ));
    }
}
