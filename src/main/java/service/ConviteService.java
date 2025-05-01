package service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import model.Convite;
import model.Leilao;
import model.Usuario;
import exception.BusinessException;
import util.ExceptionUtil;

/**
 * Serviço responsável por gerenciar as operações relacionadas aos convites para leilões fechados.
 * Centraliza a lógica de negócio e regras para criar e gerenciar convites.
 */
@ApplicationScoped
public class ConviteService {
    
    private static final Logger LOGGER = Logger.getLogger(ConviteService.class.getName());
    
    @Inject
    NotificacaoService notificacaoService;
    
    @Inject
    EmailService emailService;
    
    @Inject
    LeilaoService leilaoService;
    
    /**
     * Busca um convite pelo ID.
     * 
     * @param id ID do convite
     * @return Convite encontrado ou null se não existir
     */
    public Convite buscarPorId(Long id) {
        return Convite.findById(id);
    }
    
    /**
     * Lista todos os convites de um leilão.
     * 
     * @param leilao Leilão
     * @return Lista de convites
     */
    public List<Convite> listarConvitesPorLeilao(Leilao leilao) {
        try {
            return Convite.find("leilao = ?1 ORDER BY dataCriacao DESC", leilao).list();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar convites do leilão " + leilao.id);
            return List.of();
        }
    }
    
    /**
     * Lista todos os convites enviados a um fornecedor.
     * 
     * @param fornecedor Usuário fornecedor
     * @return Lista de convites
     */
    public List<Convite> listarConvitesPorFornecedor(Usuario fornecedor) {
        try {
            return Convite.find("fornecedor = ?1 ORDER BY dataCriacao DESC", fornecedor).list();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar convites do fornecedor " + fornecedor.id);
            return List.of();
        }
    }
    
    /**
     * Lista todos os convites aceitos de um leilão.
     * 
     * @param leilao Leilão
     * @return Lista de convites aceitos
     */
    public List<Convite> listarConvitesAceitos(Leilao leilao) {
        try {
            return Convite.find("leilao = ?1 AND status = ?2 ORDER BY dataResposta DESC", 
                leilao, Convite.Status.ACEITO).list();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar convites aceitos do leilão " + leilao.id);
            return List.of();
        }
    }
    
    /**
     * Lista todos os convites pendentes de um leilão.
     * 
     * @param leilao Leilão
     * @return Lista de convites pendentes
     */
    public List<Convite> listarConvitesPendentes(Leilao leilao) {
        try {
            return Convite.find("leilao = ?1 AND status = ?2 ORDER BY dataCriacao DESC", 
                leilao, Convite.Status.PENDENTE).list();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar convites pendentes do leilão " + leilao.id);
            return List.of();
        }
    }
    
    /**
     * Verifica se um fornecedor já foi convidado para um leilão.
     * 
     * @param fornecedor Usuário fornecedor
     * @param leilao Leilão
     * @return true se já foi convidado, false caso contrário
     */
    public boolean jaConvidado(Usuario fornecedor, Leilao leilao) {
        try {
            return Convite.count("leilao = ?1 AND fornecedor = ?2", leilao, fornecedor) > 0;
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao verificar se fornecedor já foi convidado");
            return false;
        }
    }
    
