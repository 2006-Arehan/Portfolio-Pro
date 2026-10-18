package com.portfoliopro.controller;

import com.portfoliopro.dto.ApiResponse;
import com.portfoliopro.model.User;
import com.portfoliopro.repository.UserRepository;
import com.portfoliopro.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
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

    @GetMapping("/profile")
    public ApiResponse<User> getProfile(HttpServletRequest request) {
        Long userId = resolveUserId(request);
        return userRepository.findById(userId)
                .map(u -> ApiResponse.success("User profile retrieved", u))
                .orElseGet(() -> ApiResponse.success("Default user retrieved", userService.getOrCreateDefaultUser()));
    }

    @PostMapping("/reset")
    public ApiResponse<User> resetDemoAccount(HttpServletRequest request) {
        Long userId = resolveUserId(request);
        User resetUser = userService.resetDemoAccount(userId);
        return ApiResponse.success("Demo account reset successfully with ₹10,00,000 capital and initial holdings", resetUser);
    }

    @PostMapping("/deposit")
    public ApiResponse<User> depositVirtualFunds(@RequestBody Map<String, Double> payload, HttpServletRequest request) {
        Double amount = payload.get("amount");
        if (amount == null || amount <= 0) {
            return ApiResponse.error("Deposit amount must be greater than 0");
        }
        Long userId = resolveUserId(request);
        User user = userRepository.findById(userId)
                .orElseGet(userService::getOrCreateDefaultUser);
        user.setCashBalance(Math.round((user.getCashBalance() + amount) * 100.0) / 100.0);
        userRepository.save(user);
        return ApiResponse.success(String.format("Successfully deposited ₹%,.2f virtual funds", amount), user);
    }
}
