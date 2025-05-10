-- Primeiro, adiciona a coluna permitindo valores nulos
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS tipo_usuario VARCHAR(255);

-- Atualiza registros existentes para ter um tipo de usuário padrão
UPDATE usuarios SET tipo_usuario = 'FORNECEDOR' WHERE tipo_usuario IS NULL;

-- Adiciona a restrição NOT NULL e o check constraint
ALTER TABLE usuarios ALTER COLUMN tipo_usuario SET NOT NULL;
ALTER TABLE usuarios ADD CONSTRAINT check_tipo_usuario CHECK (tipo_usuario IN ('COMPRADOR', 'FORNECEDOR', 'ADMINISTRADOR')); 