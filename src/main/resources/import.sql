-- Script de inicialização do banco de dados para o REV3RSO
-- Este script será executado automaticamente pelo Quarkus ao iniciar a aplicação
-- quando quarkus.hibernate-orm.sql-load-script=import.sql estiver configurado

-- Limpeza prévia das tabelas (em ordem inversa de dependência)
DELETE FROM avaliacoes;
DELETE FROM convites;
DELETE FROM mensagens;
DELETE FROM lances;
DELETE FROM leiloes;
DELETE FROM usuario_areas;
DELETE FROM usuarios;
DELETE FROM areas_atuacao;
DELETE FROM formas_pagamento;

-- Inserção de áreas de atuação
INSERT INTO areas_atuacao (id, descricao)
VALUES (1, 'Informática');

INSERT INTO areas_atuacao (id, descricao)
VALUES (2, 'Material de Escritório');

INSERT INTO areas_atuacao (id, descricao)
VALUES (3, 'Serviços Gerais');

INSERT INTO areas_atuacao (id, descricao)
VALUES (4, 'Construção Civil');

INSERT INTO areas_atuacao (id, descricao)
VALUES (5, 'Mobiliário');

-- Inserção de formas de pagamento
INSERT INTO formas_pagamento (id, descricao)
VALUES (1, 'À vista');

INSERT INTO formas_pagamento (id, descricao)
VALUES (2, '30 dias');

INSERT INTO formas_pagamento (id, descricao)
VALUES (3, '30/60 dias');

INSERT INTO formas_pagamento (id, descricao)
VALUES (4, '30/60/90 dias');

