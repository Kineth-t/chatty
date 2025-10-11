package com.chat_project.Chatty.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        AuthResponse authResponse = authService.register(request);
        
        // Create HttpOnly cookie with token
        Cookie cookie = new Cookie("token", authResponse.getToken());
        cookie.setHttpOnly(true);  // Cannot be accessed by JavaScript
        cookie.setSecure(false);   // Set to true in production (HTTPS only)
        cookie.setPath("/");       // Available for all paths
        cookie.setMaxAge((int) (jwtExpiration / 1000)); // 24 hours
        cookie.setAttribute("SameSite", "Lax"); // CSRF protection
        
        response.addCookie(cookie);
        
        // Don't send token in response body
        return ResponseEntity.ok(AuthResponse.builder()
                .username(authResponse.getUsername())
                .email(authResponse.getEmail())
                .fullName(authResponse.getFullName())
                .message("Registration successful")
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthResponse authResponse = authService.login(request);
        
        // Create HttpOnly cookie with token
        Cookie cookie = new Cookie("token", authResponse.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false);   // Set to true in production
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpiration / 1000));
        cookie.setAttribute("SameSite", "Lax");
        
        response.addCookie(cookie);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .username(authResponse.getUsername())
                .email(authResponse.getEmail())
                .message("Login successful")
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // Delete the cookie
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // Delete immediately
        
        response.addCookie(cookie);
        
        return ResponseEntity.ok("Logged out successfully");
    }
    
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(
            @CookieValue(name = "token", required = false) String token
    ) {
        if (token == null) {
            return ResponseEntity.status(401).build();
        }
        
        AuthResponse user = authService.getUserFromToken(token);
        return ResponseEntity.ok(user);
    }
}