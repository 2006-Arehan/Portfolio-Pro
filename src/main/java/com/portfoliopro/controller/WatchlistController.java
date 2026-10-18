package com.portfoliopro.controller;

import com.portfoliopro.dto.ApiResponse;
import com.portfoliopro.model.Stock;
import com.portfoliopro.model.User;
import com.portfoliopro.service.UserService;
import com.portfoliopro.service.WatchlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watchlist")
@CrossOrigin(origins = "*")
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final UserService userService;

    public WatchlistController(WatchlistService watchlistService, UserService userService) {
        this.watchlistService = watchlistService;
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

    @GetMapping
    public ApiResponse<List<Stock>> getWatchlist(HttpServletRequest request) {
        return ApiResponse.success("Watchlist items retrieved", watchlistService.getWatchlistStocks(resolveUserId(request)));
    }

    @PostMapping("/toggle/{ticker}")
    public ApiResponse<Map<String, Object>> toggleWatchlist(@PathVariable String ticker, HttpServletRequest request) {
        Long userId = resolveUserId(request);
        boolean isNowWatched = watchlistService.toggleWatchlist(userId, ticker);
        String action = isNowWatched ? "added to" : "removed from";
        return ApiResponse.success("Stock " + ticker + " " + action + " watchlist", Map.of("ticker", ticker, "watched", isNowWatched));
    }

    @GetMapping("/check/{ticker}")
    public ApiResponse<Map<String, Object>> checkWatchlist(@PathVariable String ticker, HttpServletRequest request) {
        boolean watched = watchlistService.isWatched(resolveUserId(request), ticker);
        return ApiResponse.success("Watchlist status check", Map.of("ticker", ticker, "watched", watched));
    }
}
