-- Script de criação das tabelas relacionadas a leilões
-- Este script é executado pelo Flyway após a criação das tabelas base

-- Criação das sequences
CREATE SEQUENCE IF NOT EXISTS leiloes_seq START 1;
CREATE SEQUENCE IF NOT EXISTS lances_seq START 1;
CREATE SEQUENCE IF NOT EXISTS convites_seq START 1;
CREATE SEQUENCE IF NOT EXISTS penalidades_seq START 1;
CREATE SEQUENCE IF NOT EXISTS historico_lances_seq START 1;

-- Criação das tabelas de leilões
CREATE TABLE IF NOT EXISTS lances (
    id BIGINT PRIMARY KEY,
    valor NUMERIC(38,2) NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    data_cancelamento TIMESTAMP,
    motivo_cancelamento VARCHAR(255),
    cancelado BOOLEAN DEFAULT FALSE,
    vencedor BOOLEAN DEFAULT FALSE,
    condicoes_entrega VARCHAR(255),
    prazo_entrega INTEGER,
    prazo_pagamento INTEGER,
    leilao_id BIGINT NOT NULL,
    fornecedor_id BIGINT NOT NULL,
    status_lance VARCHAR(20) NOT NULL DEFAULT 'ATIVO', -- ATIVO, CANCELADO, VENCEDOR
    posicao_ranking INTEGER, -- Posição atual do lance no ranking (1 = melhor)
    FOREIGN KEY (fornecedor_id) REFERENCES usuarios(id)
);

-- Tabela para histórico de alterações nos lances
CREATE TABLE IF NOT EXISTS historico_lances (
    id BIGINT PRIMARY KEY,
    lance_id BIGINT NOT NULL,
    valor_anterior NUMERIC(38,2),
    valor_novo NUMERIC(38,2),
    posicao_anterior INTEGER,
    posicao_nova INTEGER,
    data_alteracao TIMESTAMP NOT NULL,
    tipo_alteracao VARCHAR(20) NOT NULL, -- NOVO_LANCE, CANCELAMENTO, ATUALIZACAO_RANKING
    FOREIGN KEY (lance_id) REFERENCES lances(id)
);

CREATE TABLE IF NOT EXISTS leiloes (
    id BIGINT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    valor_inicial NUMERIC(38,2) NOT NULL,
    valor_vencedor NUMERIC(38,2),
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL, -- ABERTO, FECHADO, CONCLUIDO, CANCELADO
    especificacoes_tecnicas TEXT,
    quantidade INTEGER,
    unidade_medida VARCHAR(255),
    tipo_leilao VARCHAR(20) NOT NULL, -- ABERTO, FECHADO
    tipo_requisicao VARCHAR(20) NOT NULL, -- COMPRA, SERVICO
    valor_referencia NUMERIC(38,2),
    melhor_oferta NUMERIC(38,2),
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    data_cancelamento TIMESTAMP,
    motivo_cancelamento VARCHAR(255),
    criador_id BIGINT NOT NULL,
    categoria_id BIGINT,
    forma_pagamento_id BIGINT NOT NULL,
    lance_vencedor_id BIGINT,
    total_lances INTEGER DEFAULT 0,
    total_fornecedores INTEGER DEFAULT 0,
    FOREIGN KEY (criador_id) REFERENCES usuarios(id),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    FOREIGN KEY (forma_pagamento_id) REFERENCES formas_pagamento(id),
    FOREIGN KEY (lance_vencedor_id) REFERENCES lances(id)
);

-- Tabela para controle de penalidades
CREATE TABLE IF NOT EXISTS penalidades (
    id BIGINT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    leilao_id BIGINT NOT NULL,
    tipo_penalidade VARCHAR(20) NOT NULL, -- CANCELAMENTO_LANCE, RETIRADA_LEILAO
    data_ocorrencia TIMESTAMP NOT NULL,
    data_fim_penalidade TIMESTAMP NOT NULL,
    motivo VARCHAR(255),
    nivel_penalidade INTEGER NOT NULL, -- 1 = 1 mês, 2 = 6 meses, 3 = banimento
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (leilao_id) REFERENCES leiloes(id)
);

-- Adicionando a chave estrangeira de leilao_id após a criação da tabela leiloes
ALTER TABLE lances ADD CONSTRAINT fk_lances_leilao FOREIGN KEY (leilao_id) REFERENCES leiloes(id);

CREATE TABLE IF NOT EXISTS convites (
    id BIGINT PRIMARY KEY,
    data_criacao TIMESTAMP NOT NULL,
    data_envio TIMESTAMP NOT NULL,
    data_resposta TIMESTAMP,
    mensagem TEXT,
    motivo_recusa VARCHAR(255),
    status VARCHAR(20) NOT NULL, -- PENDENTE, ACEITO, RECUSADO
    fornecedor_id BIGINT NOT NULL,
    leilao_id BIGINT NOT NULL,
    FOREIGN KEY (fornecedor_id) REFERENCES usuarios(id),
    FOREIGN KEY (leilao_id) REFERENCES leiloes(id)
); 