package com.cos.combined;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:8000",
                        "http://127.0.0.1:8000",
                        "http://localhost:8080",
                        "http://127.0.0.1:8080",
                        "https://clean-our-sea.web.app",
                        "https://clean-our-sea.firebaseapp.com",
                        "https://*.onrender.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}
