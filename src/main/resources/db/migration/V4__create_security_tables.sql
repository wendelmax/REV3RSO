-- Script de criação das tabelas de segurança
-- Este script é executado pelo Flyway após a criação das tabelas de comunicação

-- Criação das sequences
CREATE SEQUENCE IF NOT EXISTS tokens_recuperacao_seq START 1;

-- Criação das tabelas de segurança
CREATE TABLE IF NOT EXISTS tokens_recuperacao (
    id BIGINT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    data_criacao TIMESTAMP NOT NULL,
    data_expiracao TIMESTAMP NOT NULL,
    data_utilizacao TIMESTAMP,
    utilizado BOOLEAN NOT NULL,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
); 