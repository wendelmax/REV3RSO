package service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import model.Lance;
import model.Leilao;
import model.Usuario;
import exception.BusinessException;
import util.ExceptionUtil;

/**
 * Serviço responsável por gerenciar as operações relacionadas aos lances.
 * Centraliza a lógica de negócio e regras para criar e gerenciar lances em leilões.
 */
@ApplicationScoped
public class LanceService {
    
    private static final Logger LOGGER = Logger.getLogger(LanceService.class.getName());
    
    @Inject
    NotificacaoService notificacaoService;
    
    @Inject
    LeilaoService leilaoService;
    
    /**
     * Busca um lance pelo ID.
     * 
     * @param id ID do lance
     * @return Lance encontrado ou null se não existir
     */
    public Lance buscarPorId(Long id) {
        return Lance.findById(id);
    }
    
    /**
     * Lista todos os lances de um leilão.
     * 
     * @param leilao Leilão
     * @return Lista de lances
     */
    public List<Lance> listarLancesPorLeilao(Leilao leilao) {
        try {
            return Lance.find("leilao = ?1 ORDER BY dataCriacao DESC", leilao).list();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar lances do leilão " + leilao.id);
            return List.of(); // Retorna lista vazia em caso de erro
        }
    }
    
    /**
     * Lista todos os lances de um fornecedor.
     * 
     * @param fornecedor Usuário fornecedor
     * @return Lista de lances
     */
    public List<Lance> listarLancesPorFornecedor(Usuario fornecedor) {
        try {
            return Lance.find("fornecedor = ?1 ORDER BY dataCriacao DESC", fornecedor).list();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar lances do fornecedor " + fornecedor.id);
            return List.of(); // Retorna lista vazia em caso de erro
        }
    }
    
    /**
     * Busca o lance vencedor de um leilão.
     * 
     * @param leilao Leilão
     * @return Lance vencedor ou null se não houver
     */
    public Lance buscarLanceVencedor(Leilao leilao) {
        try {
            if (leilao.status != Leilao.Status.CONCLUIDO) {
                return null; // Leilão não concluído
            }
            
            return leilao.lanceVencedor;
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao buscar lance vencedor do leilão " + leilao.id);
            return null;
        }
    }
    
    /**
     * Busca o menor lance de um leilão.
     * 
     * @param leilao Leilão
     * @return Menor lance ou null se não houver lances
     */
    public Lance buscarMenorLance(Leilao leilao) {
        try {
            return Lance.find("leilao = ?1 ORDER BY valor ASC", leilao).firstResult();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao buscar menor lance do leilão " + leilao.id);
            return null;
        }
    }
    
    /**
     * Verifica se um fornecedor já deu lance em um leilão.
     * 
     * @param fornecedor Usuário fornecedor
     * @param leilao Leilão
     * @return true se já deu lance, false caso contrário
     */
    public boolean jaTemLance(Usuario fornecedor, Leilao leilao) {
        try {
            return Lance.count("leilao = ?1 AND fornecedor = ?2", leilao, fornecedor) > 0;
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao verificar se fornecedor já tem lance");
            return false;
        }
    }
    
