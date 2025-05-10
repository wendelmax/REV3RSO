# REV3RSO - Sistema de Leilões Reversos
# REV3RSO - Reverse Auction System

[![Quarkus](https://design.jboss.org/quarkus/logo/final/PNG/quarkus_logo_horizontal_rgb_1280px_default.png#gh-light-mode-only)](https://quarkus.io/#gh-light-mode-only)
[![Quarkus](https://design.jboss.org/quarkus/logo/final/PNG/quarkus_logo_horizontal_rgb_1280px_reverse.png#gh-dark-mode-only)](https://quarkus.io/#gh-dark-mode-only)

[![Java](https://img.shields.io/badge/Java-17-brightgreen.svg?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/license-MIT-brightgreen.svg?style=for-the-badge&logo=apache&color=brightgreen)](LICENSE)

## 🇧🇷 Português

Sistema de leilões reversos desenvolvido com Quarkus, onde o menor lance vence. O sistema inclui um mecanismo de ranking visual (semáforo) para os lances e um sistema de penalidades para fornecedores que desistem.

## 🇺🇸 English

Enterprise-grade reverse auction system developed with Quarkus, where the lowest bid wins. The system includes a visual ranking mechanism (traffic light) for bids and a penalty system for suppliers who withdraw.

## 🚀 Tecnologias / Technologies

- Java 17
- Quarkus 3.8.1
- PostgreSQL 15
- Flyway para migrações / for migrations
- Bootstrap 5 para interface / for interface
- Thymeleaf para templates / for templates

## 📋 Pré-requisitos / Prerequisites

- Java 17 ou superior / or higher
- PostgreSQL 15
- Maven 3.8+

## 🔧 Configuração / Setup

1. Clone o repositório / Clone the repository
2. Configure o banco de dados em `application.properties` / Configure the database in `application.properties`
3. Execute as migrações do Flyway / Run Flyway migrations
4. Inicie a aplicação com `./mvnw quarkus:dev` / Start the application with `./mvnw quarkus:dev`

## 💡 Funcionalidades / Features

### Sistema de Leilões / Auction System
- Criação de leilões por compradores / Auction creation by buyers
- Participação de fornecedores com lances / Supplier participation with bids
- Ranking visual dos lances (semáforo) / Visual bid ranking (traffic light)
- Histórico completo de alterações / Complete change history
- Condições de entrega e pagamento / Delivery and payment conditions

### Sistema de Penalidades / Penalty System
- Penalidades progressivas para desistências / Progressive penalties for withdrawals
- 1ª desistência: 1 mês de suspensão / 1st withdrawal: 1 month suspension
- 2ª desistência: 6 meses de suspensão / 2nd withdrawal: 6 months suspension
- 3ª desistência: banimento permanente / 3rd withdrawal: permanent ban

### Interface Visual / Visual Interface
- Ranking visual com cores (verde/amarelo/vermelho) / Visual ranking with colors (green/yellow/red)
- Status dos lances (ATIVO/CANCELADO/VENCEDOR) / Bid status (ACTIVE/CANCELLED/WINNER)
- Histórico detalhado de alterações / Detailed change history
- Modal com condições de cada lance / Modal with conditions for each bid

## 📦 Estrutura do Projeto / Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── controller/    # Controladores REST / REST Controllers
│   │   ├── model/         # Entidades JPA / JPA Entities
│   │   ├── repository/    # Repositórios / Repositories
│   │   ├── service/       # Lógica de negócio / Business Logic
│   │   └── util/          # Utilitários / Utilities
│   └── resources/
│       ├── db/
│       │   └── migration/ # Scripts SQL / SQL Scripts
│       └── templates/     # Templates Thymeleaf / Thymeleaf Templates
```

## 🔐 Segurança / Security

- Autenticação via JWT / JWT Authentication
- Diferentes níveis de acesso (ADMIN, COMPRADOR, FORNECEDOR) / Different access levels (ADMIN, BUYER, SUPPLIER)
- Validação de permissões por operação / Permission validation per operation
- Registro de auditoria de ações / Action audit logging

## 📊 Banco de Dados / Database

### Principais Tabelas / Main Tables
- `usuarios`: Usuários do sistema / System users
- `leiloes`: Leilões criados / Created auctions
- `lances`: Lances dos fornecedores / Supplier bids
- `historico_lances`: Histórico de alterações / Change history
- `penalidades`: Penalidades aplicadas / Applied penalties
- `convites`: Convites para leilões / Auction invitations

## 🚀 Executando o Projeto / Running the Project

1. Configure o banco de dados / Configure the database:
```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=seu_usuario
quarkus.datasource.password=sua_senha
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/rev3rso
```

2. Execute as migrações / Run migrations:
```bash
./mvnw flyway:migrate
```

3. Inicie a aplicação / Start the application:
```bash
./mvnw quarkus:dev
```

## 📝 Licença / License

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
This project is under the MIT license. See the [LICENSE](LICENSE) file for more details.
