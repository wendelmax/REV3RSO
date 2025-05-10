-- Script para limpar o banco de dados antes das migrações
-- Este script é executado pelo Flyway antes de qualquer outra migração

-- Desabilita as restrições de chave estrangeira temporariamente
SET CONSTRAINTS ALL DEFERRED;

-- Remove as tabelas na ordem correta para evitar problemas com chaves estrangeiras
DROP TABLE IF EXISTS tokens_recuperacao CASCADE;
DROP TABLE IF EXISTS notificacao CASCADE;
DROP TABLE IF EXISTS anexos CASCADE;
DROP TABLE IF EXISTS avaliacoes CASCADE;
DROP TABLE IF EXISTS mensagens CASCADE;
DROP TABLE IF EXISTS convites CASCADE;
DROP TABLE IF EXISTS lances CASCADE;
DROP TABLE IF EXISTS leiloes CASCADE;
DROP TABLE IF EXISTS usuario_areas CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS formas_pagamento CASCADE;
DROP TABLE IF EXISTS areas_atuacao CASCADE;

-- Remove as sequences
DROP SEQUENCE IF EXISTS tokens_recuperacao_seq CASCADE;
DROP SEQUENCE IF EXISTS notificacao_seq CASCADE;
DROP SEQUENCE IF EXISTS anexos_seq CASCADE;
DROP SEQUENCE IF EXISTS avaliacoes_seq CASCADE;
DROP SEQUENCE IF EXISTS mensagens_seq CASCADE;
DROP SEQUENCE IF EXISTS convites_seq CASCADE;
DROP SEQUENCE IF EXISTS lances_seq CASCADE;
DROP SEQUENCE IF EXISTS leiloes_seq CASCADE;
DROP SEQUENCE IF EXISTS usuarios_seq CASCADE;
DROP SEQUENCE IF EXISTS categorias_seq CASCADE;
DROP SEQUENCE IF EXISTS formas_pagamento_seq CASCADE;
DROP SEQUENCE IF EXISTS areas_atuacao_seq CASCADE;

-- Reabilita as restrições de chave estrangeira
SET CONSTRAINTS ALL IMMEDIATE; 