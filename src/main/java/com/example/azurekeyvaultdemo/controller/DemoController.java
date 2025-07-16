package com.example.azurekeyvaultdemo.controller;

import com.example.azurekeyvaultdemo.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @Autowired
    private AppConfig.DatabaseConfig databaseConfig;

    @Autowired
    private AppConfig.ApiConfig apiConfig;

    @Autowired
    private AppConfig.JwtConfig jwtConfig;

    /**
     * Demonstrate how configuration values from Key Vault are injected
     */
    @GetMapping("/config")
    public Map<String, Object> getConfigValues() {
        Map<String, Object> config = new HashMap<>();
        
        // Note: In a real application, you would NOT expose sensitive values like this
        // This is just for demonstration purposes
        config.put("databaseConnectionString", maskSensitiveValue(databaseConfig.getConnectionString()));
        config.put("apiKey", maskSensitiveValue(apiConfig.getApiKey()));
        config.put("jwtSecret", maskSensitiveValue(jwtConfig.getJwtSecret()));
        
        return config;
    }

    /**
     * Example of using the database configuration
     */
    @GetMapping("/database-status")
    public Map<String, Object> getDatabaseStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // In a real application, you would use the connection string to connect to the database
        String connectionString = databaseConfig.getConnectionString();
        boolean isConfigured = !connectionString.equals("default-connection-string");
        
        status.put("configured", isConfigured);
        status.put("connectionString", maskSensitiveValue(connectionString));
        status.put("status", isConfigured ? "Ready to connect" : "Using default configuration");
        
        return status;
    }

    /**
     * Example of using the API configuration
     */
    @GetMapping("/api-status")
    public Map<String, Object> getApiStatus() {
        Map<String, Object> status = new HashMap<>();
        
        String apiKey = apiConfig.getApiKey();
        boolean isConfigured = !apiKey.equals("default-api-key");
        
        status.put("configured", isConfigured);
        status.put("apiKey", maskSensitiveValue(apiKey));
        status.put("status", isConfigured ? "API key configured" : "Using default API key");
        
        return status;
    }

    /**
     * Example of using the JWT configuration
     */
    @GetMapping("/jwt-status")
    public Map<String, Object> getJwtStatus() {
        Map<String, Object> status = new HashMap<>();
        
        String jwtSecret = jwtConfig.getJwtSecret();
        boolean isConfigured = !jwtSecret.equals("default-jwt-secret");
        
        status.put("configured", isConfigured);
        status.put("jwtSecret", maskSensitiveValue(jwtSecret));
        status.put("status", isConfigured ? "JWT secret configured" : "Using default JWT secret");
        
        return status;
    }

    /**
     * Utility method to mask sensitive values for display
     */
    private String maskSensitiveValue(String value) {
        if (value == null || value.length() <= 4) {
            return "****";
        }
        return value.substring(0, 2) + "****" + value.substring(value.length() - 2);
    }
}