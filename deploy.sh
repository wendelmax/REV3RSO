#!/bin/bash

# REV3RSO Deployment Script
# This script builds and deploys the REV3RSO application using Docker Compose

set -e

ECHO_PREFIX="\033[1;36m[REV3RSO]\033[0m"

echo -e "$ECHO_PREFIX Starting REV3RSO deployment process..."

# Build the application WAR file
echo -e "$ECHO_PREFIX Building application..."
mvn clean package -DskipTests

# Check if WAR file was created
if [ ! -f "./target/REV3RSO.war" ] && [ ! -f "./target/REV3RSO-*.war" ]; then
    echo -e "$ECHO_PREFIX \033[1;31mERROR: WAR file not created. Build failed.\033[0m"
    exit 1
fi

# If the file has a version in its name, rename it to REV3RSO.war
if [ ! -f "./target/REV3RSO.war" ] && [ -f "./target/REV3RSO-"*".war" ]; then
    mv ./target/REV3RSO-*.war ./target/REV3RSO.war
    echo -e "$ECHO_PREFIX Renamed WAR file to REV3RSO.war"
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
