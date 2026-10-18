package com.portfoliopro.service;

import com.portfoliopro.dto.OrderRequest;
import com.portfoliopro.dto.QuoteCalculationDTO;
import com.portfoliopro.model.PortfolioItem;
import com.portfoliopro.model.Stock;
import com.portfoliopro.model.Transaction;
import com.portfoliopro.model.User;
import com.portfoliopro.repository.PortfolioItemRepository;
import com.portfoliopro.repository.StockRepository;
import com.portfoliopro.repository.TransactionRepository;
import com.portfoliopro.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TradingService {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final TransactionRepository transactionRepository;

    public TradingService(UserRepository userRepository,
                          StockRepository stockRepository,
                          PortfolioItemRepository portfolioItemRepository,
                          TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.portfolioItemRepository = portfolioItemRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Calculates fee breakdown and margin requirement for pre-order assessment.
     */
    public QuoteCalculationDTO calculateQuote(String ticker, String orderType, Integer quantity, Long userId) {
        Stock stock = stockRepository.findByTickerIgnoreCase(ticker)
                .orElseThrow(() -> new IllegalArgumentException("Stock symbol not found: " + ticker));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        int qty = (quantity == null || quantity < 1) ? 1 : quantity;
        double unitPrice = stock.getCurrentPrice();
        double grossAmount = Math.round(unitPrice * qty * 100.0) / 100.0;

        // Enterprise Brokerage & Taxation Schedule
        double brokerage = 20.00; // Flat ₹20 discount brokerage
        double sttTax = Math.round(grossAmount * 0.001 * 100.0) / 100.0; // 0.1% STT
        double gstTax = Math.round(brokerage * 0.18 * 100.0) / 100.0; // 18% GST on brokerage
        double totalCharges = brokerage + sttTax + gstTax;

        double netTotal;
        boolean sufficientMargin;

        if ("BUY".equalsIgnoreCase(orderType)) {
            netTotal = Math.round((grossAmount + totalCharges) * 100.0) / 100.0;
            sufficientMargin = user.getCashBalance() >= netTotal;
        } else {
            netTotal = Math.round((grossAmount - totalCharges) * 100.0) / 100.0;
            Optional<PortfolioItem> holding = portfolioItemRepository.findByUserIdAndTicker(userId, stock.getTicker());
            sufficientMargin = holding.isPresent() && holding.get().getQuantity() >= qty;
        }

        return new QuoteCalculationDTO(
                stock.getTicker(),
                stock.getCompanyName(),
                orderType.toUpperCase(),
                qty,
                unitPrice,
                grossAmount,
                brokerage,
                sttTax,
                gstTax,
                netTotal,
                user.getCashBalance(),
                sufficientMargin
        );
    }

    /**
     * Executes Buy or Sell order with full ACID transaction integrity.
     */
    @Transactional
    public Transaction executeOrder(OrderRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User ID not found: " + userId));

        Stock stock = stockRepository.findByTickerIgnoreCase(request.getTicker())
                .orElseThrow(() -> new IllegalArgumentException("Invalid stock symbol: " + request.getTicker()));

        int qty = request.getQuantity();
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        double executionPrice = stock.getCurrentPrice();
        if ("LIMIT".equalsIgnoreCase(request.getOrderCategory()) && request.getLimitPrice() != null) {
            executionPrice = request.getLimitPrice();
        }

        double grossAmount = Math.round(executionPrice * qty * 100.0) / 100.0;
        double brokerage = 20.00;
        double sttTax = Math.round(grossAmount * 0.001 * 100.0) / 100.0;
        double gstTax = Math.round(brokerage * 0.18 * 100.0) / 100.0;
        double totalCharges = brokerage + sttTax + gstTax;

        String orderId = generateOrderId();
        String type = request.getOrderType().toUpperCase();

        if ("BUY".equals(type)) {
            double netRequired = Math.round((grossAmount + totalCharges) * 100.0) / 100.0;
            if (user.getCashBalance() < netRequired) {
                throw new IllegalStateException(String.format("Insufficient funds. Required: ₹%,.2f | Available: ₹%,.2f",
                        netRequired, user.getCashBalance()));
            }

            // Deduct cash
            user.setCashBalance(Math.round((user.getCashBalance() - netRequired) * 100.0) / 100.0);
            userRepository.save(user);

            // Update Portfolio
            Optional<PortfolioItem> existingHolding = portfolioItemRepository.findByUserIdAndTicker(userId, stock.getTicker());
            if (existingHolding.isPresent()) {
                PortfolioItem item = existingHolding.get();
                int newQty = item.getQuantity() + qty;
                double newInvested = item.getTotalInvested() + grossAmount;
                double newAvgPrice = Math.round((newInvested / newQty) * 100.0) / 100.0;

                item.setQuantity(newQty);
                item.setTotalInvested(Math.round(newInvested * 100.0) / 100.0);
                item.setAvgBuyPrice(newAvgPrice);
                item.setUpdatedAt(LocalDateTime.now());
                portfolioItemRepository.save(item);
            } else {
                PortfolioItem newItem = new PortfolioItem(userId, stock.getTicker(), qty, executionPrice);
                portfolioItemRepository.save(newItem);
            }

            // Record Transaction
            Transaction tx = new Transaction(
                    orderId, userId, stock.getTicker(), stock.getCompanyName(),
                    "BUY", request.getOrderCategory() != null ? request.getOrderCategory() : "MARKET",
                    qty, executionPrice, grossAmount, brokerage, sttTax, gstTax, netRequired, "EXECUTED"
            );
            return transactionRepository.save(tx);

        } else if ("SELL".equals(type)) {
            PortfolioItem holding = portfolioItemRepository.findByUserIdAndTicker(userId, stock.getTicker())
                    .orElseThrow(() -> new IllegalStateException("You do not hold shares of " + stock.getTicker() + " in your portfolio"));

            if (holding.getQuantity() < qty) {
                throw new IllegalStateException(String.format("Insufficient shares. Holding: %d shares | Attempting to sell: %d shares",
                        holding.getQuantity(), qty));
            }

            double netProceeds = Math.round((grossAmount - totalCharges) * 100.0) / 100.0;

            // Credit proceeds to user wallet
            user.setCashBalance(Math.round((user.getCashBalance() + netProceeds) * 100.0) / 100.0);

            // Compute Realized PnL
            double costBasis = holding.getAvgBuyPrice() * qty;
            double realizedGain = Math.round(((executionPrice * qty) - costBasis - totalCharges) * 100.0) / 100.0;
            user.setTotalRealizedPnl(Math.round((user.getTotalRealizedPnl() + realizedGain) * 100.0) / 100.0);
            userRepository.save(user);

            // Update or remove PortfolioItem
            if (holding.getQuantity() == qty) {
                portfolioItemRepository.delete(holding);
            } else {
                int remainingQty = holding.getQuantity() - qty;
                double remainingInvested = Math.round(remainingQty * holding.getAvgBuyPrice() * 100.0) / 100.0;
                holding.setQuantity(remainingQty);
                holding.setTotalInvested(remainingInvested);
                holding.setUpdatedAt(LocalDateTime.now());
                portfolioItemRepository.save(holding);
            }

            // Record Transaction
            Transaction tx = new Transaction(
                    orderId, userId, stock.getTicker(), stock.getCompanyName(),
                    "SELL", request.getOrderCategory() != null ? request.getOrderCategory() : "MARKET",
                    qty, executionPrice, grossAmount, brokerage, sttTax, gstTax, netProceeds, "EXECUTED"
            );
            return transactionRepository.save(tx);

        } else {
            throw new IllegalArgumentException("Unsupported order type: " + type);
        }
    }

    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByExecutedAtDesc(userId);
    }

    private String generateOrderId() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String shortId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD-" + dateStr + "-" + shortId;
    }
}
