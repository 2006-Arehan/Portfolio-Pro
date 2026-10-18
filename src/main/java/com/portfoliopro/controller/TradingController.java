package com.portfoliopro.controller;

import com.portfoliopro.dto.ApiResponse;
import com.portfoliopro.dto.OrderRequest;
import com.portfoliopro.dto.QuoteCalculationDTO;
import com.portfoliopro.model.Transaction;
import com.portfoliopro.model.User;
import com.portfoliopro.service.TradingService;
import com.portfoliopro.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade")
@CrossOrigin(origins = "*")
public class TradingController {

    private final TradingService tradingService;
    private final UserService userService;

    public TradingController(TradingService tradingService, UserService userService) {
        this.tradingService = tradingService;
        this.userService = userService;
    }

    private Long resolveUserId(HttpServletRequest request) {
        if (request != null) {
            String header = request.getHeader("X-User-Id");
            if (header != null && !header.trim().isEmpty()) {
                try {
                    return Long.parseLong(header.trim());
                } catch (NumberFormatException ignored) {}
            }
        }
        return userService.getOrCreateDefaultUser().getId();
    }

    @PostMapping("/order")
    public ApiResponse<?> executeOrder(@Valid @RequestBody OrderRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = resolveUserId(httpRequest);
            Transaction tx = tradingService.executeOrder(request, userId);
            return ApiResponse.success("Order executed successfully: " + tx.getOrderId(), tx);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/quote-calculator")
    public ApiResponse<?> calculateQuote(
            @RequestParam String ticker,
            @RequestParam(defaultValue = "BUY") String orderType,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpServletRequest httpRequest) {
        try {
            Long userId = resolveUserId(httpRequest);
            QuoteCalculationDTO quote = tradingService.calculateQuote(ticker, orderType, quantity, userId);
            return ApiResponse.success("Quote estimated successfully", quote);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/transactions")
    public ApiResponse<List<Transaction>> getTransactions(HttpServletRequest httpRequest) {
        Long userId = resolveUserId(httpRequest);
        return ApiResponse.success("Transactions retrieved", tradingService.getUserTransactions(userId));
    }
}
