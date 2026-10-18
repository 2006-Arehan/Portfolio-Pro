package com.portfoliopro.controller;

import com.portfoliopro.dto.ApiResponse;
import com.portfoliopro.dto.AuthRequest;
import com.portfoliopro.dto.AuthResponse;
import com.portfoliopro.dto.RegisterRequest;
import com.portfoliopro.model.User;
import com.portfoliopro.service.AuthService;
import com.portfoliopro.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ApiResponse.success(response.getMessage(), response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ApiResponse.success(response.getMessage(), response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ApiResponse<?> getCurrentUser(HttpServletRequest request) {
        String header = request.getHeader("X-User-Id");
        if (header != null && !header.isEmpty()) {
            try {
                Long id = Long.parseLong(header);
                return authService.getUserById(id)
                        .map(u -> ApiResponse.success("Authenticated user found", u))
                        .orElse(ApiResponse.error("User not found"));
            } catch (NumberFormatException ignored) {}
        }
        User defaultUser = userService.getOrCreateDefaultUser();
        return ApiResponse.success("Default user loaded", defaultUser);
    }
}
