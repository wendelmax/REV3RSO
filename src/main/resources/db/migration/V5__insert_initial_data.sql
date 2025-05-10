-- Script de inserção de dados iniciais para o REV3RSO
-- Este script é executado pelo Flyway após a criação de todas as tabelas

-- Inserção de áreas de atuação
INSERT INTO areas_atuacao (id, nome, descricao) VALUES (1, 'Informática', 'Produtos e serviços de informática');
INSERT INTO areas_atuacao (id, nome, descricao) VALUES (2, 'Material de Escritório', 'Materiais diversos para escritório');
INSERT INTO areas_atuacao (id, nome, descricao) VALUES (3, 'Serviços Gerais', 'Serviços gerais e manutenção');
INSERT INTO areas_atuacao (id, nome, descricao) VALUES (4, 'Construção Civil', 'Materiais e serviços de construção');
INSERT INTO areas_atuacao (id, nome, descricao) VALUES (5, 'Mobiliário', 'Móveis e decoração');

-- Inserção de formas de pagamento
INSERT INTO formas_pagamento (id, descricao) VALUES (1, 'À vista');
INSERT INTO formas_pagamento (id, descricao) VALUES (2, '30 dias');
INSERT INTO formas_pagamento (id, descricao) VALUES (3, '30/60 dias');
INSERT INTO formas_pagamento (id, descricao) VALUES (4, '30/60/90 dias');

