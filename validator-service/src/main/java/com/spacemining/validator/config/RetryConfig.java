package com.spacemining.validator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

import com.spacemining.validator.exception.ValidationFailureException;

@Configuration
public class RetryConfig {

    // Exponential backoff: 500ms, 1s, 2s, 4s entre as 5 tentativas (limite de 5s)
    @Bean
    public RetryTemplate retryTemplate(
            @Value("${validator.retry.max-attempts:5}") int maxAttempts,
            @Value("${validator.retry.initial-interval-ms:500}") long initialInterval,
            @Value("${validator.retry.multiplier:2.0}") double multiplier,
            @Value("${validator.retry.max-interval-ms:5000}") long maxInterval) {
        return RetryTemplate.builder()
                .maxAttempts(maxAttempts)
                .exponentialBackoff(initialInterval, multiplier, maxInterval)
                .retryOn(ValidationFailureException.class)
                .build();
    }
}
