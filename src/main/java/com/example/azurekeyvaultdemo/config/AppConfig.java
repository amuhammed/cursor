package com.example.azurekeyvaultdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AppConfig {

    // Example of injecting secrets from Key Vault directly into configuration
    // These will be resolved from Azure Key Vault if the secrets exist
    @Value("${database-connection-string:default-connection-string}")
    private String databaseConnectionString;

    @Value("${api-key:default-api-key}")
    private String apiKey;

    @Value("${jwt-secret:default-jwt-secret}")
    private String jwtSecret;

    /**
     * Example bean that uses secrets from Key Vault
     */
    @Bean
    public DatabaseConfig databaseConfig() {
        return new DatabaseConfig(databaseConnectionString);
    }

    /**
     * Example bean that uses API key from Key Vault
     */
    @Bean
    public ApiConfig apiConfig() {
        return new ApiConfig(apiKey);
    }

    /**
     * Example bean that uses JWT secret from Key Vault
     */
    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig(jwtSecret);
    }

    // Inner classes for configuration examples
    public static class DatabaseConfig {
        private final String connectionString;

        public DatabaseConfig(String connectionString) {
            this.connectionString = connectionString;
        }

        public String getConnectionString() {
            return connectionString;
        }
    }

    public static class ApiConfig {
        private final String apiKey;

        public ApiConfig(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getApiKey() {
            return apiKey;
        }
    }

    public static class JwtConfig {
        private final String jwtSecret;

        public JwtConfig(String jwtSecret) {
            this.jwtSecret = jwtSecret;
        }

        public String getJwtSecret() {
            return jwtSecret;
        }
    }
}