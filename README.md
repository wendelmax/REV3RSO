# REV3RSO: Sistema de Leilão Reverso de Origem
## Origin Reverse Auction System - Redefining Procurement

[![Quarkus](https://design.jboss.org/quarkus/logo/final/PNG/quarkus_logo_horizontal_rgb_1280px_default.png#gh-light-mode-only)](https://quarkus.io/#gh-light-mode-only)
[![Quarkus](https://design.jboss.org/quarkus/logo/final/PNG/quarkus_logo_horizontal_rgb_1280px_reverse.png#gh-dark-mode-only)](https://quarkus.io/#gh-dark-mode-only)

[![Java EE](https://img.shields.io/badge/Java_EE-11%2B-brightgreen.svg?style=for-the-badge&logo=openjdk)](https://jakarta.ee/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12%2B-blue.svg?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/license-MIT-brightgreen.svg?style=for-the-badge&logo=apache&color=brightgreen)](LICENSE)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg?style=for-the-badge&logo=github-actions)]()
[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg?style=for-the-badge&logo=semver)]()
[![Project Status](https://img.shields.io/badge/status-active-success.svg?style=for-the-badge)]()

**REV3RSO** is an enterprise-grade, cloud-ready e-procurement platform focused on reverse auctions and supplier management.

## Overview
REV3RSO is an online reverse auction system designed to optimize the procurement process between buyers and suppliers. The system enables buyer companies to specify products or services they wish to purchase, invite suppliers, and establish a competitive environment where suppliers compete by offering increasingly lower bids.

## REV3RSO Highlights

* **Cost Optimization**:
  Reduce procurement costs through competitive bidding and automated negotiation processes.
* **Supplier Management**:
  Comprehensive tools for evaluating, comparing, and managing supplier relationships.
* **Transparent Procurement**:
  End-to-end visibility into the purchasing process for all stakeholders.
* **Enterprise Ready**:
  Built for scalability and performance with Java EE standards.
* **Compliance Focused**:
  Ensures adherence to organizational purchasing policies and regulatory requirements.
* **Analytics Driven**:
  Data-driven insights to improve procurement decisions and supplier performance.

_All under ONE integrated platform._

## Purpose
The main purpose of REV3RSO is to help organizations reduce costs during the purchase of goods or services by automating operational purchasing activities and creating a competitive environment that drives prices down. The system provides a transparent, secure, and reliable purchasing methodology.

## Key Features

* **User Management**:
  Comprehensive registration and authentication for buyers and suppliers.
* **Auction Creation**:
  Buyers can create open or private (invitation-only) auctions with detailed specifications.
* **Supplier Invitation**:
  Intelligent supplier selection and invitation system based on categories and performance.
* **Real-time Bidding**:
  Suppliers compete by placing increasingly lower bids with immediate updates.
* **Auction Visualization**:
  Specialized interfaces for buyers and suppliers with real-time analytics.
* **Rating System**:
  Detailed feedback between buyers and suppliers to build trust and accountability.
* **Messaging System**:
  Secure communication during the auction for clarifications and negotiations.
* **User Control**:
  Administrative tools to manage user accounts with audit trails.

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

## Getting Started

* [Documentation](docs/)
* [Installation Guide](docs/installation.md)
* [User Guide](docs/user-guide.md)
* [API Documentation](docs/api-docs.md)

## Development Resources

* [Contributing Guidelines](CONTRIBUTING.md)
* [Issue Tracker](issues/)
* [Development Roadmap](docs/roadmap.md)

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

## Requisitos
- Java 17
- Maven
- PostgreSQL

## Configuração do Banco de Dados
1. Crie um banco de dados PostgreSQL chamado `rev3rso`
2. Crie um usuário `rev3rso_user` com senha `rev3rso_password`
3. Conceda todas as permissões do banco `rev3rso` para o usuário `rev3rso_user`

## Executando a Aplicação
1. Clone o repositório
2. Execute `mvn quarkus:dev`
3. Acesse a aplicação em `http://localhost:8080`
4. Acesse a documentação da API em `http://localhost:8080/q/swagger-ui/`

## Documentação da API

### Swagger UI
A documentação interativa da API está disponível em:
- `http://localhost:8080/q/swagger-ui/` - Interface Swagger UI
- `http://localhost:8080/q/openapi` - Especificação OpenAPI

### Endpoints JSON
Os seguintes endpoints retornam respostas em formato JSON:

#### Leilões
- `GET /leiloes` - Lista paginada de leilões
- `GET /leiloes/{id}` - Detalhes de um leilão específico

#### Notificações
- `GET /notificacoes/nao-lidas/count` - Contagem de notificações não lidas
- `GET /notificacoes/recentes` - Lista de notificações recentes

## Funcionalidades
- Cadastro de usuários (compradores e fornecedores)
- Criação e gerenciamento de leilões
- Sistema de lances
- Notificações
- Avaliações

## Recursos Disponíveis

### Endpoints da API

#### Avaliações
- `POST /avaliacoes/avaliar` - Criar nova avaliação (form-urlencoded)
- `POST /avaliacoes/replica/{id}` - Replicar avaliação
- `POST /avaliacoes/replicar` - Replicar avaliação (form-urlencoded)
- `POST /avaliacoes/salvar` - Salvar avaliação
- `GET /avaliacoes/avaliar/{leilaoId}/{avaliado}/{avaliadoId}` - Formulário de avaliação
- `GET /avaliacoes/minhas` - Listar minhas avaliações
- `GET /avaliacoes/recebidas` - Listar avaliações recebidas

#### Convites
- `POST /convites/enviar` - Enviar convite (form)
- `GET /convites/aceitar/{id}/{conviteId}` - Aceitar convite
- `GET /convites/listar/{leilaoId}` - Listar convites do leilão
- `GET /convites/recebidos` - Listar convites recebidos
- `GET /convites/recusar/{id}/{conviteId}` - Recusar convite
- `GET /convites/selecionar/{leilaoId}` - Selecionar fornecedores para convite

#### Notificações
- `POST /notificacoes/marcar-lida/{id}` - Marcar notificação como lida
- `POST /notificacoes/marcar-todas-lidas` - Marcar todas como lidas
- `GET /notificacoes/listar` - Listar notificações

#### Administração
- `POST /admin/areas/excluir` - Excluir área
- `POST /admin/areas/salvar` - Salvar área
- `POST /admin/formas-pagamento/excluir` - Excluir forma de pagamento
- `POST /admin/formas-pagamento/salvar` - Salvar forma de pagamento
- `POST /admin/usuarios/ativar/{id}` - Ativar usuário
- `POST /admin/usuarios/resetar-senha/{id}` - Resetar senha
- `POST /admin/usuarios/salvar/{id}` - Salvar usuário
- `POST /admin/usuarios/suspender/{id}` - Suspender usuário
- `GET /admin/areas` - Listar áreas
- `GET /admin/dashboard` - Dashboard administrativo
- `GET /admin/formas-pagamento` - Listar formas de pagamento
- `GET /admin/usuarios` - Listar usuários
- `GET /admin/usuarios/editar/{id}` - Editar usuário

#### Lances
- `POST /lances/dar` - Dar lance (form)
- `GET /lances/historico/{leilaoId}` - Histórico de lances

#### Usuários
- `POST /usuarios/atualizar` - Atualizar usuário (form)
- `POST /usuarios/autenticar` - Autenticar (form)
- `POST /usuarios/cadastrar` - Cadastrar (form)
- `GET /usuarios/cadastro` - Formulário de cadastro
- `GET /usuarios/editar` - Editar perfil
- `GET /usuarios/fornecedores` - Listar fornecedores
- `GET /usuarios/login` - Página de login
- `GET /usuarios/logout` - Logout
- `GET /usuarios/perfil` - Meu perfil
- `GET /usuarios/perfil/{id}` - Perfil de usuário

#### Leilões
- `POST /leiloes/cancelar/{id}` - Cancelar leilão (form)
- `POST /leiloes/enviarConvites/{id}/{leilaoId}` - Enviar convites (form)
- `POST /leiloes/salvar` - Salvar leilão (form)
- `GET /leiloes` - Listar leilões
- `GET /leiloes/{id}` - Detalhes do leilão
- `GET /leiloes/convidar/{id}` - Convidar fornecedores
- `GET /leiloes/criar` - Criar leilão
- `GET /leiloes/disponiveis` - Leilões disponíveis
- `GET /leiloes/listarLeiloes` - Listar leilões
- `GET /leiloes/meus` - Meus leilões
- `GET /leiloes/visualizar/{id}` - Visualizar leilão

#### Mensagens
- `POST /mensagens/perguntar` - Enviar pergunta
- `POST /mensagens/responder` - Responder mensagem (form)
- `GET /mensagens/leilao/{id}/{leilaoId}` - Mensagens do leilão
- `GET /mensagens/responder/{id}/{mensagemId}` - Responder mensagem

### Recursos de Desenvolvimento

#### Dev UI
- `http://localhost:8080/q/dev-ui` - Interface de desenvolvimento
- `http://localhost:8080/q/arc` - Visão geral do CDI
- `http://localhost:8080/q/arc/beans` - Beans CDI ativos
- `http://localhost:8080/q/arc/observers` - Observadores CDI ativos
- `http://localhost:8080/q/arc/removed-beans` - Beans CDI removidos

#### Documentação da API
- `http://localhost:8080/q/openapi` - Documento OpenAPI
- `http://localhost:8080/q/swagger-ui` - Interface Swagger UI

#### Recursos Estáticos
- `http://localhost:8080/static/bundle/main.css` - CSS principal
- `http://localhost:8080/static/bundle/main.js` - JavaScript principal
- `http://localhost:8080/static/logo.svg` - Logo do sistema
- `http://localhost:8080/web-bundler.html` - Bundler web
