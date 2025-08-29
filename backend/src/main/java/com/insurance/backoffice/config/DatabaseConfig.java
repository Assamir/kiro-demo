package com.insurance.backoffice.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Database configuration for production environments with optimized connection pooling.
 * This configuration provides HikariCP connection pool tuning for different environments.
 */
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * Production-optimized HikariCP DataSource configuration.
     * Configured for high-throughput production environments.
     */
    @Bean
    @Profile("prod")
    public DataSource productionDataSource() {
        HikariConfig config = new HikariConfig();
        
        // Basic connection settings
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        
        // Production connection pool settings
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000); // 5 minutes
        config.setMaxLifetime(1200000); // 20 minutes
        config.setConnectionTimeout(20000); // 20 seconds
        config.setValidationTimeout(5000); // 5 seconds
        config.setLeakDetectionThreshold(60000); // 1 minute
        
        // Performance optimizations
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("InsuranceHikariCP-Prod");
        config.setAutoCommit(true);
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        
        // Connection properties for PostgreSQL optimization
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        return new HikariDataSource(config);
    }

    /**
     * Staging-optimized HikariCP DataSource configuration.
     * Configured for testing environments with moderate load.
     */
    @Bean
    @Profile("staging")
    public DataSource stagingDataSource() {
        HikariConfig config = new HikariConfig();
        
        // Basic connection settings
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        
        // Staging connection pool settings (smaller pool)
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(3);
        config.setIdleTimeout(300000); // 5 minutes
        config.setMaxLifetime(1200000); // 20 minutes
        config.setConnectionTimeout(20000); // 20 seconds
        config.setValidationTimeout(5000); // 5 seconds
        config.setLeakDetectionThreshold(60000); // 1 minute
        
        // Performance optimizations
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("InsuranceHikariCP-Staging");
        config.setAutoCommit(true);
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        
        // Basic connection properties
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "100");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "1024");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        
        return new HikariDataSource(config);
    }
}