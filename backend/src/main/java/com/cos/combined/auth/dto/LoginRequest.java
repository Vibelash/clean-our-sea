package com.cos.combined.auth.dto;

/**
 * Body of POST /auth/login.
 * email + password — same as the register inputs minus username.
 */
public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
