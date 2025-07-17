# Azure Key Vault Spring Boot Demo

A Spring Boot application that demonstrates how to read data from Azure Key Vault using the Azure Spring Cloud libraries.

## Features

- **Azure Key Vault Integration**: Read secrets from Azure Key Vault
- **REST API**: HTTP endpoints to interact with Key Vault
- **Configuration Injection**: Automatic injection of secrets into Spring configuration
- **Health Checks**: Built-in health check endpoints
- **Error Handling**: Comprehensive error handling and logging
- **Authentication**: Support for multiple Azure authentication methods

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Azure subscription with Key Vault access
- Azure CLI (optional, for authentication)

## Azure Setup

### 1. Create Azure Key Vault

```bash
# Create a resource group
az group create --name my-resource-group --location eastus

# Create a Key Vault
az keyvault create --name my-keyvault --resource-group my-resource-group --location eastus
```

### 2. Create Azure Service Principal

```bash
# Create a service principal
az ad sp create-for-rbac --name my-keyvault-app --role contributor --scopes /subscriptions/{subscription-id}/resourceGroups/{resource-group}/providers/Microsoft.KeyVault/vaults/{keyvault-name}

# Note down the output:
# - appId (client-id)
# - password (client-secret)
# - tenant
```

### 3. Grant Key Vault Permissions

```bash
# Grant secret permissions to the service principal
az keyvault set-policy --name my-keyvault --spn {client-id} --secret-permissions get list set delete
```

### 4. Add Sample Secrets

```bash
# Add some sample secrets to test with
az keyvault secret set --vault-name my-keyvault --name database-connection-string --value "Server=myserver;Database=mydb;User=myuser;Password=mypass"
az keyvault secret set --vault-name my-keyvault --name api-key --value "your-api-key-here"
az keyvault secret set --vault-name my-keyvault --name jwt-secret --value "your-jwt-secret-here"
```

## Application Configuration

### Environment Variables

Set the following environment variables:

```bash
export AZURE_KEYVAULT_ENDPOINT=https://my-keyvault.vault.azure.net/
export AZURE_CLIENT_ID=your-client-id
export AZURE_CLIENT_SECRET=your-client-secret
export AZURE_TENANT_ID=your-tenant-id
```

### Alternative: application.yml

Update `src/main/resources/application.yml` with your Key Vault details:

```yaml
spring:
  cloud:
    azure:
      keyvault:
        secret:
          endpoint: https://your-keyvault-name.vault.azure.net/
      credentials:
        client-id: your-client-id
        client-secret: your-client-secret
      profile:
        tenant-id: your-tenant-id
```

## Running the Application

### 1. Build the Application

```bash
mvn clean compile
```

### 2. Run Tests

```bash
mvn test
```

### 3. Start the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Key Vault Operations

- **GET** `/api/keyvault/health` - Health check for Key Vault connection
- **GET** `/api/keyvault/secrets` - List all secret names
- **GET** `/api/keyvault/secrets/{secretName}` - Get a specific secret
- **GET** `/api/keyvault/secrets/{secretName}/exists` - Check if secret exists
- **POST** `/api/keyvault/secrets/batch` - Get multiple secrets
- **POST** `/api/keyvault/secrets/{secretName}` - Create/update a secret
- **DELETE** `/api/keyvault/secrets/{secretName}` - Delete a secret

### Configuration Demo

- **GET** `/api/demo/config` - Show injected configuration values
- **GET** `/api/demo/database-status` - Database configuration status
- **GET** `/api/demo/api-status` - API configuration status
- **GET** `/api/demo/jwt-status` - JWT configuration status

### Spring Boot Actuator

- **GET** `/actuator/health` - Application health
- **GET** `/actuator/info` - Application info
- **GET** `/actuator/env` - Environment properties

## Usage Examples

### 1. Get a Secret

```bash
curl http://localhost:8080/api/keyvault/secrets/database-connection-string
```

### 2. Check Key Vault Health

```bash
curl http://localhost:8080/api/keyvault/health
```

### 3. List All Secrets

```bash
curl http://localhost:8080/api/keyvault/secrets
```

### 4. Create a New Secret

```bash
curl -X POST http://localhost:8080/api/keyvault/secrets/my-new-secret \
  -H "Content-Type: application/json" \
  -d '{"secretValue": "my-secret-value"}'
```

### 5. Get Multiple Secrets

```bash
curl -X POST http://localhost:8080/api/keyvault/secrets/batch \
  -H "Content-Type: application/json" \
  -d '["database-connection-string", "api-key", "jwt-secret"]'
```

## Authentication Methods

The application supports multiple Azure authentication methods:

1. **Service Principal** (recommended for production)
2. **Managed Identity** (for Azure-hosted applications)
3. **Azure CLI** (for local development)
4. **Environment Variables**

## Security Best Practices

1. **Never commit secrets** to version control
2. **Use environment variables** for sensitive configuration
3. **Implement proper access controls** in Azure Key Vault
4. **Rotate secrets regularly**
5. **Monitor access logs** in Azure
6. **Use Managed Identity** when running on Azure

## Troubleshooting

### Common Issues

1. **Authentication Failed**
   - Verify your client ID, client secret, and tenant ID
   - Check Key Vault access policies
   - Ensure the service principal has proper permissions

2. **Key Vault Not Found**
   - Verify the Key Vault endpoint URL
   - Check if the Key Vault exists and is accessible

3. **Secret Not Found**
   - Verify the secret name (case-sensitive)
   - Check if the secret exists in the Key Vault
   - Verify read permissions

### Debug Mode

Enable debug logging by setting:

```yaml
logging:
  level:
    com.azure: DEBUG
    com.example: DEBUG
```

## Development

### Project Structure

```
src/
├── main/
│   ├── java/com/example/azurekeyvaultdemo/
│   │   ├── AzureKeyVaultDemoApplication.java
│   │   ├── config/
│   │   │   └── AppConfig.java
│   │   ├── controller/
│   │   │   ├── DemoController.java
│   │   │   └── KeyVaultController.java
│   │   └── service/
│   │       └── KeyVaultService.java
│   └── resources/
│       └── application.yml
└── test/
    ├── java/com/example/azurekeyvaultdemo/
    │   └── AzureKeyVaultDemoApplicationTests.java
    └── resources/
        └── application-test.yml
```

### Dependencies

- Spring Boot 3.2.0
- Azure Spring Cloud 5.8.0
- Azure Key Vault SDK
- Azure Identity SDK

## License

This project is licensed under the MIT License.