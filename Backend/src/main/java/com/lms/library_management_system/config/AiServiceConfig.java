package com.lms.library_management_system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration for the Python AI recommendation service HTTP client.
 * The base URL is read from the AI_SERVICE_URL environment variable,
 * defaulting to http://localhost:8000 for local development.
 */
@Configuration
public class AiServiceConfig {

    @Value("${ai.service.base-url:http://localhost:8000}")
    private String aiServiceBaseUrl;

    @Bean
    public RestOperations aiRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }

    public String getAiServiceBaseUrl() {
        return aiServiceBaseUrl;
    }
}
