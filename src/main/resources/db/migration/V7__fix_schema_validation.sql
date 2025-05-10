-- Corrige o incremento das sequências
ALTER SEQUENCE anexos_seq INCREMENT BY 50;
ALTER SEQUENCE avaliacoes_seq INCREMENT BY 50;

-- Adiciona a coluna nome na tabela usuarios
ALTER TABLE usuarios ADD COLUMN nome varchar(255);

-- Atualiza os registros existentes usando razao_social como nome
UPDATE usuarios SET nome = razao_social WHERE nome IS NULL;

-- Torna a coluna nome NOT NULL
ALTER TABLE usuarios ALTER COLUMN nome SET NOT NULL;

-- Corrige os tipos de dados das colunas enum
ALTER TABLE convites ALTER COLUMN status TYPE varchar(255);
ALTER TABLE leiloes ALTER COLUMN status TYPE varchar(255);
ALTER TABLE leiloes ALTER COLUMN tipo_leilao TYPE varchar(255);
ALTER TABLE leiloes ALTER COLUMN tipo_requisicao TYPE varchar(255);
ALTER TABLE mensagens ALTER COLUMN tipo TYPE varchar(255);
ALTER TABLE usuarios ALTER COLUMN cep TYPE varchar(255);
ALTER TABLE usuarios ALTER COLUMN cnpj TYPE varchar(255);
ALTER TABLE usuarios ALTER COLUMN status TYPE varchar(255);
ALTER TABLE usuarios ALTER COLUMN telefone TYPE varchar(255);
ALTER TABLE usuarios ALTER COLUMN tipo_usuario TYPE varchar(255);
ALTER TABLE usuarios ALTER COLUMN uf TYPE varchar(255); 