-- Inserção de usuários de teste
-- Senha: 123456 (hash gerado com BCrypt)
INSERT INTO usuarios (id, nome, email, senha, tipo_usuario, status, pontuacao, cpf, razao_social, nome_fantasia, cnpj, endereco, telefone, data_cadastro, data_atualizacao) 
VALUES 
(1, 'Admin', 'admin@rev3rso.com.br', '$2a$10$kBrQRZgM8OlHkxqhS2Ryw.8/N.ncm9Z8jAFa.QA/DAG.UHjIjaTBO', 'ADMIN', 'ATIVO', 5.0, '00000000000', 'REV3RSO Admin', 'REV3RSO', '00000000000000', 'Av. Principal, 1000', '11999999999', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

INSERT INTO usuarios (id, nome, email, senha, tipo_usuario, status, pontuacao, cpf, razao_social, nome_fantasia, cnpj, endereco, telefone, data_cadastro, data_atualizacao) 
VALUES 
(2, 'João Comprador', 'joao@empresa.com.br', '$2a$10$kBrQRZgM8OlHkxqhS2Ryw.8/N.ncm9Z8jAFa.QA/DAG.UHjIjaTBO', 'COMPRADOR', 'ATIVO', 4.5, '11122233344', 'Empresa de Compras Ltda.', 'Compras Tech', '11222333444455', 'Rua das Compras, 123', '11988887777', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

INSERT INTO usuarios (id, nome, email, senha, tipo_usuario, status, pontuacao, cpf, razao_social, nome_fantasia, cnpj, endereco, telefone, data_cadastro, data_atualizacao) 
VALUES 
(3, 'Maria Compradora', 'maria@empresa.com.br', '$2a$10$kBrQRZgM8OlHkxqhS2Ryw.8/N.ncm9Z8jAFa.QA/DAG.UHjIjaTBO', 'COMPRADOR', 'ATIVO', 4.8, '22233344455', 'Empresa de Aquisições S/A', 'Aquisições Tech', '22333444555566', 'Av. das Compras, 456', '11977776666', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

INSERT INTO usuarios (id, nome, email, senha, tipo_usuario, status, pontuacao, cpf, razao_social, nome_fantasia, cnpj, endereco, telefone, data_cadastro, data_atualizacao) 
VALUES 
(4, 'Carlos Fornecedor', 'carlos@fornecedor.com.br', '$2a$10$kBrQRZgM8OlHkxqhS2Ryw.8/N.ncm9Z8jAFa.QA/DAG.UHjIjaTBO', 'FORNECEDOR', 'ATIVO', 4.2, '33344455566', 'Fornecedores Unidos Ltda.', 'F Unidos', '33444555666677', 'Rua dos Fornecedores, 789', '11966665555', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

INSERT INTO usuarios (id, nome, email, senha, tipo_usuario, status, pontuacao, cpf, razao_social, nome_fantasia, cnpj, endereco, telefone, data_cadastro, data_atualizacao) 
VALUES 
(5, 'Ana Fornecedora', 'ana@fornecedor.com.br', '$2a$10$kBrQRZgM8OlHkxqhS2Ryw.8/N.ncm9Z8jAFa.QA/DAG.UHjIjaTBO', 'FORNECEDOR', 'ATIVO', 4.9, '44455566677', 'Ana Suprimentos ME', 'Ana Suprimentos', '44555666777788', 'Av. dos Fornecedores, 101', '11955554444', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

INSERT INTO usuarios (id, nome, email, senha, tipo_usuario, status, pontuacao, cpf, razao_social, nome_fantasia, cnpj, endereco, telefone, data_cadastro, data_atualizacao) 
VALUES 
(6, 'Paulo Fornecedor', 'paulo@fornecedor.com.br', '$2a$10$kBrQRZgM8OlHkxqhS2Ryw.8/N.ncm9Z8jAFa.QA/DAG.UHjIjaTBO', 'FORNECEDOR', 'ATIVO', 3.7, '55566677788', 'Paulo Produtos Ltda.', 'Paulo Produtos', '55666777888899', 'Rua das Entregas, 202', '11944443333', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

-- Inserção de leilões de exemplo
-- Leilão Aberto em andamento
INSERT INTO leiloes (id, titulo, descricao, data_inicio, data_fim, quantidade, unidade_medida, tipo_leilao, status, criador_id, forma_pagamento_id, tipo_requisicao, data_criacao, data_atualizacao) 
VALUES 
(1, 'Aquisição de Notebooks', 'Compra de notebooks para equipe de desenvolvimento', '2025-04-30 00:00:00', '2025-05-15 23:59:59', 20, 'unidades', 'ABERTO', 'ABERTO', 2, 2, 'Compra', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

-- Leilão Fechado em andamento
INSERT INTO leiloes (id, titulo, descricao, data_inicio, data_fim, quantidade, unidade_medida, tipo_leilao, status, criador_id, forma_pagamento_id, tipo_requisicao, data_criacao, data_atualizacao) 
VALUES 
(2, 'Desenvolvimento de Website', 'Contratação para desenvolvimento de website corporativo', '2025-04-30 00:00:00', '2025-05-10 23:59:59', 1, 'serviço', 'FECHADO', 'ABERTO', 3, 4, 'Serviço', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

-- Leilão Aberto concluído
INSERT INTO leiloes (id, titulo, descricao, data_inicio, data_fim, quantidade, unidade_medida, tipo_leilao, status, criador_id, forma_pagamento_id, tipo_requisicao, data_criacao, data_atualizacao) 
VALUES 
(3, 'Fornecimento de Material de Escritório', 'Aquisição de materiais diversos para escritório', '2025-03-01 00:00:00', '2025-03-15 23:59:59', 1, 'lote', 'ABERTO', 'CONCLUIDO', 2, 1, 'Compra', '2025-03-01 00:00:00', '2025-03-16 10:00:00');

-- Inserção de convites para o leilão fechado
INSERT INTO convites (id, leilao_id, fornecedor_id, data_envio, status) 
VALUES 
(1, 2, 4, '2025-04-30 08:15:00', 'ACEITO');

INSERT INTO convites (id, leilao_id, fornecedor_id, data_envio, status, data_resposta) 
VALUES 
(2, 2, 5, '2025-04-30 08:15:00', 'ACEITO', '2025-04-30 10:20:00');

INSERT INTO convites (id, leilao_id, fornecedor_id, data_envio, status) 
VALUES 
(3, 2, 6, '2025-04-30 08:15:00', 'PENDENTE');

-- Inserção de lances para o leilão aberto
INSERT INTO lances (id, leilao_id, fornecedor_id, valor, condicoes_entrega, prazo_entrega, prazo_pagamento, data_criacao)
VALUES 
(1, 1, 4, 25000.00, 'Entrega no local', 15, 30, '2025-04-30 10:15:00');

INSERT INTO lances (id, leilao_id, fornecedor_id, valor, condicoes_entrega, prazo_entrega, prazo_pagamento, data_criacao)
VALUES 
(2, 1, 5, 24500.00, 'Entrega no local com instalação', 10, 30, '2025-04-30 11:30:00');

INSERT INTO lances (id, leilao_id, fornecedor_id, valor, condicoes_entrega, prazo_entrega, prazo_pagamento, data_criacao)
VALUES 
(3, 1, 5, 24000.00, 'Entrega no local com instalação', 10, 30, '2025-04-30 14:45:00');

-- Inserção de lances para o leilão concluído
INSERT INTO lances (id, leilao_id, fornecedor_id, valor, condicoes_entrega, prazo_entrega, prazo_pagamento, data_criacao)
VALUES 
(4, 3, 4, 15000.00, 'Entrega no local', 5, 30, '2025-03-05 09:20:00');

INSERT INTO lances (id, leilao_id, fornecedor_id, valor, condicoes_entrega, prazo_entrega, prazo_pagamento, data_criacao)
VALUES 
(5, 3, 5, 14500.00, 'Entrega no local', 7, 45, '2025-03-07 13:10:00');

INSERT INTO lances (id, leilao_id, fornecedor_id, valor, condicoes_entrega, prazo_entrega, prazo_pagamento, data_criacao)
VALUES 
(6, 3, 6, 14000.00, 'Entrega no local', 10, 60, '2025-03-08 16:40:00');

-- Inserção de mensagens (perguntas e respostas)
-- Pergunta para o leilão 1
INSERT INTO mensagens (id, leilao_id, autor_id, conteudo, tipo, data_criacao, respondida)
VALUES 
(1, 1, 4, 'Qual é a especificação mínima dos notebooks?', 'PERGUNTA_FORNECEDOR', '2025-04-30 09:30:00', true);

-- Resposta para a pergunta 1
INSERT INTO mensagens (id, leilao_id, autor_id, conteudo, tipo, data_criacao, mensagem_pai_id)
VALUES 
(2, 1, 2, 'Processador i7 de 10ª geração, 16GB RAM, 512GB SSD, tela 15"', 'RESPOSTA_COMPRADOR', '2025-04-30 10:00:00', 1);

-- Pergunta para o leilão 3
INSERT INTO mensagens (id, leilao_id, autor_id, conteudo, tipo, data_criacao, respondida)
VALUES 
(3, 3, 5, 'A entrega pode ser feita em lotes?', 'PERGUNTA_FORNECEDOR', '2025-03-03 11:20:00', true);

-- Resposta para a pergunta 3
INSERT INTO mensagens (id, leilao_id, autor_id, conteudo, tipo, data_criacao, mensagem_pai_id)
VALUES 
(4, 3, 2, 'Sim, a entrega pode ser feita em até 2 lotes, desde que o prazo final seja respeitado.', 'RESPOSTA_COMPRADOR', '2025-03-03 14:15:00', 3);

-- Inserção de avaliações
-- Avaliação do comprador sobre o fornecedor no leilão 3
INSERT INTO avaliacoes (id, leilao_id, avaliador_id, avaliado_id, nota, comentario, data_criacao)
VALUES 
(1, 3, 2, 6, 5, 'Excelente fornecedor. Entregou conforme prometido e com ótima qualidade.', '2025-03-20 10:00:00');

-- Réplica do fornecedor à avaliação
UPDATE avaliacoes SET replica = 'Obrigado pela avaliação! Foi um prazer trabalhar com sua empresa.' WHERE id = 1;

-- Avaliação do fornecedor sobre o comprador no leilão 3
INSERT INTO avaliacoes (id, leilao_id, avaliador_id, avaliado_id, nota, comentario, data_criacao)
VALUES 
(2, 3, 6, 2, 5, 'Ótimo comprador, comunicação clara e pagamento no prazo combinado.', '2025-03-21 09:15:00');

-- Associação de fornecedores com áreas de atuação
INSERT INTO usuario_areas (usuario_id, area_id)
VALUES (4, 1);

INSERT INTO usuario_areas (usuario_id, area_id)
VALUES (4, 3);

INSERT INTO usuario_areas (usuario_id, area_id)
VALUES (5, 1);

INSERT INTO usuario_areas (usuario_id, area_id)
VALUES (5, 2);

INSERT INTO usuario_areas (usuario_id, area_id)
VALUES (6, 2);

INSERT INTO usuario_areas (usuario_id, area_id)
VALUES (6, 5);

-- Configuração do sequence para auto-incremento
SELECT setval('usuarios_seq', 6, true);
SELECT setval('leiloes_seq', 3, true);
SELECT setval('lances_seq', 6, true);
SELECT setval('convites_seq', 3, true);
SELECT setval('mensagens_seq', 4, true);
SELECT setval('avaliacoes_seq', 2, true);
SELECT setval('areas_atuacao_seq', 5, true);
SELECT setval('formas_pagamento_seq', 4, true);
