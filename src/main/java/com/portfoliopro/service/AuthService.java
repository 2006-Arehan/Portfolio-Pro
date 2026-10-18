package com.portfoliopro.service;

import com.portfoliopro.dto.AuthRequest;
import com.portfoliopro.dto.AuthResponse;
import com.portfoliopro.dto.RegisterRequest;
import com.portfoliopro.model.User;
import com.portfoliopro.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;

    public AuthService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String cleanUsername = req.getUsername().trim().toLowerCase();
        String cleanEmail = req.getEmail().trim().toLowerCase();

        if (userRepository.findByUsername(cleanUsername).isPresent()) {
            throw new IllegalArgumentException("Username '" + req.getUsername() + "' is already registered");
        }
        if (userRepository.findByEmail(cleanEmail).isPresent()) {
            throw new IllegalArgumentException("Email '" + req.getEmail() + "' is already registered");
        }

        String passwordHash = hashPassword(req.getPassword());
        User newUser = new User(
                cleanUsername,
                req.getFullName().trim(),
                cleanEmail,
                passwordHash,
                1000000.00 // ₹10,00,000 Initial Virtual Cash
        );

        User savedUser = userRepository.save(newUser);

        // Seed initial starter portfolio for immediate exploration
        userService.resetDemoAccount(savedUser.getId());

        String token = "PP-AUTH-" + UUID.randomUUID();
        return new AuthResponse(token, savedUser, "Registration successful! Welcome to PortfolioPro.");
    }

    public AuthResponse login(AuthRequest req) {
        String identifier = req.getUsername().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByUsername(identifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(identifier);
        }

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userOpt.get();
        String incomingHash = hashPassword(req.getPassword());

        // Also allow legacy demo password if demo account
        boolean isValid = user.getPasswordHash().equals(incomingHash) 
                || ("demo123".equals(req.getPassword()) && "demo123_hash".equals(user.getPasswordHash()));

        if (!isValid) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = "PP-AUTH-" + UUID.randomUUID();
        return new AuthResponse(token, user, "Login successful! Welcome back, " + user.getFullName());
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
