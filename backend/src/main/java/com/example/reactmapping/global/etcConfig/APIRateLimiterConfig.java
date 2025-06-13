package com.example.reactmapping.global.etcConfig;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class APIRateLimiterConfig {

    @Bean
    public RateLimiterConfig rateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofSeconds(5))
                .build();
    }

    @Bean
    public RateLimiter rateLimiter(RateLimiterConfig rateLimiterConfig) {
        return RateLimiter.of("riot-api-limiter", rateLimiterConfig);
    }
}
