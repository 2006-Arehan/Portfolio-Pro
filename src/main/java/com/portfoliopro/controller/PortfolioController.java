package com.portfoliopro.controller;

import com.portfoliopro.dto.*;
import com.portfoliopro.model.User;
import com.portfoliopro.service.PortfolioService;
import com.portfoliopro.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final UserService userService;

    public PortfolioController(PortfolioService portfolioService, UserService userService) {
        this.portfolioService = portfolioService;
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

    @GetMapping("/summary")
    public ApiResponse<PortfolioSummaryDTO> getSummary(HttpServletRequest request) {
        return ApiResponse.success("Portfolio summary calculated", portfolioService.getPortfolioSummary(resolveUserId(request)));
    }

    @GetMapping("/holdings")
    public ApiResponse<List<HoldingDTO>> getHoldings(HttpServletRequest request) {
        return ApiResponse.success("Portfolio holdings retrieved", portfolioService.getHoldings(resolveUserId(request)));
    }

    @GetMapping("/sector-allocation")
    public ApiResponse<List<SectorAllocationDTO>> getSectorAllocation(HttpServletRequest request) {
        return ApiResponse.success("Sector distribution calculated", portfolioService.getSectorAllocation(resolveUserId(request)));
    }

    @GetMapping("/risk-metrics")
    public ApiResponse<RiskMetricsDTO> getRiskMetrics(HttpServletRequest request) {
        return ApiResponse.success("Institutional risk metrics calculated", portfolioService.getRiskMetrics(resolveUserId(request)));
    }

    @GetMapping("/performance-history")
    public ApiResponse<List<PricePointDTO>> getPerformanceHistory(
            @RequestParam(defaultValue = "1M") String timeframe,
            HttpServletRequest request) {
        return ApiResponse.success("Portfolio growth history generated", portfolioService.getPortfolioPerformanceHistory(resolveUserId(request), timeframe));
    }
}
