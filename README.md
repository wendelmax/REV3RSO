# REV3RSO

## Overview
REV3RSO is an online reverse auction system designed to optimize the procurement process between buyers and suppliers. The system enables buyer companies to specify products or services they wish to purchase, invite suppliers, and establish a competitive environment where suppliers compete by offering increasingly lower bids.

## Purpose
The main purpose of REV3RSO is to help organizations reduce costs during the purchase of goods or services by automating operational purchasing activities and creating a competitive environment that drives prices down. The system provides a transparent, secure, and reliable purchasing methodology.

## Key Features

- **User Management**: Registration and authentication for buyers and suppliers
- **Auction Creation**: Buyers can create open or private (invitation-only) auctions
- **Supplier Invitation**: Ability to invite specific suppliers to participate in auctions
- **Real-time Bidding**: Suppliers compete by placing increasingly lower bids
- **Auction Visualization**: Different interfaces for buyers and suppliers
- **Rating System**: Feedback between buyers and suppliers
- **Messaging System**: Communication during the auction
- **User Control**: Suspend/reactivate user accounts (admin module)

## Technology Stack

REV3RSO is built with a modern Java-based approach:

- **Backend**: Java with MVC pattern
- **Database**: PostgreSQL with Hibernate ORM
- **Web Interface**: HTML/CSS/JavaScript
- **API**: RESTful services with JAX-RS
- **Framework**: Java EE/Jakarta EE
- **Reporting**: Custom reporting system

## Project Evolution

The REV3RSO project has an interesting technological journey:

- **Original Concept (2013)**: Initially developed as an academic project at IBTA college using PHP with MySQL database, following a traditional layered architecture (Business, DALC, Entities, WebSite)
- **First Evolution**: Later restructured with .NET technologies
- **Current Version**: Completely modernized using Java with a focus on maintainability, security, and performance

## Architecture

The application follows a clean architecture approach with a layered design:

### Domain Layer
- **`model` package**: Contains all domain entities with Hibernate ORM annotations
  - Core business entities (Usuario, Leilao, Lance, Avaliacao, etc.)
  - Uses JPA for data persistence
  - Implements business logic that belongs directly to the entity

### Controllers
- **`controller` package**: Contains all controllers for handling HTTP requests
  - Each controller handles a specific domain area (e.g., UsuarioController, LeilaoController)
  - Follows a RESTful approach with standard CRUD operations and domain-specific actions
  - Returns appropriate responses based on the action performed

### Data Transfer Objects
- **`dto` package**: Objects for transferring data between layers
  - Simplifies API interactions
  - Allows for data validation and transformation

### Services
- **`service` package**: Contains business logic spanning multiple entities
  - Implements transaction management and complex operations
  - Contains service implementations for core system functionality

### Utilities
- **`util` package**: Cross-cutting concerns and helper classes
  - Email sending
  - Date/time utilities
  - Formatting helpers

### Additional Components
- **`annotation` package**: Custom annotations for the system
- **`config` package**: Configuration classes for the application
- **`exception` package**: Custom exception handling
- **`interceptor` package**: Interceptors for cross-cutting concerns

### Project Structure
```
src/
└── main/
    ├── java/
    │   ├── annotation/        # Custom annotations
    │   ├── config/            # Application configuration
    │   ├── controller/        # MVC controllers
    │   │   ├── UsuarioController.java
    │   │   ├── LeilaoController.java
    │   │   ├── LanceController.java
    │   │   └── AvaliacaoController.java
    │   ├── dto/               # Data transfer objects
    │   ├── exception/         # Custom exceptions
    │   ├── interceptor/       # Custom interceptors
    │   ├── model/             # Domain entities with JPA
    │   │   ├── Usuario.java
    │   │   ├── Leilao.java
    │   │   ├── Lance.java
    │   │   └── Avaliacao.java
    │   ├── service/           # Business services
    │   │   ├── UsuarioService.java
    │   │   ├── LeilaoService.java
    │   │   └── NotificacaoService.java
    │   └── util/              # Utility classes
    │       ├── EmailUtil.java
    │       └── DateUtil.java
    ├── resources/
    │   ├── static/            # Static resources (CSS/JS)
    │   │   ├── css/
    │   │   └── js/
    │   ├── templates/         # HTML templates
    │   │   ├── usuario/
    │   │   ├── leilao/
    │   │   └── dashboard.html
    │   └── application.properties
    └── webapp/                # Web application files
        ├── WEB-INF/
        └── index.html
```

### Database Schema

The database design follows a normalized structure with the following core tables:

- **usuarios**: Stores user information (both buyers and suppliers)
- **leiloes**: Auction details, including start/end dates, requirements
- **lances**: Bids placed by suppliers on auctions
- **mensagens**: Messages exchanged during auctions
- **avaliacoes**: Ratings between users after completed auctions
- **convites**: Invitations sent to suppliers for private auctions

## Benefits

- **Cost Reduction**: Drive down procurement costs through competitive bidding
- **Time Optimization**: Reduce negotiation time and streamline the purchasing process
- **Transparency**: More transparent purchasing methodology
- **Process Automation**: Automate operational purchasing activities
- **Supplier Relationship**: Foster healthy competition among suppliers

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Development Guide

This project uses standard Java EE/Jakarta EE technologies with Maven for build management.

### Prerequisites

- JDK 11 or later
- Maven 3.8 or later
- PostgreSQL 12 or later

### Running the application in dev mode

You can run your application in dev mode using:

```shell script
mvn clean install
mvn tomcat7:run
```

Alternatively, you can deploy the WAR file to a compatible application server.

### Packaging the application

The application can be packaged using:

```shell script
mvn clean package
```

It produces a WAR file in the `target/` directory that can be deployed to any Java EE compatible application server.

### Database Setup

The application requires a PostgreSQL database. You can configure the database connection in `src/main/resources/application.properties`.

### Accessing the Application

- Main application: http://localhost:8080/REV3RSO/
- Admin panel: http://localhost:8080/REV3RSO/admin/

### API Documentation

RESTful API endpoints are available at: http://localhost:8080/REV3RSO/api/
