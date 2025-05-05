# REV3RSO Docker Deployment Guide

## Overview

This document provides instructions for deploying the REV3RSO e-procurement system using Docker Compose. This containerized setup includes all necessary services for running the application:

- PostgreSQL database
- PgAdmin for database management
- Tomcat application server
- MailHog for email testing

## Prerequisites

- Docker and Docker Compose installed
- Java 11 or later and Maven for building the application
- Git for version control

## Services Configuration

The `docker-compose.yml` file configures the following services:

### PostgreSQL
- **Image**: postgres:14-alpine
- **Port**: 5432:5432
- **Credentials**:
  - Database: rev3rso
  - Username: rev3rso_user
  - Password: rev3rso_password

### PgAdmin
- **Image**: dpage/pgadmin4:6.15
- **Port**: 5050:80
- **Credentials**:
  - Email: admin@rev3rso.com
  - Password: pgadmin_password

### Application Server (Quarkus)
- **Image**: quay.io/quarkus/quarkus-micro-image:2.0
- **Port**: 8080:8080
- **Deployment**: Mounts the Quarkus app directory to /deployments

### MailHog (Email Testing)
- **Image**: mailhog/mailhog:latest
- **Ports**: 
  - 1025:1025 (SMTP)
  - 8025:8025 (Web UI)

## Deployment Instructions

### Automatic Deployment

The easiest way to deploy the REV3RSO system is using the provided deployment script:

```bash
./deploy.sh
```

This script will:
1. Build the application using Maven
2. Rename the WAR file if necessary
3. Start all Docker services
4. Provide access URLs for the application and management tools

### Manual Deployment

If you prefer to deploy manually:

1. Build the application:
   ```bash
   mvn clean package
   ```

2. Rename the WAR file to REV3RSO.war if it has a version number:
   ```bash
   mv ./target/REV3RSO-*.war ./target/REV3RSO.war
   ```

3. Start the Docker services:
   ```bash
   docker-compose up -d
   ```

## Accessing the Application

- **REV3RSO Application**: http://localhost:8080/REV3RSO
- **PgAdmin**: http://localhost:5050
- **MailHog**: http://localhost:8025

## Maintenance

### View Logs

```bash
# View logs for all services
docker-compose logs

# View logs for a specific service
docker-compose logs app
```

### Restart Services

```bash
# Restart all services
docker-compose restart

# Restart a specific service
docker-compose restart app
```

### Stop and Remove

```bash
# Stop all services
docker-compose down

# Stop all services and remove volumes
docker-compose down -v
```

## Database Connection

To connect your development environment to the dockerized database:

- **Host**: localhost
- **Port**: 5432
- **Database**: rev3rso
- **Username**: rev3rso_user
- **Password**: rev3rso_password

## Troubleshooting

- **Application not starting**: Check Tomcat logs with `docker-compose logs app`
- **Database connection issues**: Ensure PostgreSQL is healthy with `docker-compose ps`
- **Email not working**: Check MailHog at http://localhost:8025
