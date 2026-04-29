package com.cos.combined.auth;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory token → userId map.
 *
 * Keeping this simple on purpose:
 *  - No JWT, no signing key, no expiry library, no DB-backed sessions.
 *  - One ConcurrentHashMap. Tokens are random URL-safe strings.
 *  - Sessions reset every time the backend restarts. Acceptable for a
 *    2-day group-project demo; trivially upgradable later if needed.
 *
 * If/when we move to JWTs, the rest of the app doesn't care — only this
 * class and AuthService change.
 */
@Component
public class SessionStore {

    private final ConcurrentHashMap<String, Long> tokenToUserId = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    /** Mint a new opaque token for the given userId and return it. */
    public String issueToken(Long userId) {
        byte[] bytes = new byte[32]; // 256 bits, base64url ≈ 43 chars
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tokenToUserId.put(token, userId);
        return token;
    }

    /** Resolve a token to the userId it represents, if still valid. */
    public Optional<Long> resolve(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        Long id = tokenToUserId.get(token);
        return Optional.ofNullable(id);
    }

    /** Forget the token (logout). Idempotent. */
    public void revoke(String token) {
        if (token != null) tokenToUserId.remove(token);
    }
}