    /**
     * Cria um novo convite para um fornecedor participar de um leilão.
     * 
     * @param leilao Leilão
     * @param fornecedor Usuário fornecedor
     * @param comprador Usuário comprador que está enviando o convite
     * @param mensagem Mensagem opcional do convite
     * @return Convite criado
     */
    @Transactional
    public Convite criarConvite(Leilao leilao, Usuario fornecedor, Usuario comprador, String mensagem) {
        try {
            // Validações
            if (fornecedor.tipoUsuario != Usuario.TipoUsuario.FORNECEDOR) {
                throw new BusinessException("Apenas fornecedores podem ser convidados para leilões");
            }
            
            if (!leilaoService.podeConvidar(comprador, leilao)) {
                throw new BusinessException("Você não tem permissão para convidar fornecedores para este leilão");
            }
            
            if (leilao.tipoLeilao != Leilao.TipoLeilao.FECHADO) {
                throw new BusinessException("Apenas leilões fechados podem ter convites");
            }
            
            if (jaConvidado(fornecedor, leilao)) {
                throw new BusinessException("Este fornecedor já foi convidado para o leilão");
            }
            
            // Criar convite
            Convite convite = new Convite();
            convite.leilao = leilao;
            convite.fornecedor = fornecedor;
            convite.mensagem = mensagem;
            convite.status = Convite.Status.PENDENTE;
            convite.dataCriacao = new Date();
            
            convite.persist();
            
            // Notificar fornecedor
            notificarNovoConvite(convite);
            
            // Enviar email (assíncrono)
            enviarEmailConvite(convite);
            
            LOGGER.info("Convite criado com sucesso. ID: " + convite.id + ", Leilão: " + leilao.id + ", Fornecedor: " + fornecedor.id);
            return convite;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao criar convite");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Aceita um convite para participar de um leilão.
     * 
     * @param conviteId ID do convite
     * @param fornecedor Usuário fornecedor que está aceitando o convite
     * @return Convite atualizado
     */
    @Transactional
    public Convite aceitarConvite(Long conviteId, Usuario fornecedor) {
        try {
            Convite convite = buscarPorId(conviteId);
            
            if (convite == null) {
                throw new BusinessException("Convite não encontrado");
            }
            
            // Verificar permissão
            if (!fornecedor.equals(convite.fornecedor)) {
                throw new BusinessException("Você não tem permissão para responder este convite");
            }
            
            // Verificar status atual
            if (convite.status != Convite.Status.PENDENTE) {
                throw new BusinessException("Este convite já foi respondido");
            }
            
            // Verificar se o leilão está válido
            if (convite.leilao.status == Leilao.Status.CANCELADO || 
                convite.leilao.status == Leilao.Status.CONCLUIDO) {
                throw new BusinessException("O leilão relacionado a este convite não está mais disponível");
            }
            
            // Atualizar convite
            convite.status = Convite.Status.ACEITO;
            convite.dataResposta = new Date();
            convite.persist();
            
            // Notificar comprador
            notificarRespostaConvite(convite);
            
            LOGGER.info("Convite aceito com sucesso. ID: " + convite.id);
            return convite;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao aceitar convite");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Recusa um convite para participar de um leilão.
     * 
     * @param conviteId ID do convite
     * @param motivo Motivo da recusa
     * @param fornecedor Usuário fornecedor que está recusando o convite
     * @return Convite atualizado
     */
    @Transactional
    public Convite recusarConvite(Long conviteId, String motivo, Usuario fornecedor) {
        try {
            Convite convite = buscarPorId(conviteId);
            
            if (convite == null) {
                throw new BusinessException("Convite não encontrado");
            }
            
            // Verificar permissão
            if (!fornecedor.equals(convite.fornecedor)) {
                throw new BusinessException("Você não tem permissão para responder este convite");
            }
            
            // Verificar status atual
            if (convite.status != Convite.Status.PENDENTE) {
                throw new BusinessException("Este convite já foi respondido");
            }
            
            // Atualizar convite
            convite.status = Convite.Status.RECUSADO;
            convite.motivoRecusa = motivo;
            convite.dataResposta = new Date();
            convite.persist();
            
            // Notificar comprador
            notificarRespostaConvite(convite);
            
            LOGGER.info("Convite recusado com sucesso. ID: " + convite.id);
            return convite;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao recusar convite");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Cancela um convite.
     * 
     * @param conviteId ID do convite
     * @param motivo Motivo do cancelamento
     * @param comprador Usuário comprador que está cancelando o convite
     * @return Convite cancelado
     */
    @Transactional
    public Convite cancelarConvite(Long conviteId, String motivo, Usuario comprador) {
        try {
            Convite convite = buscarPorId(conviteId);
            
            if (convite == null) {
                throw new BusinessException("Convite não encontrado");
            }
            
            // Verificar permissão (apenas o criador do leilão pode cancelar)
            if (!comprador.equals(convite.leilao.criador)) {
                throw new BusinessException("Você não tem permissão para cancelar este convite");
            }
            
            // Verificar status atual
            if (convite.status != Convite.Status.PENDENTE) {
                throw new BusinessException("Apenas convites pendentes podem ser cancelados");
            }
            
            // Atualizar convite
            convite.status = Convite.Status.CANCELADO;
            convite.motivoRecusa = motivo; // Reutilizamos o campo de motivo
            convite.dataResposta = new Date();
            convite.persist();
            
            // Notificar fornecedor
            notificarCancelamentoConvite(convite);
            
            LOGGER.info("Convite cancelado com sucesso. ID: " + convite.id);
            return convite;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao cancelar convite");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Notifica um fornecedor sobre um novo convite.
     * 
     * @param convite Novo convite
     */
    private void notificarNovoConvite(Convite convite) {
        try {
            notificacaoService.criarNotificacao(
                convite.fornecedor,
                "Novo convite para leilão",
                "Você foi convidado para participar do leilão: " + convite.leilao.titulo,
                "/convites/" + convite.id
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao notificar sobre novo convite: " + e.getMessage());
        }
    }
    
    /**
     * Notifica o comprador sobre a resposta de um convite.
     * 
     * @param convite Convite respondido
     */
    private void notificarRespostaConvite(Convite convite) {
        try {
            String statusTexto = convite.status == Convite.Status.ACEITO ? "aceitou" : "recusou";
            String mensagem = "O fornecedor " + convite.fornecedor.nome + " " + statusTexto + 
                            " seu convite para o leilão " + convite.leilao.titulo;
            
            if (convite.status == Convite.Status.RECUSADO && convite.motivoRecusa != null) {
                mensagem += ". Motivo: " + convite.motivoRecusa;
            }
            
            notificacaoService.criarNotificacao(
                convite.leilao.criador,
                "Resposta de convite",
                mensagem,
                "/leiloes/" + convite.leilao.id + "/convites"
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao notificar sobre resposta de convite: " + e.getMessage());
        }
    }
    
    /**
     * Notifica um fornecedor sobre o cancelamento de um convite.
     * 
     * @param convite Convite cancelado
     */
    private void notificarCancelamentoConvite(Convite convite) {
        try {
            String mensagem = "Seu convite para o leilão " + convite.leilao.titulo + " foi cancelado";
            
            if (convite.motivoRecusa != null) {
                mensagem += ". Motivo: " + convite.motivoRecusa;
            }
            
            notificacaoService.criarNotificacao(
                convite.fornecedor,
                "Convite cancelado",
                mensagem,
                "/convites"
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao notificar sobre cancelamento de convite: " + e.getMessage());
        }
    }
    
    /**
     * Envia um email para o fornecedor notificando sobre um novo convite.
     * 
     * @param convite Novo convite
     */
    private void enviarEmailConvite(Convite convite) {
        try {
            String assunto = "Novo convite para leilão: " + convite.leilao.titulo;
            
            StringBuilder corpo = new StringBuilder();
            corpo.append("Olá, ").append(convite.fornecedor.nome).append("!\n\n");
            corpo.append("Você foi convidado para participar do leilão \"").append(convite.leilao.titulo).append("\".\n\n");
            
            if (convite.mensagem != null && !convite.mensagem.isEmpty()) {
                corpo.append("Mensagem do comprador: ").append(convite.mensagem).append("\n\n");
            }
            
            corpo.append("Detalhes do leilão:\n");
            corpo.append("- Título: ").append(convite.leilao.titulo).append("\n");
            corpo.append("- Descrição: ").append(convite.leilao.descricao).append("\n");
            corpo.append("- Data de início: ").append(convite.leilao.dataInicio).append("\n");
            corpo.append("- Data de término: ").append(convite.leilao.dataFim).append("\n\n");
            
            corpo.append("Para responder ao convite, acesse o sistema através do link: \n");
            corpo.append("http://sistema.exemplo.com/convites/").append(convite.id).append("\n\n");
            
            corpo.append("Atenciosamente,\n");
            corpo.append("Equipe de Leilões");
            
            emailService.enviarEmail(
                convite.fornecedor.email,
                assunto,
                corpo.toString()
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao enviar email de convite: " + e.getMessage());
        }
    }
}
