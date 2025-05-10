-- Inserir usuário administrador inicial
INSERT INTO usuario (
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
    ultima_atualizacao
) VALUES (
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