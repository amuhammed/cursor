package com.example.azurekeyvaultdemo.service;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.security.keyvault.secrets.models.SecretProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeyVaultService {

    private static final Logger logger = LoggerFactory.getLogger(KeyVaultService.class);

    @Value("${spring.cloud.azure.keyvault.secret.endpoint}")
    private String keyVaultEndpoint;

    private SecretClient secretClient;

    @PostConstruct
    public void initialize() {
        try {
            this.secretClient = new SecretClientBuilder()
                    .vaultUrl(keyVaultEndpoint)
                    .credential(new DefaultAzureCredentialBuilder().build())
                    .buildClient();
            logger.info("Azure Key Vault client initialized successfully for endpoint: {}", keyVaultEndpoint);
        } catch (Exception e) {
            logger.error("Failed to initialize Azure Key Vault client", e);
            throw new RuntimeException("Failed to initialize Azure Key Vault client", e);
        }
    }

    /**
     * Retrieve a secret value from Azure Key Vault
     */
    public String getSecret(String secretName) {
        try {
            KeyVaultSecret secret = secretClient.getSecret(secretName);
            logger.info("Successfully retrieved secret: {}", secretName);
            return secret.getValue();
        } catch (Exception e) {
            logger.error("Failed to retrieve secret: {}", secretName, e);
            throw new RuntimeException("Failed to retrieve secret: " + secretName, e);
        }
    }

    /**
     * Check if a secret exists in Azure Key Vault
     */
    public boolean secretExists(String secretName) {
        try {
            secretClient.getSecret(secretName);
            return true;
        } catch (Exception e) {
            logger.warn("Secret '{}' does not exist or cannot be accessed", secretName);
            return false;
        }
    }

    /**
     * Get all secret names from Azure Key Vault
     */
    public List<String> getAllSecretNames() {
        List<String> secretNames = new ArrayList<>();
        try {
            for (SecretProperties secretProperties : secretClient.listPropertiesOfSecrets()) {
                secretNames.add(secretProperties.getName());
            }
            logger.info("Retrieved {} secret names from Key Vault", secretNames.size());
        } catch (Exception e) {
            logger.error("Failed to retrieve secret names", e);
            throw new RuntimeException("Failed to retrieve secret names", e);
        }
        return secretNames;
    }

    /**
     * Get multiple secrets as a map
     */
    public Map<String, String> getSecrets(List<String> secretNames) {
        Map<String, String> secrets = new HashMap<>();
        for (String secretName : secretNames) {
            try {
                String secretValue = getSecret(secretName);
                secrets.put(secretName, secretValue);
            } catch (Exception e) {
                logger.warn("Failed to retrieve secret '{}': {}", secretName, e.getMessage());
                secrets.put(secretName, null);
            }
        }
        return secrets;
    }

    /**
     * Create or update a secret in Azure Key Vault
     */
    public void setSecret(String secretName, String secretValue) {
        try {
            secretClient.setSecret(secretName, secretValue);
            logger.info("Successfully set secret: {}", secretName);
        } catch (Exception e) {
            logger.error("Failed to set secret: {}", secretName, e);
            throw new RuntimeException("Failed to set secret: " + secretName, e);
        }
    }

    /**
     * Delete a secret from Azure Key Vault
     */
    public void deleteSecret(String secretName) {
        try {
            secretClient.beginDeleteSecret(secretName);
            logger.info("Successfully deleted secret: {}", secretName);
        } catch (Exception e) {
            logger.error("Failed to delete secret: {}", secretName, e);
            throw new RuntimeException("Failed to delete secret: " + secretName, e);
        }
    }
}