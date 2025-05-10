package service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import model.Leilao;
import model.Usuario;
import model.Convite;
import model.Lance;
import model.FormaPagamento;
import dto.PaginatedResponse;
import exception.BusinessException;
import util.ExceptionUtil;
import io.quarkus.panache.common.Page;

/**
 * Serviço responsável por gerenciar as operações relacionadas aos leilões.
 * Centraliza a lógica de negócio e regras para criar, atualizar, publicar e finalizar leilões.
 */
@ApplicationScoped
public class LeilaoService {
    
    private static final Logger LOGGER = Logger.getLogger(LeilaoService.class.getName());
    
    @Inject
    NotificacaoService notificacaoService;
    
    @Inject
    AutenticacaoService autenticacaoService;
    
    /**
     * Busca um leilão pelo ID.
     * 
     * @param id ID do leilão
     * @return Leilão encontrado ou null se não existir
     */
    public Leilao buscarPorId(Long id) {
        return Leilao.findById(id);
    }
    
    /**
     * Lista todos os leilões disponíveis para um fornecedor.
     * 
     * @param fornecedor Usuário fornecedor
     * @return Lista de leilões disponíveis
     */
    public List<Leilao> listarDisponiveisPara(Usuario fornecedor) {
        try {
            if (fornecedor.tipoUsuario != Usuario.TipoUsuario.FORNECEDOR) {
                return List.of();
            }
            
            return Leilao.listarLeiloesPorFornecedor(fornecedor);
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar leilões disponíveis");
            return List.of();
        }
    }
    
    /**
     * Lista leilões criados por um usuário.
     * 
     * @param criador Usuário criador
     * @return Lista de leilões
     */
    public List<Leilao> listarLeiloesCriadosPor(Usuario criador) {
        try {
            if (criador.tipoUsuario != Usuario.TipoUsuario.COMPRADOR) {
                return List.of();
            }
            
            return Leilao.find("criador = ?1 ORDER BY dataCriacao DESC", criador).list();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar leilões do criador");
            return List.of();
        }
    }
    
