-- Corrige problemas de validação de dados

-- Corrige o incremento das sequências
ALTER SEQUENCE anexos_seq INCREMENT BY 50;
ALTER SEQUENCE avaliacoes_seq INCREMENT BY 50;

-- Corrige a coluna data_atualizacao
ALTER TABLE usuarios ALTER COLUMN data_atualizacao DROP NOT NULL;
UPDATE usuarios SET data_atualizacao = data_cadastro WHERE data_atualizacao IS NULL;

-- Corrige a coluna ultima_atualizacao
UPDATE usuarios SET ultima_atualizacao = data_cadastro WHERE ultima_atualizacao IS NULL;
ALTER TABLE usuarios ALTER COLUMN ultima_atualizacao SET NOT NULL;

-- Corrige os tipos de dados das colunas enum
ALTER TABLE convites ALTER COLUMN status TYPE varchar(255);
ALTER TABLE leiloes ALTER COLUMN status TYPE varchar(255);
ALTER TABLE leiloes ALTER COLUMN tipo_leilao TYPE varchar(255);
ALTER TABLE leiloes ALTER COLUMN tipo_requisicao TYPE varchar(255);
ALTER TABLE mensagens ALTER COLUMN tipo TYPE varchar(255);
ALTER TABLE usuarios ALTER COLUMN status TYPE varchar(255);
ALTER TABLE usuarios ALTER COLUMN tipo_usuario TYPE varchar(255);

-- Corrige a coluna nome
UPDATE usuarios SET nome = razao_social WHERE nome IS NULL;
ALTER TABLE usuarios ALTER COLUMN nome SET NOT NULL; 