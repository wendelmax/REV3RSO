-- Criação das sequências
CREATE SEQUENCE IF NOT EXISTS usuarios_seq START 1;
CREATE SEQUENCE IF NOT EXISTS areas_atuacao_seq START 1;
CREATE SEQUENCE IF NOT EXISTS leiloes_seq START 1;
CREATE SEQUENCE IF NOT EXISTS lances_seq START 1;
CREATE SEQUENCE IF NOT EXISTS avaliacoes_seq START 1;

-- Criação da tabela de áreas de atuação
CREATE TABLE IF NOT EXISTS areas_atuacao (
    id BIGINT PRIMARY KEY DEFAULT nextval('areas_atuacao_seq'),
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    detalhes TEXT
);

-- Criação da tabela de usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT PRIMARY KEY DEFAULT nextval('usuarios_seq'),
    razao_social VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255) NOT NULL,
    cnpj VARCHAR(255) NOT NULL UNIQUE,
    endereco VARCHAR(255) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    cep VARCHAR(8) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    motivo_suspensao TEXT,
    data_suspensao TIMESTAMP,
    data_reativacao TIMESTAMP,
    tipo_usuario VARCHAR(255) NOT NULL CHECK (tipo_usuario IN ('COMPRADOR', 'FORNECEDOR', 'ADMINISTRADOR')),
    status VARCHAR(255) NOT NULL CHECK (status IN ('ATIVO', 'SUSPENSO', 'INATIVO')),
    pontuacao DOUBLE PRECISION DEFAULT 0,
    total_avaliacoes BIGINT DEFAULT 0,
    data_cadastro TIMESTAMP NOT NULL,
    ultima_atualizacao TIMESTAMP,
    ultimo_acesso TIMESTAMP
);

-- Criação da tabela de relacionamento entre usuários e áreas de atuação
CREATE TABLE IF NOT EXISTS usuario_areas (
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    area_id BIGINT NOT NULL REFERENCES areas_atuacao(id),
    PRIMARY KEY (usuario_id, area_id)
);

-- Criação da tabela de leilões
CREATE TABLE IF NOT EXISTS leiloes (
    id BIGINT PRIMARY KEY DEFAULT nextval('leiloes_seq'),
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    valor_inicial DOUBLE PRECISION NOT NULL,
    valor_minimo DOUBLE PRECISION,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP NOT NULL,
    status VARCHAR(255) NOT NULL CHECK (status IN ('ABERTO', 'FECHADO', 'CANCELADO', 'FINALIZADO')),
    criador_id BIGINT NOT NULL REFERENCES usuarios(id),
    vencedor_id BIGINT REFERENCES usuarios(id),
    valor_vencedor DOUBLE PRECISION,
    data_criacao TIMESTAMP NOT NULL,
    ultima_atualizacao TIMESTAMP
);

-- Criação da tabela de lances
CREATE TABLE IF NOT EXISTS lances (
    id BIGINT PRIMARY KEY DEFAULT nextval('lances_seq'),
    leilao_id BIGINT NOT NULL REFERENCES leiloes(id),
    fornecedor_id BIGINT NOT NULL REFERENCES usuarios(id),
    valor DOUBLE PRECISION NOT NULL,
    data_lance TIMESTAMP NOT NULL,
    status VARCHAR(255) NOT NULL CHECK (status IN ('ATIVO', 'CANCELADO', 'VENCEDOR'))
);

-- Criação da tabela de avaliações
CREATE TABLE IF NOT EXISTS avaliacoes (
    id BIGINT PRIMARY KEY DEFAULT nextval('avaliacoes_seq'),
    avaliador_id BIGINT NOT NULL REFERENCES usuarios(id),
    avaliado_id BIGINT NOT NULL REFERENCES usuarios(id),
    leilao_id BIGINT NOT NULL REFERENCES leiloes(id),
    nota INTEGER NOT NULL CHECK (nota BETWEEN 1 AND 5),
    comentario TEXT,
    data_avaliacao TIMESTAMP NOT NULL
); 