-- Script de criação das tabelas de comunicação
-- Este script é executado pelo Flyway após a criação das tabelas de leilões

-- Criação das sequences
CREATE SEQUENCE IF NOT EXISTS mensagens_seq START 1;
CREATE SEQUENCE IF NOT EXISTS avaliacoes_seq START 1;
CREATE SEQUENCE IF NOT EXISTS anexos_seq START 1;
CREATE SEQUENCE IF NOT EXISTS notificacao_seq START 1;

-- Criação das tabelas de comunicação
CREATE TABLE IF NOT EXISTS mensagens (
    id BIGINT PRIMARY KEY,
    conteudo TEXT NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    respondida BOOLEAN DEFAULT FALSE,
    tipo VARCHAR(50) NOT NULL,
    autor_id BIGINT NOT NULL,
    leilao_id BIGINT NOT NULL,
    mensagem_pai_id BIGINT,
    FOREIGN KEY (autor_id) REFERENCES usuarios(id),
    FOREIGN KEY (leilao_id) REFERENCES leiloes(id),
    FOREIGN KEY (mensagem_pai_id) REFERENCES mensagens(id)
);

CREATE TABLE IF NOT EXISTS avaliacoes (
    id BIGINT PRIMARY KEY,
    nota INTEGER NOT NULL,
    comentario TEXT NOT NULL,
    data_avaliacao TIMESTAMP NOT NULL,
    data_replica TIMESTAMP,
    replica TEXT,
    avaliador_id BIGINT NOT NULL,
    avaliado_id BIGINT NOT NULL,
    leilao_id BIGINT NOT NULL,
    FOREIGN KEY (avaliador_id) REFERENCES usuarios(id),
    FOREIGN KEY (avaliado_id) REFERENCES usuarios(id),
    FOREIGN KEY (leilao_id) REFERENCES leiloes(id)
);

CREATE TABLE IF NOT EXISTS anexos (
    id BIGINT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    caminho_arquivo VARCHAR(255) NOT NULL,
    tipo_arquivo VARCHAR(255),
    tamanho BIGINT,
    data_upload TIMESTAMP NOT NULL,
    leilao_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (leilao_id) REFERENCES leiloes(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS notificacao (
    id BIGINT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    mensagem VARCHAR(1000) NOT NULL,
    tipo SMALLINT,
    data_envio TIMESTAMP NOT NULL,
    data_leitura TIMESTAMP,
    lida BOOLEAN NOT NULL,
    link VARCHAR(255),
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
); 