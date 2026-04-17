package com.cos.combined;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS so the static frontend served by Python on port 8000 can
 * call this backend on 8080 without preflight pain.
 *
 * Replaces the 3 per-module CORS configs (Daniel's inner WebMvcConfigurer
 * bean, Tarun's WebConfig, and the per-controller @CrossOrigin annotations),
 * which all had slightly different scopes.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:8000",
                        "http://127.0.0.1:8000",
                        "http://localhost:8080",
                        "http://127.0.0.1:8080"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}
