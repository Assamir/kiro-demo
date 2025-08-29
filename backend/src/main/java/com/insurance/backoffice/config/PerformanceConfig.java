package com.insurance.backoffice.config;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;

/**
 * Performance and monitoring configuration for production environments.
 * Includes caching, metrics, and performance optimizations.
 */
@Configuration
@EnableCaching
public class PerformanceConfig {

    /**
     * Configure metrics registry with custom tags and filters.
     */
    @Bean
    @Profile({"prod", "staging"})
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            registry.config()
                .commonTags("application", "insurance-backoffice")
                .meterFilter(MeterFilter.deny(id -> {
                    String uri = id.getTag("uri");
                    return uri != null && (
                        uri.startsWith("/actuator") ||
                        uri.startsWith("/swagger") ||
                        uri.startsWith("/api-docs")
                    );
                }));
        };
    }

    /**
     * Cache manager for application-level caching.
     * Used for rating tables, user data, and other frequently accessed data.
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "rating-tables",
            "policies",
            "users",
            "clients",
            "vehicles"
        ));
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }
}