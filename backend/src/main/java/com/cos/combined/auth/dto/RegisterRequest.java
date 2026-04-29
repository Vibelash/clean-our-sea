package com.cos.combined.auth.dto;

/**
 * Body of POST /auth/register.
 * username = public display name (also used as Snake leaderboard player name)
 * email    = login identifier (must be unique)
 * password = plain-text password from the user; we BCrypt it before storage.
 */
public class RegisterRequest {
    private String username;
    private String email;
    private String password;

    public RegisterRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
