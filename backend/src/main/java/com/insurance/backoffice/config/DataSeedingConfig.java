package com.insurance.backoffice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for data seeding behavior.
 * Allows fine-grained control over what data should be seeded and when.
 * 
 * Clean Code Principles Applied:
 * - Single Responsibility: Focused only on seeding configuration
 * - Configuration Segregation: Separate from business logic
 * - Environment-specific: Only active in development/test profiles
 */
@Configuration
@ConfigurationProperties(prefix = "app.data-seeding")
@Profile({"dev", "test"})
public class DataSeedingConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSeedingConfig.class);

    /**
     * Whether data seeding is enabled at all
     */
    private boolean enabled = true;

    /**
     * Whether to seed user data
     */
    private boolean seedUsers = true;

    /**
     * Whether to seed client data
     */
    private boolean seedClients = true;

    /**
     * Whether to seed vehicle data
     */
    private boolean seedVehicles = true;

    /**
     * Whether to seed policy data
     */
    private boolean seedPolicies = true;

    /**
     * Whether to seed rating table data (usually handled by migrations)
     */
    private boolean seedRatingTables = false;

    /**
     * Whether to force seeding even if data already exists
     */
    private boolean forceSeeding = false;

    /**
     * Number of sample records to create for each entity type
     */
    private int sampleSize = 10;

    // Getters and setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            logger.info("Data seeding is enabled");
        } else {
            logger.info("Data seeding is disabled");
        }
    }

    public boolean isSeedUsers() {
        return seedUsers;
    }

    public void setSeedUsers(boolean seedUsers) {
        this.seedUsers = seedUsers;
    }

    public boolean isSeedClients() {
        return seedClients;
    }

    public void setSeedClients(boolean seedClients) {
        this.seedClients = seedClients;
    }

    public boolean isSeedVehicles() {
        return seedVehicles;
    }

    public void setSeedVehicles(boolean seedVehicles) {
        this.seedVehicles = seedVehicles;
    }

    public boolean isSeedPolicies() {
        return seedPolicies;
    }

    public void setSeedPolicies(boolean seedPolicies) {
        this.seedPolicies = seedPolicies;
    }

    public boolean isSeedRatingTables() {
        return seedRatingTables;
    }

    public void setSeedRatingTables(boolean seedRatingTables) {
        this.seedRatingTables = seedRatingTables;
    }

    public boolean isForceSeeding() {
        return forceSeeding;
    }

    public void setForceSeeding(boolean forceSeeding) {
        this.forceSeeding = forceSeeding;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = Math.max(1, Math.min(sampleSize, 100)); // Limit between 1 and 100
    }

    /**
     * Check if any seeding is configured to run
     */
    public boolean hasAnySeedingEnabled() {
        return enabled && (seedUsers || seedClients || seedVehicles || seedPolicies || seedRatingTables);
    }

    /**
     * Log current configuration for debugging
     */
    public void logConfiguration() {
        logger.info("Data Seeding Configuration:");
        logger.info("  Enabled: {}", enabled);
        logger.info("  Seed Users: {}", seedUsers);
        logger.info("  Seed Clients: {}", seedClients);
        logger.info("  Seed Vehicles: {}", seedVehicles);
        logger.info("  Seed Policies: {}", seedPolicies);
        logger.info("  Seed Rating Tables: {}", seedRatingTables);
        logger.info("  Force Seeding: {}", forceSeeding);
        logger.info("  Sample Size: {}", sampleSize);
    }
}