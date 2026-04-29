package com.cos.combined.auth;

import com.cos.combined.auth.dto.AuthResponse;
import com.cos.combined.auth.dto.LoginRequest;
import com.cos.combined.auth.dto.RegisterRequest;
import com.example.seasweepers.Models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP endpoints for the shared auth flow.
 *
 *   POST /auth/register   { username, email, password }   -> AuthResponse
 *   POST /auth/login      { email, password }             -> AuthResponse
 *   GET  /auth/me                                          -> { userId, username, email }
 *   POST /auth/logout                                      -> 204
 *
 * The token is returned in the JSON body of register/login. The frontend
 * stores it (e.g. localStorage) and sends it back on subsequent calls via
 *   Authorization: Bearer <token>
 *
 * @CrossOrigin so any teammate's frontend served from a different port
 * (e.g. Daniel's quizzes page on :8000) can call these without a CORS
 * pre-flight failure.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
        } catch (AuthService.AuthException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            return ResponseEntity.ok(authService.login(req));
        } catch (AuthService.AuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = stripBearer(authHeader);
        try {
            User u = authService.currentUser(token);
            Map<String, Object> body = new HashMap<>();
            body.put("userId", u.getId());
            body.put("username", u.getUsername());
            body.put("email", u.getEmail());
            return ResponseEntity.ok(body);
        } catch (AuthService.AuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        authService.logout(stripBearer(authHeader));
        return ResponseEntity.noContent().build();
    }

    private static String stripBearer(String header) {
        if (header == null) return null;
        String h = header.trim();
        if (h.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return h.substring(7).trim();
        }
        return h;
    }
}