    /**
     * Cria um novo lance em um leilão.
     * 
     * @param leilao Leilão
     * @param fornecedor Usuário fornecedor
     * @param valor Valor do lance
     * @param condicoesEntrega Condições de entrega
     * @param prazoEntrega Prazo de entrega
     * @param prazoPagamento Prazo de pagamento
     * @return Lance criado
     */
    @Transactional
    public Lance criarLance(Leilao leilao, Usuario fornecedor, Double valor,
                          String condicoesEntrega, Integer prazoEntrega, Integer prazoPagamento) {
        try {
            // Validações
            if (fornecedor.tipoUsuario != Usuario.TipoUsuario.FORNECEDOR) {
                throw new BusinessException("Apenas fornecedores podem dar lances");
            }
            
            if (!leilaoService.isAbertoParaLances(leilao)) {
                throw new BusinessException("Este leilão não está aberto para lances");
            }
            
            if (!leilaoService.podeParticipar(fornecedor, leilao)) {
                throw new BusinessException("Você não tem permissão para participar deste leilão");
            }
            
            if (valor <= 0) {
                throw new BusinessException("O valor do lance deve ser maior que zero");
            }
            
            // Validar valor de referência
            if (leilao.valorReferencia != null && valor > leilao.valorReferencia) {
                throw new BusinessException("O valor do lance não pode ser maior que o valor de referência");
            }
            
            // Criar lance
            Lance lance = new Lance();
            lance.leilao = leilao;
            lance.fornecedor = fornecedor;
            lance.valor = valor;
            lance.condicoesEntrega = condicoesEntrega;
            lance.prazoEntrega = prazoEntrega;
            lance.prazoPagamento = prazoPagamento;
            lance.dataCriacao = new Date();
            
            lance.persist();
            
            // Verificar se é o menor lance (melhor oferta)
            atualizarMelhorOferta(leilao);
            
            // Notificar o comprador
            notificarNovoLance(lance);
            
            LOGGER.info("Lance criado com sucesso. ID: " + lance.id + ", Leilão: " + leilao.id + ", Fornecedor: " + fornecedor.id);
            return lance;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao criar lance");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Atualiza o lance atual de um fornecedor em um leilão.
     * 
     * @param lanceId ID do lance a ser atualizado
     * @param valor Novo valor do lance
     * @param condicoesEntrega Novas condições de entrega
     * @param prazoEntrega Novo prazo de entrega
     * @param prazoPagamento Novo prazo de pagamento
     * @param fornecedor Usuário fornecedor que está atualizando o lance
     * @return Lance atualizado
     */
    @Transactional
    public Lance atualizarLance(Long lanceId, Double valor, String condicoesEntrega,
                              Integer prazoEntrega, Integer prazoPagamento, Usuario fornecedor) {
        try {
            Lance lance = buscarPorId(lanceId);
            
            if (lance == null) {
                throw new BusinessException("Lance não encontrado");
            }
            
            // Verificar permissão
            if (!fornecedor.equals(lance.fornecedor)) {
                throw new BusinessException("Você não tem permissão para atualizar este lance");
            }
            
            // Verificar se o leilão está aberto
            if (!leilaoService.isAbertoParaLances(lance.leilao)) {
                throw new BusinessException("O leilão não está mais aberto para lances");
            }
            
            if (valor <= 0) {
                throw new BusinessException("O valor do lance deve ser maior que zero");
            }
            
            // Validar valor de referência
            if (lance.leilao.valorReferencia != null && valor > lance.leilao.valorReferencia) {
                throw new BusinessException("O valor do lance não pode ser maior que o valor de referência");
            }
            
            // Atualizar lance
            lance.valor = valor;
            lance.condicoesEntrega = condicoesEntrega;
            lance.prazoEntrega = prazoEntrega;
            lance.prazoPagamento = prazoPagamento;
            lance.dataAtualizacao = new Date();
            
            lance.persist();
            
            // Verificar se é o menor lance (melhor oferta)
            atualizarMelhorOferta(lance.leilao);
            
            // Notificar o comprador
            notificarAtualizacaoLance(lance);
            
            LOGGER.info("Lance atualizado com sucesso. ID: " + lance.id);
            return lance;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao atualizar lance");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Cancela um lance.
     * 
     * @param lanceId ID do lance
     * @param motivo Motivo do cancelamento
     * @param usuario Usuário que está cancelando o lance
     * @return Lance cancelado
     */
    @Transactional
    public Lance cancelarLance(Long lanceId, String motivo, Usuario usuario) {
        try {
            Lance lance = buscarPorId(lanceId);
            
            if (lance == null) {
                throw new BusinessException("Lance não encontrado");
            }
            
            // Verificar permissão (apenas o fornecedor que deu o lance ou o criador do leilão)
            if (!usuario.equals(lance.fornecedor) && !usuario.equals(lance.leilao.criador)) {
                throw new BusinessException("Você não tem permissão para cancelar este lance");
            }
            
            // Verificar se o leilão está aberto
            if (lance.leilao.status != Leilao.Status.ABERTO) {
                throw new BusinessException("Não é possível cancelar lances em leilões não abertos");
            }
            
            // Cancelar lance
            lance.cancelado = true;
            lance.motivoCancelamento = motivo;
            lance.dataCancelamento = new Date();
            
            lance.persist();
            
            // Verificar se era o menor lance e atualizar
            atualizarMelhorOferta(lance.leilao);
            
            // Notificar cancelamento
            notificarCancelamentoLance(lance, usuario);
            
            LOGGER.info("Lance cancelado com sucesso. ID: " + lance.id);
            return lance;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao cancelar lance");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Atualiza a melhor oferta de um leilão.
     * 
     * @param leilao Leilão
     */
    private void atualizarMelhorOferta(Leilao leilao) {
        try {
            // Buscar o menor valor de lance não cancelado
            Lance menorLance = Lance.find(
                "leilao = ?1 AND cancelado = false ORDER BY valor ASC", leilao).firstResult();
            
            if (menorLance != null) {
                // Atualizar referência no leilão
                leilao.melhorOferta = menorLance.valor;
                leilao.persist();
            } else {
                // Não há lances válidos
                leilao.melhorOferta = null;
                leilao.persist();
            }
        } catch (Exception e) {
            LOGGER.severe("Erro ao atualizar melhor oferta: " + e.getMessage());
        }
    }
    
    /**
     * Notifica os participantes sobre um novo lance.
     * 
     * @param lance Novo lance
     */
    private void notificarNovoLance(Lance lance) {
        try {
            // Notificar o comprador (criador do leilão)
            notificacaoService.criarNotificacao(
                lance.leilao.criador,
                "Novo lance em seu leilão",
                "O fornecedor " + lance.fornecedor.nome + " deu um lance de R$ " + 
                String.format("%.2f", lance.valor) + " no leilão " + lance.leilao.titulo,
                "/leiloes/" + lance.leilao.id
            );
            
            // Notificar outros fornecedores com lance maior
            List<Lance> lancesSuperiores = Lance.find(
                "leilao = ?1 AND fornecedor != ?2 AND valor > ?3 AND cancelado = false",
                lance.leilao, lance.fornecedor, lance.valor).list();
            
            for (Lance lanceAnterior : lancesSuperiores) {
                notificacaoService.criarNotificacao(
                    lanceAnterior.fornecedor,
                    "Seu lance foi superado",
                    "Um novo lance de R$ " + String.format("%.2f", lance.valor) + 
                    " foi registrado no leilão " + lance.leilao.titulo,
                    "/leiloes/" + lance.leilao.id
                );
            }
        } catch (Exception e) {
            LOGGER.severe("Erro ao notificar sobre novo lance: " + e.getMessage());
        }
    }
    
    /**
     * Notifica os participantes sobre a atualização de um lance.
     * 
     * @param lance Lance atualizado
     */
    private void notificarAtualizacaoLance(Lance lance) {
        try {
            // Notificar o comprador (criador do leilão)
            notificacaoService.criarNotificacao(
                lance.leilao.criador,
                "Lance atualizado em seu leilão",
                "O fornecedor " + lance.fornecedor.nome + " atualizou seu lance para R$ " + 
                String.format("%.2f", lance.valor) + " no leilão " + lance.leilao.titulo,
                "/leiloes/" + lance.leilao.id
            );
            
            // Notificar outros fornecedores com lance maior
            List<Lance> lancesSuperiores = Lance.find(
                "leilao = ?1 AND fornecedor != ?2 AND valor > ?3 AND cancelado = false",
                lance.leilao, lance.fornecedor, lance.valor).list();
            
            for (Lance lanceAnterior : lancesSuperiores) {
                notificacaoService.criarNotificacao(
                    lanceAnterior.fornecedor,
                    "Seu lance foi superado",
                    "Um lance foi atualizado para R$ " + String.format("%.2f", lance.valor) + 
                    " no leilão " + lance.leilao.titulo,
                    "/leiloes/" + lance.leilao.id
                );
            }
        } catch (Exception e) {
            LOGGER.severe("Erro ao notificar sobre atualização de lance: " + e.getMessage());
        }
    }
    
    /**
     * Notifica os participantes sobre o cancelamento de um lance.
     * 
     * @param lance Lance cancelado
     * @param cancelador Usuário que cancelou o lance
     */
    private void notificarCancelamentoLance(Lance lance, Usuario cancelador) {
        try {
            // Se o cancelador é o próprio fornecedor
            if (cancelador.equals(lance.fornecedor)) {
                notificacaoService.criarNotificacao(
                    lance.leilao.criador,
                    "Lance cancelado em seu leilão",
                    "O fornecedor " + lance.fornecedor.nome + " cancelou seu lance no leilão " + 
                    lance.leilao.titulo + ". Motivo: " + lance.motivoCancelamento,
                    "/leiloes/" + lance.leilao.id
                );
            } 
            // Se o cancelador é o criador do leilão
            else if (cancelador.equals(lance.leilao.criador)) {
                notificacaoService.criarNotificacao(
                    lance.fornecedor,
                    "Seu lance foi cancelado",
                    "O comprador cancelou seu lance no leilão " + lance.leilao.titulo + 
                    ". Motivo: " + lance.motivoCancelamento,
                    "/leiloes/" + lance.leilao.id
                );
            }
        } catch (Exception e) {
            LOGGER.severe("Erro ao notificar sobre cancelamento de lance: " + e.getMessage());
        }
    }
}
