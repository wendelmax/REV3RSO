-- Script de criação das tabelas base do sistema
-- Este script é executado pelo Flyway na inicialização do banco de dados

-- Criação das sequences
CREATE SEQUENCE IF NOT EXISTS usuarios_seq START 1;
CREATE SEQUENCE IF NOT EXISTS areas_atuacao_seq START 1;
CREATE SEQUENCE IF NOT EXISTS formas_pagamento_seq START 1;
CREATE SEQUENCE IF NOT EXISTS categorias_seq START 1;

-- Criação das tabelas base
CREATE TABLE IF NOT EXISTS areas_atuacao (
    id BIGINT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255) NOT NULL UNIQUE,
    detalhes TEXT
);

CREATE TABLE IF NOT EXISTS formas_pagamento (
    id BIGINT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    descricao VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    pontuacao FLOAT,
    cpf VARCHAR(20),
    razao_social VARCHAR(255),
    nome_fantasia VARCHAR(255),
    cnpj VARCHAR(20),
    endereco VARCHAR(255),
    cidade VARCHAR(255),
    uf VARCHAR(2),
    cep VARCHAR(10),
    telefone VARCHAR(20),
    motivo_suspensao VARCHAR(255),
    data_suspensao TIMESTAMP,
    data_reativacao TIMESTAMP,
    total_avaliacoes BIGINT DEFAULT 0,
    data_cadastro TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL,
    ultimo_acesso TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuario_areas (
    usuario_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, area_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (area_id) REFERENCES areas_atuacao(id)
); 