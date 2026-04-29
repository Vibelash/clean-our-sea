package com.cos.combined.auth;

import com.cos.combined.auth.dto.AuthResponse;
import com.cos.combined.auth.dto.LoginRequest;
import com.cos.combined.auth.dto.RegisterRequest;
import com.example.seasweepers.Models.User;
import com.example.seasweepers.Repos.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Business logic for the shared auth module.
 *
 * Why this lives in com.cos.combined.auth (and not under any teammate's
 * package): every teammate's module reads/writes the same User table.
 * The auth module owns the username/email/passwordHash columns; it is
 * intentionally placed under com.cos.combined to make it clear that this
 * is shared infrastructure rather than one person's feature.
 *
 * The Tala-owned fields (bio, country, totalScore, weeklyGoal etc.) on
 * the User entity are deliberately untouched here.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SessionStore sessionStore;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, SessionStore sessionStore) {
        this.userRepository = userRepository;
        this.sessionStore = sessionStore;
    }

    /** Create a new account. Throws if the email or username is already taken. */
    public AuthResponse register(RegisterRequest req) {
        validateRegister(req);

        String email = req.getEmail().trim().toLowerCase();
        String username = req.getUsername().trim();

        if (userRepository.existsByEmail(email)) {
            throw new AuthException("That email is already registered.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new AuthException("That username is taken.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(req.getPassword()));
        // Sensible defaults for Tala's fields so leaderboard rows are valid.
        user.setBio("");
        user.setCountry("");
        user.setTotalScore(0);
        user.setWeeklyGoal(0);
        user.setWeeklyPoints(0);

        User saved = userRepository.save(user);
        String token = sessionStore.issueToken(saved.getId());
        return new AuthResponse(token, saved.getId(), saved.getUsername(), saved.getEmail());
    }

    /** Verify password and mint a session token. */
    public AuthResponse login(LoginRequest req) {
        if (req == null || req.getEmail() == null || req.getPassword() == null) {
            throw new AuthException("Email and password are required.");
        }

        String email = req.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Invalid email or password."));

        // Reject accounts that exist but have no password set (legacy rows
        // created via Tala's UserController before auth was wired up).
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new AuthException("This account has no password set yet. Please register again.");
        }

        if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new AuthException("Invalid email or password.");
        }

        String token = sessionStore.issueToken(user.getId());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail());
    }

    /** Look up the User behind a token (for /auth/me). */
    public User currentUser(String token) {
        Long userId = sessionStore.resolve(token)
                .orElseThrow(() -> new AuthException("Not logged in."));
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User no longer exists."));
    }

    public void logout(String token) {
        sessionStore.revoke(token);
    }

    // ---------- helpers ----------

    private void validateRegister(RegisterRequest req) {
        if (req == null) {
            throw new AuthException("Request body missing.");
        }
        if (req.getUsername() == null || req.getUsername().trim().length() < 2) {
            throw new AuthException("Username must be at least 2 characters.");
        }
        if (req.getEmail() == null || !req.getEmail().contains("@")) {
            throw new AuthException("Please enter a valid email address.");
        }
        if (req.getPassword() == null || req.getPassword().length() < 8) {
            throw new AuthException("Password must be at least 8 characters.");
        }
    }

    /** Domain exception so AuthController can map it to a clean 400 response. */
    public static class AuthException extends RuntimeException {
        public AuthException(String message) { super(message); }
    }
}