    /**
     * Cria um novo leilão.
     * 
     * @param criador Usuário criador do leilão
     * @param titulo Título do leilão
     * @param descricao Descrição do leilão
     * @param dataInicio Data de início
     * @param dataFim Data de término
     * @param tipoLeilao Tipo de leilão (ABERTO ou FECHADO)
     * @param tipoRequisicao Tipo de requisição
     * @param formaPagamento Forma de pagamento
     * @param quantidade Quantidade
     * @param unidadeMedida Unidade de medida
     * @param valorReferencia Valor de referência
     * @return Leilão criado
     */
    @Transactional
    public Leilao criar(Usuario criador, String titulo, String descricao,
                        Date dataInicio, Date dataFim, Leilao.TipoLeilao tipoLeilao,
                        String tipoRequisicao, FormaPagamento formaPagamento,
                        Integer quantidade, String unidadeMedida, BigDecimal valorReferencia) {
        try {
            // Validações
            if (criador.tipoUsuario != Usuario.TipoUsuario.COMPRADOR) {
                throw new BusinessException("Apenas compradores podem criar leilões");
            }
            
            validarDatasLeilao(dataInicio, dataFim);
            
            // Criar novo leilão
            Leilao leilao = new Leilao();
            leilao.criador = criador;
            leilao.titulo = titulo;
            leilao.descricao = descricao;
            leilao.dataInicio = dataInicio;
            leilao.dataFim = dataFim;
            leilao.tipoLeilao = tipoLeilao;
            leilao.tipoRequisicao = tipoRequisicao;
            leilao.formaPagamento = formaPagamento;
            leilao.quantidade = quantidade;
            leilao.unidadeMedida = unidadeMedida;
            leilao.valorReferencia = valorReferencia;
            leilao.status = Leilao.Status.RASCUNHO;
            leilao.dataCriacao = new Date();
            
            leilao.persist();
            LOGGER.info("Leilão criado com sucesso. ID: " + leilao.id);
            
            return leilao;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao criar leilão");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Atualiza um leilão existente.
     * 
     * @param leilaoId ID do leilão
     * @param titulo Título do leilão
     * @param descricao Descrição do leilão
     * @param dataInicio Data de início
     * @param dataFim Data de término
     * @param tipoLeilao Tipo de leilão (ABERTO ou FECHADO)
     * @param tipoRequisicao Tipo de requisição
     * @param formaPagamento Forma de pagamento
     * @param quantidade Quantidade
     * @param unidadeMedida Unidade de medida
     * @param valorReferencia Valor de referência
     * @param usuario Usuário que está atualizando o leilão
     * @return Leilão atualizado
     */
    @Transactional
    public Leilao atualizar(Long leilaoId, String titulo, String descricao,
                          Date dataInicio, Date dataFim, Leilao.TipoLeilao tipoLeilao,
                          String tipoRequisicao, FormaPagamento formaPagamento,
                          Integer quantidade, String unidadeMedida, BigDecimal valorReferencia,
                          Usuario usuario) {
        try {
            Leilao leilao = buscarPorId(leilaoId);
            
            if (leilao == null) {
                throw new BusinessException("Leilão não encontrado");
            }
            
            // Verificar permissão
            if (!autenticacaoService.temPermissao(usuario, "editar", leilao)) {
                throw new BusinessException("Você não tem permissão para editar este leilão");
            }
            
            // Verificar se o leilão pode ser editado
            if (leilao.status != Leilao.Status.RASCUNHO) {
                throw new BusinessException("Apenas leilões em rascunho podem ser editados");
            }
            
            // Validar datas
            validarDatasLeilao(dataInicio, dataFim);
            
            // Atualizar leilão
            leilao.titulo = titulo;
            leilao.descricao = descricao;
            leilao.dataInicio = dataInicio;
            leilao.dataFim = dataFim;
            leilao.tipoLeilao = tipoLeilao;
            leilao.tipoRequisicao = tipoRequisicao;
            leilao.formaPagamento = formaPagamento;
            leilao.quantidade = quantidade;
            leilao.unidadeMedida = unidadeMedida;
            leilao.valorReferencia = valorReferencia;
            
            leilao.persist();
            LOGGER.info("Leilão atualizado com sucesso. ID: " + leilao.id);
            
            return leilao;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao atualizar leilão");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Publica um leilão, tornando-o visível para fornecedores.
     * 
     * @param leilaoId ID do leilão
     * @param usuario Usuário que está publicando o leilão
     * @return Leilão publicado
     */
    @Transactional
    public Leilao publicar(Long leilaoId, Usuario usuario) {
        try {
            Leilao leilao = buscarPorId(leilaoId);
            
            if (leilao == null) {
                throw new BusinessException("Leilão não encontrado");
            }
            
            // Verificar permissão
            if (!autenticacaoService.temPermissao(usuario, "publicar", leilao)) {
                throw new BusinessException("Você não tem permissão para publicar este leilão");
            }
            
            // Verificar se o leilão pode ser publicado
            if (leilao.status != Leilao.Status.RASCUNHO) {
                throw new BusinessException("Apenas leilões em rascunho podem ser publicados");
            }
            
            // Validar datas
            validarDatasLeilao(leilao.dataInicio, leilao.dataFim);
            
            // Verificar convites para leilões fechados
            if (leilao.tipoLeilao == Leilao.TipoLeilao.FECHADO) {
                long convidados = Convite.count("leilao", leilao);
                if (convidados == 0) {
                    throw new BusinessException("Leilões fechados precisam ter pelo menos um fornecedor convidado");
                }
            }
            
            // Atualizar status
            leilao.status = Leilao.Status.ABERTO;
            leilao.persist();
            
            // Notificar fornecedores
            notificarFornecedores(leilao);
            
            LOGGER.info("Leilão publicado com sucesso. ID: " + leilao.id);
            return leilao;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao publicar leilão");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Cancela um leilão.
     * 
     * @param leilaoId ID do leilão
     * @param motivo Motivo do cancelamento
     * @param usuario Usuário que está cancelando o leilão
     * @return Leilão cancelado
     */
    @Transactional
    public Leilao cancelar(Long leilaoId, String motivo, Usuario usuario) {
        try {
            Leilao leilao = buscarPorId(leilaoId);
            
            if (leilao == null) {
                throw new BusinessException("Leilão não encontrado");
            }
            
            // Verificar permissão
            if (!autenticacaoService.temPermissao(usuario, "cancelar", leilao)) {
                throw new BusinessException("Você não tem permissão para cancelar este leilão");
            }
            
            // Verificar se o leilão pode ser cancelado
            if (leilao.status == Leilao.Status.CANCELADO || leilao.status == Leilao.Status.CONCLUIDO) {
                throw new BusinessException("Leilões já cancelados ou concluídos não podem ser cancelados");
            }
            
            // Atualizar status
            leilao.status = Leilao.Status.CANCELADO;
            leilao.motivoCancelamento = motivo;
            leilao.dataCancelamento = new Date();
            leilao.persist();
            
            // Notificar participantes
            notificarCancelamento(leilao);
            
            LOGGER.info("Leilão cancelado com sucesso. ID: " + leilao.id);
            return leilao;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao cancelar leilão");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Conclui um leilão manualmente.
     * 
     * @param leilaoId ID do leilão
     * @param usuario Usuário que está concluindo o leilão
     * @return Leilão concluído
     */
    @Transactional
    public Leilao concluir(Long leilaoId, Usuario usuario) {
        try {
            Leilao leilao = buscarPorId(leilaoId);
            
            if (leilao == null) {
                throw new BusinessException("Leilão não encontrado");
            }
            
            // Verificar permissão
            if (!autenticacaoService.temPermissao(usuario, "concluir", leilao)) {
                throw new BusinessException("Você não tem permissão para concluir este leilão");
            }
            
            // Verificar se o leilão pode ser concluído
            if (leilao.status != Leilao.Status.ABERTO) {
                throw new BusinessException("Apenas leilões abertos podem ser concluídos manualmente");
            }
            
            // Definir lance vencedor, se houver
            List<Lance> lances = Lance.list("leilao", leilao);
            if (!lances.isEmpty()) {
                // Ordenar por valor crescente (menor valor primeiro)
                Lance menorLance = lances.stream()
                    .min((l1, l2) -> l1.valor.compareTo(l2.valor))
                    .orElse(null);
                
                if (menorLance != null) {
                    leilao.lanceVencedor = menorLance;
                }
            }
            
            // Atualizar status
            leilao.status = Leilao.Status.CONCLUIDO;
            leilao.dataAtualizacao = new Date();
            leilao.persist();
            
            // Notificar participantes
            notificarConclusao(leilao);
            
            LOGGER.info("Leilão concluído com sucesso. ID: " + leilao.id);
            return leilao;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao concluir leilão");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Verifica se um leilão está aberto para lances.
     * 
     * @param leilao Leilão a ser verificado
     * @return true se o leilão está aberto para lances, false caso contrário
     */
    public boolean isAbertoParaLances(Leilao leilao) {
        if (leilao == null) {
            return false;
        }
        
        if (leilao.status != Leilao.Status.ABERTO) {
            return false;
        }
        
        Date agora = new Date();
        return !agora.before(leilao.dataInicio) && !agora.after(leilao.dataFim);
    }
    
    /**
     * Verifica se um fornecedor pode participar de um leilão.
     * 
     * @param fornecedor Usuário fornecedor
     * @param leilao Leilão
     * @return true se o fornecedor pode participar, false caso contrário
     */
    public boolean podeParticipar(Usuario fornecedor, Leilao leilao) {
        if (fornecedor == null || leilao == null) {
            return false;
        }
        
        if (fornecedor.tipoUsuario != Usuario.TipoUsuario.FORNECEDOR) {
            return false;
        }
        
        if (!isAbertoParaLances(leilao)) {
            return false;
        }
        
        // Verificar tipo de leilão
        if (leilao.tipoLeilao == Leilao.TipoLeilao.FECHADO) {
            return leilao.isConvidado(fornecedor);
        }
        
        return true; // Leilão aberto
    }
    
    /**
     * Verifica se um comprador pode convidar fornecedores para um leilão.
     * 
     * @param comprador Usuário comprador
     * @param leilao Leilão
     * @return true se o comprador pode convidar, false caso contrário
     */
    public boolean podeConvidar(Usuario comprador, Leilao leilao) {
        if (comprador == null || leilao == null) {
            return false;
        }
        
        // Verificar se é o criador do leilão
        if (!comprador.equals(leilao.criador)) {
            return false;
        }
        
        // Verificar tipo de leilão
        if (leilao.tipoLeilao != Leilao.TipoLeilao.FECHADO) {
            return false;
        }
        
        // Verificar status do leilão
        return leilao.status == Leilao.Status.RASCUNHO || leilao.status == Leilao.Status.ABERTO;
    }
    
    /**
     * Verifica se um usuário pode visualizar os detalhes de um leilão.
     * 
     * @param usuario Usuário
     * @param leilao Leilão
     * @return true se o usuário pode visualizar, false caso contrário
     */
    public boolean podeVisualizar(Usuario usuario, Leilao leilao) {
        if (usuario == null || leilao == null) {
            return false;
        }
        
        // Administradores podem visualizar qualquer leilão
        if (usuario.tipoUsuario == Usuario.TipoUsuario.ADMINISTRADOR) {
            return true;
        }
        
        // Criador do leilão sempre pode visualizar
        if (usuario.equals(leilao.criador)) {
            return true;
        }
        
        // Fornecedores podem visualizar leilões abertos ou que foram convidados
        if (usuario.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR) {
            if (leilao.status == Leilao.Status.ABERTO || leilao.status == Leilao.Status.CONCLUIDO) {
                return leilao.tipoLeilao == Leilao.TipoLeilao.ABERTO || leilao.isConvidado(usuario);
            }
        }
        
        return false;
    }
    
    /**
     * Valida as datas de início e fim de um leilão.
     * 
     * @param dataInicio Data de início
     * @param dataFim Data de fim
     * @throws BusinessException Se as datas são inválidas
     */
    private void validarDatasLeilao(Date dataInicio, Date dataFim) {
        Date agora = new Date();
        
        if (dataInicio == null || dataFim == null) {
            throw new BusinessException("As datas de início e término são obrigatórias");
        }
        
        if (dataInicio.before(agora)) {
            throw new BusinessException("A data de início deve ser futura");
        }
        
        if (dataFim.before(dataInicio)) {
            throw new BusinessException("A data de término deve ser posterior à data de início");
        }
    }
    
    /**
     * Notifica fornecedores sobre um novo leilão.
     * 
     * @param leilao Leilão recém-publicado
     */
    private void notificarFornecedores(Leilao leilao) {
        if (leilao.tipoLeilao == Leilao.TipoLeilao.FECHADO) {
            // Notificar apenas fornecedores convidados
            List<Convite> convites = Convite.list("leilao", leilao);
            for (Convite convite : convites) {
                notificacaoService.criarNotificacao(
                    convite.fornecedor,
                    "Novo leilão disponível",
                    "Você foi convidado para participar do leilão: " + leilao.titulo,
                    "/leiloes/" + leilao.id
                );
            }
        } else {
            // Notificar todos os fornecedores (poderia ser filtrado por área)
            List<Usuario> fornecedores = Usuario.list("tipoUsuario", Usuario.TipoUsuario.FORNECEDOR);
            for (Usuario fornecedor : fornecedores) {
                notificacaoService.criarNotificacao(
                    fornecedor,
                    "Novo leilão disponível",
                    "Um novo leilão foi publicado: " + leilao.titulo,
                    "/leiloes/" + leilao.id
                );
            }
        }
    }
    
    /**
     * Notifica participantes sobre o cancelamento de um leilão.
     * 
     * @param leilao Leilão cancelado
     */
    private void notificarCancelamento(Leilao leilao) {
        List<Usuario> participantes = leilao.getParticipantes();
        
        for (Usuario participante : participantes) {
            notificacaoService.criarNotificacao(
                participante,
                "Leilão cancelado",
                "O leilão \"" + leilao.titulo + "\" foi cancelado. Motivo: " + leilao.motivoCancelamento,
                "/leiloes/" + leilao.id
            );
        }
    }
    
    /**
     * Notifica participantes sobre a conclusão de um leilão.
     * 
     * @param leilao Leilão concluído
     */
    private void notificarConclusao(Leilao leilao) {
        List<Usuario> participantes = leilao.getParticipantes();
        
        for (Usuario participante : participantes) {
            String mensagem;
            
            if (leilao.lanceVencedor != null && participante.equals(leilao.lanceVencedor.fornecedor)) {
                mensagem = "Parabéns! Seu lance venceu o leilão \"" + leilao.titulo + "\"!";
            } else {
                mensagem = "O leilão \"" + leilao.titulo + "\" foi concluído";
                
                if (leilao.lanceVencedor != null) {
                    mensagem += ". Vencedor: " + leilao.lanceVencedor.fornecedor.nomeFantasia;
                } else {
                    mensagem += " sem lances";
                }
            }
            
            notificacaoService.criarNotificacao(
                participante,
                "Leilão concluído",
                mensagem,
                "/leiloes/" + leilao.id
            );
        }
    }
    
    /**
     * Lista leilões com paginação
     * 
     * @param page Número da página (começa em 1)
     * @param size Tamanho da página
     * @return Resposta paginada com leilões
     */
    public PaginatedResponse<Leilao> listarLeiloesPaginados(int page, int size) {
        try {
            // Ajusta o índice da página (Panache usa 0-based)
            int pageIndex = page - 1;
            
            // Busca os leilões paginados
            var pageResult = Leilao.findAll()
                .page(io.quarkus.panache.common.Page.of(pageIndex, size));
            
            // Calcula o total de páginas
            int totalPages = (int) Math.ceil((double) pageResult.count() / size);
            
            return new PaginatedResponse<>(
                pageResult.list(),
                page,
                totalPages,
                pageResult.count(),
                size
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao listar leilões paginados: " + e.getMessage());
            throw new BusinessException("Erro ao listar leilões");
        }
    }
    
    /**
     * Lista leilões por criador com paginação
     * 
     * @param criador Usuário criador
     * @param page Número da página (começa em 1)
     * @param size Tamanho da página
     * @return Resposta paginada com leilões
     */
    public PaginatedResponse<Leilao> listarLeiloesPorCriadorPaginados(Usuario criador, int page, int size) {
        try {
            int pageIndex = page - 1;
            
            var pageResult = Leilao.find("criador = ?1 ORDER BY dataCriacao DESC", criador)
                .page(io.quarkus.panache.common.Page.of(pageIndex, size));
            
            int totalPages = (int) Math.ceil((double) pageResult.count() / size);
            
            return new PaginatedResponse<>(
                pageResult.list(),
                page,
                totalPages,
                pageResult.count(),
                size
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao listar leilões do criador: " + e.getMessage());
            throw new BusinessException("Erro ao listar leilões");
        }
    }
}
