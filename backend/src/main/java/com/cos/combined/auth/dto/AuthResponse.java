package com.cos.combined.auth.dto;

/**
 * Response shape for /auth/register and /auth/login.
 * Returns enough info that the frontend can render "logged in as X"
 * and remember the user across the rest of the site.
 *
 * Crucially, the password hash is NEVER included.
 */
public class AuthResponse {
    private String token;     // opaque session token; frontend stores in localStorage
    private Long userId;      // primary key — for any module that wants FK references
    private String username;  // display name
    private String email;     // login identifier

    public AuthResponse() {}

    public AuthResponse(String token, Long userId, String username, String email) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
