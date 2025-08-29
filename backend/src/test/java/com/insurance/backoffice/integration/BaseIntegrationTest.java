package com.insurance.backoffice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.domain.UserRole;
import com.insurance.backoffice.infrastructure.repository.UserRepository;
import com.insurance.backoffice.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests using TestContainers.
 * Provides common setup for database, authentication, and test utilities.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public abstract class BaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("insurance_test")
            .withUsername("test_user")
            .withPassword("test_password")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtUtil jwtUtil;

    protected User testAdmin;
    protected User testOperator;
    protected String adminToken;
    protected String operatorToken;

    @BeforeEach
    void setUpBaseIntegrationTest() {
        createTestUsers();
        generateTokens();
    }

    private void createTestUsers() {
        // Create test admin user
        testAdmin = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin123"))
                .role(UserRole.ADMIN)
                .build();
        testAdmin = userRepository.save(testAdmin);

        // Create test operator user
        testOperator = User.builder()
                .firstName("Operator")
                .lastName("User")
                .email("operator@test.com")
                .password(passwordEncoder.encode("operator123"))
                .role(UserRole.OPERATOR)
                .build();
        testOperator = userRepository.save(testOperator);
    }

    private void generateTokens() {
        adminToken = jwtUtil.generateToken(testAdmin);
        operatorToken = jwtUtil.generateToken(testOperator);
    }

    protected HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    protected HttpHeaders createAdminHeaders() {
        return createAuthHeaders(adminToken);
    }

    protected HttpHeaders createOperatorHeaders() {
        return createAuthHeaders(operatorToken);
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }
}