-- Inserção de usuários de teste
-- Senha: 123456 (hash gerado com BCrypt)
INSERT INTO usuarios (id, nome, email, senha, tipo_usuario, status, pontuacao, cpf, razao_social, nome_fantasia, cnpj, endereco, telefone, data_cadastro, data_atualizacao) 
VALUES (1, 'Admin', 'admin@rev3rso.com.br', '$2a$10$kBrQRZgM8OlHkxqhS2Ryw.8/N.ncm9Z8jAFa.QA/DAG.UHjIjaTBO', 'ADMIN', 'ATIVO', 5.0, '00000000000', 'REV3RSO Admin', 'REV3RSO', '00000000000000', 'Av. Principal, 1000', '11999999999', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

INSERT INTO usuarios (id, nome, email, senha, tipo_usuario, status, pontuacao, cpf, razao_social, nome_fantasia, cnpj, endereco, telefone, data_cadastro, data_atualizacao) 
VALUES (2, 'João Comprador', 'joao@empresa.com.br', '$2a$10$kBrQRZgM8OlHkxqhS2Ryw.8/N.ncm9Z8jAFa.QA/DAG.UHjIjaTBO', 'COMPRADOR', 'ATIVO', 4.5, '11122233344', 'Empresa de Compras Ltda.', 'Compras Tech', '11222333444455', 'Rua das Compras, 123', '11988887777', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

INSERT INTO usuarios (id, nome, email, senha, tipo_usuario, status, pontuacao, cpf, razao_social, nome_fantasia, cnpj, endereco, telefone, data_cadastro, data_atualizacao) 
VALUES (3, 'Maria Compradora', 'maria@empresa.com.br', '$2a$10$kBrQRZgM8OlHkxqhS2Ryw.8/N.ncm9Z8jAFa.QA/DAG.UHjIjaTBO', 'COMPRADOR', 'ATIVO', 4.8, '22233344455', 'Empresa de Aquisições S/A', 'Aquisições Tech', '22333444555566', 'Av. das Compras, 456', '11977776666', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

INSERT INTO usuarios (id, nome, email, senha, tipo_usuario, status, pontuacao, cpf, razao_social, nome_fantasia, cnpj, endereco, telefone, data_cadastro, data_atualizacao) 
VALUES (4, 'Carlos Fornecedor', 'carlos@fornecedor.com.br', '$2a$10$kBrQRZgM8OlHkxqhS2Ryw.8/N.ncm9Z8jAFa.QA/DAG.UHjIjaTBO', 'FORNECEDOR', 'ATIVO', 4.2, '33344455566', 'Fornecedores Unidos Ltda.', 'F Unidos', '33444555666677', 'Rua dos Fornecedores, 789', '11966665555', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

INSERT INTO usuarios (id, nome, email, senha, tipo_usuario, status, pontuacao, cpf, razao_social, nome_fantasia, cnpj, endereco, telefone, data_cadastro, data_atualizacao) 
VALUES (5, 'Ana Fornecedora', 'ana@fornecedor.com.br', '$2a$10$kBrQRZgM8OlHkxqhS2Ryw.8/N.ncm9Z8jAFa.QA/DAG.UHjIjaTBO', 'FORNECEDOR', 'ATIVO', 4.9, '44455566677', 'Ana Suprimentos ME', 'Ana Suprimentos', '44555666777788', 'Av. dos Fornecedores, 101', '11955554444', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

INSERT INTO usuarios (id, nome, email, senha, tipo_usuario, status, pontuacao, cpf, razao_social, nome_fantasia, cnpj, endereco, telefone, data_cadastro, data_atualizacao) 
VALUES (6, 'Paulo Fornecedor', 'paulo@fornecedor.com.br', '$2a$10$kBrQRZgM8OlHkxqhS2Ryw.8/N.ncm9Z8jAFa.QA/DAG.UHjIjaTBO', 'FORNECEDOR', 'ATIVO', 3.7, '55566677788', 'Paulo Produtos Ltda.', 'Paulo Produtos', '55666777888899', 'Rua das Entregas, 202', '11944443333', '2025-04-30 00:00:00', '2025-04-30 00:00:00');

-- Inserção de leilões de exemplo
-- Leilão Aberto em andamento
INSERT INTO leiloes (id, titulo, descricao, valor_inicial, data_inicio, data_fim, quantidade, unidade_medida, tipo_leilao, status, criador_id, forma_pagamento_id, tipo_requisicao, data_criacao, data_atualizacao, total_lances, total_fornecedores) 
VALUES (1, 'Aquisição de Notebooks', 'Compra de notebooks para equipe de desenvolvimento', 25000.00, '2025-04-30 00:00:00', '2025-05-15 23:59:59', 20, 'unidades', 'ABERTO', 'ABERTO', 2, 2, 'COMPRA', '2025-04-30 00:00:00', '2025-04-30 00:00:00', 3, 2);

-- Leilão Fechado em andamento
INSERT INTO leiloes (id, titulo, descricao, valor_inicial, data_inicio, data_fim, quantidade, unidade_medida, tipo_leilao, status, criador_id, forma_pagamento_id, tipo_requisicao, data_criacao, data_atualizacao, total_lances, total_fornecedores) 
VALUES (2, 'Desenvolvimento de Website', 'Contratação para desenvolvimento de website corporativo', 50000.00, '2025-04-30 00:00:00', '2025-05-10 23:59:59', 1, 'serviço', 'FECHADO', 'ABERTO', 3, 4, 'SERVICO', '2025-04-30 00:00:00', '2025-04-30 00:00:00', 0, 0);

-- Leilão Aberto concluído
INSERT INTO leiloes (id, titulo, descricao, valor_inicial, data_inicio, data_fim, quantidade, unidade_medida, tipo_leilao, status, criador_id, forma_pagamento_id, tipo_requisicao, data_criacao, data_atualizacao, total_lances, total_fornecedores) 
VALUES (3, 'Fornecimento de Material de Escritório', 'Aquisição de materiais diversos para escritório', 15000.00, '2025-03-01 00:00:00', '2025-03-15 23:59:59', 1, 'lote', 'ABERTO', 'CONCLUIDO', 2, 1, 'COMPRA', '2025-03-01 00:00:00', '2025-03-16 10:00:00', 3, 3);

-- Inserção de convites para o leilão fechado
INSERT INTO convites (id, leilao_id, fornecedor_id, data_criacao, data_envio, status) 
VALUES (1, 2, 4, '2025-04-30 00:00:00', '2025-04-30 08:15:00', 'ACEITO');

INSERT INTO convites (id, leilao_id, fornecedor_id, data_criacao, data_envio, status, data_resposta) 
VALUES (2, 2, 5, '2025-04-30 00:00:00', '2025-04-30 08:15:00', 'ACEITO', '2025-04-30 10:20:00');

INSERT INTO convites (id, leilao_id, fornecedor_id, data_criacao, data_envio, status) 
VALUES (3, 2, 6, '2025-04-30 00:00:00', '2025-04-30 08:15:00', 'PENDENTE');

-- Inserção de lances para o leilão aberto
INSERT INTO lances (id, leilao_id, fornecedor_id, valor, condicoes_entrega, prazo_entrega, prazo_pagamento, data_criacao, status_lance, posicao_ranking)
VALUES (1, 1, 4, 25000.00, 'Entrega no local', 15, 30, '2025-04-30 10:15:00', 'ATIVO', 3);

INSERT INTO lances (id, leilao_id, fornecedor_id, valor, condicoes_entrega, prazo_entrega, prazo_pagamento, data_criacao, status_lance, posicao_ranking)
VALUES (2, 1, 5, 24500.00, 'Entrega no local com instalação', 10, 30, '2025-04-30 11:30:00', 'ATIVO', 2);

INSERT INTO lances (id, leilao_id, fornecedor_id, valor, condicoes_entrega, prazo_entrega, prazo_pagamento, data_criacao, status_lance, posicao_ranking)
VALUES (3, 1, 5, 24000.00, 'Entrega no local com instalação', 10, 30, '2025-04-30 14:45:00', 'ATIVO', 1);

-- Inserção de lances para o leilão concluído
INSERT INTO lances (id, leilao_id, fornecedor_id, valor, condicoes_entrega, prazo_entrega, prazo_pagamento, data_criacao, status_lance, posicao_ranking)
VALUES (4, 3, 4, 15000.00, 'Entrega no local', 5, 30, '2025-03-05 09:20:00', 'CANCELADO', 3);

INSERT INTO lances (id, leilao_id, fornecedor_id, valor, condicoes_entrega, prazo_entrega, prazo_pagamento, data_criacao, status_lance, posicao_ranking)
VALUES (5, 3, 5, 14500.00, 'Entrega no local', 7, 45, '2025-03-07 13:10:00', 'VENCEDOR', 1);

INSERT INTO lances (id, leilao_id, fornecedor_id, valor, condicoes_entrega, prazo_entrega, prazo_pagamento, data_criacao, status_lance, posicao_ranking)
VALUES (6, 3, 6, 14000.00, 'Entrega no local', 10, 60, '2025-03-08 16:40:00', 'CANCELADO', 2);

-- Inserção de histórico de lances
INSERT INTO historico_lances (id, lance_id, valor_anterior, valor_novo, posicao_anterior, posicao_nova, data_alteracao, tipo_alteracao)
VALUES (1, 1, NULL, 25000.00, NULL, 1, '2025-04-30 10:15:00', 'NOVO_LANCE');

INSERT INTO historico_lances (id, lance_id, valor_anterior, valor_novo, posicao_anterior, posicao_nova, data_alteracao, tipo_alteracao)
VALUES (2, 2, NULL, 24500.00, NULL, 1, '2025-04-30 11:30:00', 'NOVO_LANCE');

INSERT INTO historico_lances (id, lance_id, valor_anterior, valor_novo, posicao_anterior, posicao_nova, data_alteracao, tipo_alteracao)
VALUES (3, 1, 25000.00, 25000.00, 1, 2, '2025-04-30 11:30:00', 'ATUALIZACAO_RANKING');

INSERT INTO historico_lances (id, lance_id, valor_anterior, valor_novo, posicao_anterior, posicao_nova, data_alteracao, tipo_alteracao)
VALUES (4, 3, NULL, 24000.00, NULL, 1, '2025-04-30 14:45:00', 'NOVO_LANCE');

INSERT INTO historico_lances (id, lance_id, valor_anterior, valor_novo, posicao_anterior, posicao_nova, data_alteracao, tipo_alteracao)
VALUES (5, 1, 25000.00, 25000.00, 2, 3, '2025-04-30 14:45:00', 'ATUALIZACAO_RANKING');

INSERT INTO historico_lances (id, lance_id, valor_anterior, valor_novo, posicao_anterior, posicao_nova, data_alteracao, tipo_alteracao)
VALUES (6, 2, 24500.00, 24500.00, 1, 2, '2025-04-30 14:45:00', 'ATUALIZACAO_RANKING');

-- Inserção de penalidades
-- Penalidade de 1 mês para o fornecedor 4 (primeira retirada)
INSERT INTO penalidades (id, usuario_id, leilao_id, tipo_penalidade, data_ocorrencia, data_fim_penalidade, motivo, nivel_penalidade)
VALUES (1, 4, 3, 'RETIRADA_LEILAO', '2025-03-08 16:40:00', '2025-04-08 16:40:00', 'Retirada do leilão após envio de lance', 1);

-- Penalidade de 6 meses para o fornecedor 6 (segunda retirada)
INSERT INTO penalidades (id, usuario_id, leilao_id, tipo_penalidade, data_ocorrencia, data_fim_penalidade, motivo, nivel_penalidade)
VALUES (2, 6, 3, 'RETIRADA_LEILAO', '2025-03-08 16:40:00', '2025-09-08 16:40:00', 'Segunda retirada de leilão', 2);

-- Inserção de mensagens (perguntas e respostas)
-- Pergunta para o leilão 1
INSERT INTO mensagens (id, leilao_id, autor_id, conteudo, tipo, data_criacao, respondida)
VALUES (1, 1, 4, 'Qual é a especificação mínima dos notebooks?', 'PERGUNTA_FORNECEDOR', '2025-04-30 09:30:00', true);

-- Resposta para a pergunta 1
INSERT INTO mensagens (id, leilao_id, autor_id, conteudo, tipo, data_criacao, mensagem_pai_id)
VALUES (2, 1, 2, 'Processador i7 de 10ª geração, 16GB RAM, 512GB SSD, tela 15"', 'RESPOSTA_COMPRADOR', '2025-04-30 10:00:00', 1);

-- Pergunta para o leilão 3
INSERT INTO mensagens (id, leilao_id, autor_id, conteudo, tipo, data_criacao, respondida)
VALUES (3, 3, 5, 'A entrega pode ser feita em lotes?', 'PERGUNTA_FORNECEDOR', '2025-03-03 11:20:00', true);

-- Resposta para a pergunta 3
INSERT INTO mensagens (id, leilao_id, autor_id, conteudo, tipo, data_criacao, mensagem_pai_id)
VALUES (4, 3, 2, 'Sim, a entrega pode ser feita em até 2 lotes, desde que o prazo final seja respeitado.', 'RESPOSTA_COMPRADOR', '2025-03-03 14:15:00', 3);

-- Inserção de avaliações
-- Avaliação do comprador sobre o fornecedor no leilão 3
INSERT INTO avaliacoes (id, leilao_id, avaliador_id, avaliado_id, nota, comentario, data_avaliacao)
VALUES (1, 3, 2, 6, 5, 'Excelente fornecedor. Entregou conforme prometido e com ótima qualidade.', '2025-03-20 10:00:00');

-- Réplica do fornecedor à avaliação
UPDATE avaliacoes SET replica = 'Obrigado pela avaliação! Foi um prazer trabalhar com sua empresa.' WHERE id = 1;

-- Avaliação do fornecedor sobre o comprador no leilão 3
INSERT INTO avaliacoes (id, leilao_id, avaliador_id, avaliado_id, nota, comentario, data_avaliacao)
VALUES (2, 3, 6, 2, 5, 'Ótimo comprador, comunicação clara e pagamento no prazo combinado.', '2025-03-21 09:15:00');

-- Associação de fornecedores com áreas de atuação
INSERT INTO usuario_areas (usuario_id, area_id) VALUES (4, 1);
INSERT INTO usuario_areas (usuario_id, area_id) VALUES (4, 3);
INSERT INTO usuario_areas (usuario_id, area_id) VALUES (5, 1);
INSERT INTO usuario_areas (usuario_id, area_id) VALUES (5, 2);
INSERT INTO usuario_areas (usuario_id, area_id) VALUES (6, 2);
INSERT INTO usuario_areas (usuario_id, area_id) VALUES (6, 5);

-- Configuração do sequence para auto-incremento
SELECT setval('usuarios_seq', 6, true);
SELECT setval('leiloes_seq', 3, true);
SELECT setval('lances_seq', 6, true);
SELECT setval('convites_seq', 3, true);
SELECT setval('mensagens_seq', 4, true);
SELECT setval('avaliacoes_seq', 2, true);
SELECT setval('areas_atuacao_seq', 5, true);
SELECT setval('formas_pagamento_seq', 4, true);
SELECT setval('penalidades_seq', 2, true);
SELECT setval('historico_lances_seq', 6, true); 