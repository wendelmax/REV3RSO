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

REV3RSO is built on modern Quarkus technologies with a clean architecture approach:

- **Backend**: Quarkus with MVC pattern
- **Database**: PostgreSQL with Hibernate ORM
- **Web Interface**: Qute templating engine
- **API**: RESTful services with JAX-RS
- **Reactive Components**: Mutiny for reactive programming
- **Reporting**: Custom reporting system

## Project Evolution

The REV3RSO project has an interesting technological journey:

- **Original Concept (2013)**: Initially developed as an academic project at IBTA college using PHP with MySQL database, following a traditional layered architecture (Business, DALC, Entities, WebSite)
- **First Evolution**: Later restructured with .NET technologies
- **Current Version**: Completely modernized using Quarkus, a Kubernetes-native Java framework optimized for containers, microservices, and cloud deployments

## Architecture

The application follows the Quarkus Renarde MVC architecture, which is a modern, efficient approach for web applications:

### Domain Layer
- **`model` package**: Contains all domain entities with Hibernate ORM annotations
  - Core business entities (Usuario, Leilao, Lance, Avaliacao, etc.)
  - Uses Panache for simplified data access patterns
  - Implements business logic that belongs directly to the entity

### Controllers
- **`controller` package**: Contains all Renarde controllers (formerly in the `rest` package)
  - Each controller handles a specific domain area (e.g., UsuarioController, LeilaoController)
  - Follows a RESTful approach with standard CRUD operations and domain-specific actions
  - Uses Renarde annotations to simplify routing, validation, and form handling
  - Returns appropriate template instances based on the action performed

### Views
- **`resources/templates` directory**: Contains Qute templates for the UI
  - Organized in subdirectories matching controller names
  - Uses template inheritance (extending main.html)
  - Implements responsive layouts suitable for both desktop and mobile

### Services
- **`service` package**: Contains business logic spanning multiple entities
  - Implements transaction management and complex operations
  - Contains service interfaces and their implementations

### Security
- **`security` package**: Manages authentication, authorization, and audit
  - Defines user roles and permissions
  - Handles session management

### Utilities
- **`util` package**: Cross-cutting concerns and helper classes
  - Email sending
  - Date/time utilities
  - Formatting helpers

### Project Structure
```
src/
└── main/
    ├── java/
    │   ├── model/             # Domain entities with Panache
    │   │   ├── Usuario.java
    │   │   ├── Leilao.java
    │   │   ├── Lance.java
    │   │   └── Avaliacao.java
    │   ├── controller/        # Renarde MVC controllers
    │   │   ├── UsuarioController.java
    │   │   ├── LeilaoController.java
    │   │   ├── LanceController.java
    │   │   └── AvaliacaoController.java
    │   ├── service/           # Business services
    │   │   ├── LeilaoService.java
    │   │   ├── NotificacaoService.java
    │   │   └── ReportService.java
    │   ├── security/          # Security configurations
    │   │   └── SecurityConfig.java
    │   └── util/              # Utility classes
    │       ├── EmailSender.java
    │       └── DateUtils.java
    ├── resources/
    │   ├── templates/         # Qute templates
    │   │   ├── Usuario/       # Templates for user management
    │   │   ├── Leilao/        # Templates for auction views
    │   │   ├── Lance/         # Templates for bidding 
    │   │   └── main.html      # Base template
    │   ├── static/            # Static resources (CSS/JS)
    │   │   ├── css/
    │   │   └── js/
    │   └── application.properties
    └── docker/                # Docker configurations
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

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

### Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it's not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

### Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/rev3rso-1.0.0-SNAPSHOT-runner`

### Extensions Used

- **Renarde**: A server-side Web Framework based on Quarkus, Qute, Hibernate and RESTEasy Reactive.
- **Web Bundler**: Creating full-stack Web Apps with zero config bundling for web-app scripts (js, jsx, ts, tsx), dependencies and styles (css, scss, sass).

### Accessing the Application

- Main application: http://localhost:8080/
- Renarde UI: http://localhost:8080/renarde
- Dev UI: http://localhost:8080/q/dev/
