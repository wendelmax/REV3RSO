-- Script para adicionar colunas faltantes
-- Este script é executado pelo Flyway após as migrações anteriores

-- Adiciona coluna status na tabela lances
ALTER TABLE lances ADD COLUMN IF NOT EXISTS status VARCHAR(255) NOT NULL DEFAULT 'ATIVO' CHECK (status IN ('ATIVO', 'CANCELADO', 'VENCEDOR'));

-- Adiciona coluna ultima_atualizacao na tabela usuarios
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS ultima_atualizacao TIMESTAMP;

-- Atualiza os registros existentes
UPDATE lances SET status = status_lance WHERE status_lance IS NOT NULL;
UPDATE usuarios SET ultima_atualizacao = data_atualizacao WHERE data_atualizacao IS NOT NULL; 