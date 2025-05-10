-- Corrige a geração de ID da tabela areas_atuacao

-- Recria a sequência com as configurações corretas
DROP SEQUENCE IF EXISTS areas_atuacao_seq;
CREATE SEQUENCE areas_atuacao_seq START 1 INCREMENT 1;

-- Remove as foreign keys existentes
ALTER TABLE usuario_areas DROP CONSTRAINT IF EXISTS usuario_areas_area_id_fkey;
ALTER TABLE usuario_areas DROP CONSTRAINT IF EXISTS usuario_areas_usuario_id_fkey;

-- Recria a tabela com a configuração correta de ID
CREATE TABLE IF NOT EXISTS areas_atuacao_new (
    id BIGINT PRIMARY KEY DEFAULT nextval('areas_atuacao_seq'),
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255) NOT NULL UNIQUE,
    detalhes TEXT
);

-- Copia os dados existentes
INSERT INTO areas_atuacao_new (nome, descricao, detalhes)
SELECT nome, descricao, detalhes FROM areas_atuacao;

-- Remove a tabela antiga
DROP TABLE IF EXISTS areas_atuacao;

-- Renomeia a nova tabela
ALTER TABLE areas_atuacao_new RENAME TO areas_atuacao;

-- Recria as foreign keys
ALTER TABLE usuario_areas 
    ADD CONSTRAINT fk_usuario_areas_area 
    FOREIGN KEY (area_id) 
    REFERENCES areas_atuacao(id);

ALTER TABLE usuario_areas 
    ADD CONSTRAINT fk_usuario_areas_usuario 
    FOREIGN KEY (usuario_id) 
    REFERENCES usuarios(id); 