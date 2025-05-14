# REV3RSO - Sistema de E-Procurement por Leilão Reverso

O REV3RSO é uma plataforma inovadora que conecta compradores e fornecedores através de leilões reversos, proporcionando transparência, eficiência e economia em suas compras corporativas.

## Características

- Sistema de leilão reverso para compras corporativas
- Interface moderna e responsiva usando o tema Materio
- Suporte a diferentes tipos de usuários (Compradores e Fornecedores)
- Dashboard personalizado por tipo de usuário
- Sistema de notificações em tempo real
- Histórico de leilões e lances
- Relatórios e análises de compras

## Tecnologias Utilizadas

- Java 17
- Quarkus Framework
- PostgreSQL
- Flyway para migrações
- Qute Templates
- Bootstrap 5
- Remix Icon
- ApexCharts

## Requisitos

- Java 17 ou superior
- PostgreSQL 12 ou superior
- Maven 3.8 ou superior

## Configuração

1. Clone o repositório
2. Configure o banco de dados no `application.properties`
3. Execute as migrações do Flyway
4. Execute o projeto com `mvn quarkus:dev`

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   ├── controller/    # Controladores da aplicação
│   │   ├── model/        # Modelos de dados
│   │   ├── repository/   # Repositórios JPA
│   │   ├── service/      # Serviços de negócio
│   │   └── util/         # Utilitários
│   └── resources/
│       ├── META-INF/
│       │   └── resources/
│       │       ├── html/  # Templates Qute
│       │       ├── css/   # Estilos
│       │       └── js/    # Scripts
│       └── db/
│           └── migration/ # Scripts de migração Flyway
```

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## Contato

Para mais informações, entre em contato através do email: contato@rev3rso.com.br

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

- **Backend**: 
  - Java 17
  - Quarkus 3.8.1
  - Hibernate ORM
  - Flyway para migrações / for migrations

- **Frontend**:
  - Bootstrap 5
  - [Materio Bootstrap Admin Template](https://github.com/themeselection/materio-bootstrap-html-admin-template-free)
  - Qute para templates / for templates

- **Banco de Dados / Database**:
  - PostgreSQL 15

## 📋 Pré-requisitos / Prerequisites

- Java 17 ou superior / or higher
- PostgreSQL 15
- Maven 3.8+

## 🔧 Configuração / Setup

1. Clone o repositório / Clone the repository
2. Configure o banco de dados em `application.properties` / Configure the database in `application.properties`
3. Execute as migrações do Flyway / Run Flyway migrations
4. Inicie a aplicação com `mvn quarkus:dev` / Start the application with `mvn quarkus:dev`

### Credenciais do Administrador / Admin Credentials
- Email: `admin@rev3rso.com` ou `admin@rev3rso.com.br`
- Senha: `admin123` ou `admin`

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
- Design moderno e responsivo com Materio
- Componentes Bootstrap 5 otimizados
- Layout vertical com menu lateral
- Suporte a temas claro/escuro
- Componentes UI ricos e personalizáveis
- Ícones Remix
- Formulários e tabelas avançadas
- Suporte a múltiplos navegadores

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
│       └── templates/     # Templates Qute / Qute Templates
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
mvn flyway:migrate
```

3. Inicie a aplicação / Start the application:
```bash
mvn quarkus:dev
```

## 📝 Licença / License

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
This project is under the MIT license. See the [LICENSE](LICENSE) file for more details.

## Créditos / Credits

- Template UI: [Materio Bootstrap Admin Template](https://github.com/themeselection/materio-bootstrap-html-admin-template-free) por ThemeSelection
