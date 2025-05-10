-- Criação das tabelas principais
CREATE TABLE areas_atuacao (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    detalhes TEXT
);

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255) NOT NULL,
    cnpj VARCHAR(255) NOT NULL UNIQUE,
    endereco VARCHAR(255) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    uf VARCHAR(255) NOT NULL,
    cep VARCHAR(255) NOT NULL,
    telefone VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    motivo_suspensao VARCHAR(255),
    data_suspensao TIMESTAMP,
    data_reativacao TIMESTAMP,
    tipo_usuario VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    pontuacao DOUBLE PRECISION,
    total_avaliacoes BIGINT,
    data_cadastro TIMESTAMP NOT NULL,
    ultima_atualizacao TIMESTAMP,
    ultimo_acesso TIMESTAMP
);

CREATE TABLE usuario_areas (
    usuario_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, area_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (area_id) REFERENCES areas_atuacao(id)
);

CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    descricao VARCHAR(255)
);

CREATE TABLE formas_pagamento (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE leiloes (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    valor_inicial NUMERIC(38,2) NOT NULL,
    valor_vencedor NUMERIC(38,2),
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP NOT NULL,
    status VARCHAR(255) NOT NULL,
    especificacoes_tecnicas TEXT,
    quantidade INTEGER,
    unidade_medida VARCHAR(255),
    tipo_leilao VARCHAR(255) NOT NULL,
    tipo_requisicao VARCHAR(255) NOT NULL,
    valor_referencia NUMERIC(38,2),
    melhor_oferta NUMERIC(38,2),
    data_atualizacao TIMESTAMP,
    data_cancelamento TIMESTAMP,
    motivo_cancelamento VARCHAR(255),
    criador_id BIGINT NOT NULL,
    categoria_id BIGINT,
    forma_pagamento_id BIGINT NOT NULL,
    lance_vencedor_id BIGINT,
    FOREIGN KEY (criador_id) REFERENCES usuarios(id),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    FOREIGN KEY (forma_pagamento_id) REFERENCES formas_pagamento(id)
);

CREATE TABLE lances (
    id BIGSERIAL PRIMARY KEY,
    valor NUMERIC(38,2) NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    data_cancelamento TIMESTAMP,
    motivo_cancelamento VARCHAR(255),
    cancelado BOOLEAN,
    vencedor BOOLEAN,
    condicoes_entrega VARCHAR(255),
    prazo_entrega INTEGER,
    prazo_pagamento INTEGER,
    leilao_id BIGINT NOT NULL,
    fornecedor_id BIGINT NOT NULL,
    FOREIGN KEY (leilao_id) REFERENCES leiloes(id),
    FOREIGN KEY (fornecedor_id) REFERENCES usuarios(id)
);

CREATE TABLE avaliacoes (
    id BIGSERIAL PRIMARY KEY,
    nota INTEGER NOT NULL,
    comentario TEXT,
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

CREATE TABLE anexos (
    id BIGSERIAL PRIMARY KEY,
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

CREATE TABLE mensagens (
    id BIGSERIAL PRIMARY KEY,
    conteudo TEXT NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    respondida BOOLEAN,
    tipo VARCHAR(255) NOT NULL,
    autor_id BIGINT NOT NULL,
    leilao_id BIGINT NOT NULL,
    mensagem_pai_id BIGINT,
    FOREIGN KEY (autor_id) REFERENCES usuarios(id),
    FOREIGN KEY (leilao_id) REFERENCES leiloes(id),
    FOREIGN KEY (mensagem_pai_id) REFERENCES mensagens(id)
);

CREATE TABLE notificacao (
    id BIGSERIAL PRIMARY KEY,
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

CREATE TABLE tokens_recuperacao (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    data_criacao TIMESTAMP NOT NULL,
    data_expiracao TIMESTAMP NOT NULL,
    data_utilizacao TIMESTAMP,
    utilizado BOOLEAN NOT NULL,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE convites (
    id BIGSERIAL PRIMARY KEY,
    data_criacao TIMESTAMP NOT NULL,
    data_envio TIMESTAMP NOT NULL,
    data_resposta TIMESTAMP,
    mensagem TEXT,
    motivo_recusa VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    fornecedor_id BIGINT NOT NULL,
    leilao_id BIGINT NOT NULL,
    FOREIGN KEY (fornecedor_id) REFERENCES usuarios(id),
    FOREIGN KEY (leilao_id) REFERENCES leiloes(id)
); 