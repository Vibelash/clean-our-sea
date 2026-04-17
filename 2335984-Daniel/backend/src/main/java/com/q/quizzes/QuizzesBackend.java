package com.q.quizzes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class QuizzesBackend 
{
	public static void main(String[] args) 
	{
		SpringApplication.run(QuizzesBackend.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() 
	{
		return new WebMvcConfigurer() 
		{
			@Override
			public void addCorsMappings(CorsRegistry registry) 
			{
				registry.addMapping("/**")
						// allowedOriginPatterns works with file:// and any localhost port (Live Server uses 5500)
						.allowedOriginPatterns("*")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*");
			}
		};
	}
}
