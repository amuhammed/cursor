package com.example.azurekeyvaultdemo.controller;

import com.example.azurekeyvaultdemo.service.KeyVaultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/keyvault")
public class KeyVaultController {

    private static final Logger logger = LoggerFactory.getLogger(KeyVaultController.class);

    @Autowired
    private KeyVaultService keyVaultService;

    /**
     * Get a specific secret from Azure Key Vault
     */
    @GetMapping("/secrets/{secretName}")
    public ResponseEntity<Map<String, Object>> getSecret(@PathVariable String secretName) {
        Map<String, Object> response = new HashMap<>();
        try {
            String secretValue = keyVaultService.getSecret(secretName);
            response.put("secretName", secretName);
            response.put("secretValue", secretValue);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to retrieve secret: {}", secretName, e);
            response.put("error", "Failed to retrieve secret: " + e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check if a secret exists in Azure Key Vault
     */
    @GetMapping("/secrets/{secretName}/exists")
    public ResponseEntity<Map<String, Object>> secretExists(@PathVariable String secretName) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean exists = keyVaultService.secretExists(secretName);
            response.put("secretName", secretName);
            response.put("exists", exists);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to check secret existence: {}", secretName, e);
            response.put("error", "Failed to check secret existence: " + e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all secret names from Azure Key Vault
     */
    @GetMapping("/secrets")
    public ResponseEntity<Map<String, Object>> getAllSecretNames() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<String> secretNames = keyVaultService.getAllSecretNames();
            response.put("secretNames", secretNames);
            response.put("count", secretNames.size());
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to retrieve secret names", e);
            response.put("error", "Failed to retrieve secret names: " + e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get multiple secrets by providing a list of secret names
     */
    @PostMapping("/secrets/batch")
    public ResponseEntity<Map<String, Object>> getSecrets(@RequestBody List<String> secretNames) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> secrets = keyVaultService.getSecrets(secretNames);
            response.put("secrets", secrets);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to retrieve secrets", e);
            response.put("error", "Failed to retrieve secrets: " + e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Create or update a secret in Azure Key Vault
     */
    @PostMapping("/secrets/{secretName}")
    public ResponseEntity<Map<String, Object>> setSecret(
            @PathVariable String secretName,
            @RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        try {
            String secretValue = requestBody.get("secretValue");
            if (secretValue == null || secretValue.trim().isEmpty()) {
                response.put("error", "Secret value cannot be null or empty");
                response.put("success", false);
                return ResponseEntity.badRequest().body(response);
            }

            keyVaultService.setSecret(secretName, secretValue);
            response.put("secretName", secretName);
            response.put("message", "Secret successfully created/updated");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to set secret: {}", secretName, e);
            response.put("error", "Failed to set secret: " + e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete a secret from Azure Key Vault
     */
    @DeleteMapping("/secrets/{secretName}")
    public ResponseEntity<Map<String, Object>> deleteSecret(@PathVariable String secretName) {
        Map<String, Object> response = new HashMap<>();
        try {
            keyVaultService.deleteSecret(secretName);
            response.put("secretName", secretName);
            response.put("message", "Secret successfully deleted");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to delete secret: {}", secretName, e);
            response.put("error", "Failed to delete secret: " + e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Try to list secrets as a health check
            keyVaultService.getAllSecretNames();
            response.put("status", "healthy");
            response.put("message", "Azure Key Vault connection is working");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Health check failed", e);
            response.put("status", "unhealthy");
            response.put("error", "Azure Key Vault connection failed: " + e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}