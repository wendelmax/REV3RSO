# Inicialização da Aplicação REV3RSO

Este documento explica o processo de inicialização da aplicação REV3RSO e as melhores práticas adotadas.

## Componentes de Inicialização

O processo de inicialização está dividido em diferentes componentes com responsabilidades específicas:

### 1. AppConfig

A classe `config.AppConfig` é responsável pela configuração central da aplicação:

- Carrega parâmetros de configuração do ambiente
- Registra metadados da aplicação (nome, versão)
- Inicializa componentes de infraestrutura gerais
- **Não manipula dados de negócio**

### 2. DataInitializationService

A classe `service.DataInitializationService` é responsável pela inicialização de dados:

- Inicializa dados padrão para ambiente de desenvolvimento
- Cria usuários, áreas de atuação, formas de pagamento iniciais
- Utiliza o `LaunchMode` para executar apenas em modo de desenvolvimento
- Separa a lógica de negócio da lógica de configuração

## Eventos de Inicialização

Tanto `AppConfig` quanto `DataInitializationService` observam o evento `StartupEvent` para executar operações na inicialização da aplicação:
