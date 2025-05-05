#!/bin/bash

# REV3RSO Deployment Script
# This script builds and deploys the REV3RSO application using Docker Compose

set -e

ECHO_PREFIX="\033[1;36m[REV3RSO]\033[0m"

echo -e "$ECHO_PREFIX Starting REV3RSO deployment process..."

# Build the Quarkus application
echo -e "$ECHO_PREFIX Building Quarkus application..."
mvn clean package -DskipTests

# Check if Quarkus application files were created
if [ ! -d "./target/quarkus-app" ]; then
    echo -e "$ECHO_PREFIX \033[1;31mERROR: Quarkus application files not created. Build failed.\033[0m"
    exit 1
fi

# Start Docker Compose services
echo -e "$ECHO_PREFIX Starting Docker containers..."
docker-compose up -d

echo -e "$ECHO_PREFIX Waiting for services to be fully up..."
sleep 10

echo -e "$ECHO_PREFIX \033[1;32mDeployment complete!\033[0m"
echo -e "$ECHO_PREFIX Access the application at http://localhost:8080/REV3RSO"
echo -e "$ECHO_PREFIX Access PgAdmin at http://localhost:5050"
echo -e "$ECHO_PREFIX   - Email: admin@rev3rso.com"
echo -e "$ECHO_PREFIX   - Password: pgadmin_password"
echo -e "$ECHO_PREFIX Access MailHog at http://localhost:8025"
