#!/bin/bash

# Azure Key Vault Spring Boot Demo Runner Script

echo "Starting Azure Key Vault Spring Boot Demo..."

# Check if .env file exists and source it
if [ -f .env ]; then
    echo "Loading environment variables from .env file..."
    export $(cat .env | xargs)
else
    echo "No .env file found. Please create one based on .env.example"
    echo "Or set the following environment variables:"
    echo "  AZURE_KEYVAULT_ENDPOINT"
    echo "  AZURE_CLIENT_ID"
    echo "  AZURE_CLIENT_SECRET"
    echo "  AZURE_TENANT_ID"
fi

# Check if required environment variables are set
if [ -z "$AZURE_KEYVAULT_ENDPOINT" ] || [ -z "$AZURE_CLIENT_ID" ] || [ -z "$AZURE_CLIENT_SECRET" ] || [ -z "$AZURE_TENANT_ID" ]; then
    echo "Error: Required environment variables are not set."
    echo "Please set AZURE_KEYVAULT_ENDPOINT, AZURE_CLIENT_ID, AZURE_CLIENT_SECRET, and AZURE_TENANT_ID"
    exit 1
fi

echo "Environment variables configured successfully."
echo "Key Vault Endpoint: $AZURE_KEYVAULT_ENDPOINT"
echo "Client ID: ${AZURE_CLIENT_ID:0:8}..."

# Build and run the application
echo "Building the application..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "Build successful. Starting the application..."
    mvn spring-boot:run
else
    echo "Build failed. Please check the error messages above."
    exit 1
fi