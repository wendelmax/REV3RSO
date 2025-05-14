-- Inserir usuário administrador inicial apenas se não existir
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@rev3rso.com.br') THEN
        INSERT INTO usuarios (
            id,
            nome,
            razao_social,
            nome_fantasia,
            cnpj,
            endereco,
            cidade,
            uf,
            cep,
            telefone,
            email,
            senha,
            tipo_usuario,
            status,
            data_cadastro,
            data_atualizacao
        ) VALUES (
            nextval('usuarios_seq'),
            'REV3RSO Admin',
            'REV3RSO Admin',
            'REV3RSO Admin',
            '00000000000000',
            'Endereço Administrativo',
            'São Paulo',
            'SP',
            '00000000',
            '(11) 0000-0000',
            'admin@rev3rso.com.br',
            '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', -- senha: admin
            'ADMINISTRADOR',
            'ATIVO',
            CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP
        );
    END IF;
END $$; 