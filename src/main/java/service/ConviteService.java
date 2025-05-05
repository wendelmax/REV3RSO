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
 * Serviu00e7o responsu00e1vel por gerenciar as operau00e7u00f5es relacionadas aos convites para leilu00f5es fechados.
 * Centraliza a lu00f3gica de negu00f3cio e regras para criar e gerenciar convites.
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
     * @return Convite encontrado ou null se nu00e3o existir
     */
    public Convite buscarPorId(Long id) {
        return Convite.findById(id);
    }
    
    /**
     * Lista todos os convites de um leilu00e3o.
     * 
     * @param leilao Leilu00e3o
     * @return Lista de convites
     */
    public List<Convite> listarConvitesPorLeilao(Leilao leilao) {
        try {
            return Convite.find("leilao = ?1 ORDER BY dataCriacao DESC", leilao).list();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar convites do leilu00e3o " + leilao.id);
            return List.of();
        }
    }
    
    /**
     * Lista todos os convites enviados a um fornecedor.
     * 
     * @param fornecedor Usuu00e1rio fornecedor
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
     * Lista todos os convites aceitos de um leilu00e3o.
     * 
     * @param leilao Leilu00e3o
     * @return Lista de convites aceitos
     */
    public List<Convite> listarConvitesAceitos(Leilao leilao) {
        try {
            return Convite.find("leilao = ?1 AND status = ?2 ORDER BY dataResposta DESC", 
                leilao, Convite.Status.ACEITO).list();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar convites aceitos do leilu00e3o " + leilao.id);
            return List.of();
        }
    }
    
    /**
     * Lista todos os convites pendentes de um leilu00e3o.
     * 
     * @param leilao Leilu00e3o
     * @return Lista de convites pendentes
     */
    public List<Convite> listarConvitesPendentes(Leilao leilao) {
        try {
            return Convite.find("leilao = ?1 AND status = ?2 ORDER BY dataCriacao DESC", 
                leilao, Convite.Status.PENDENTE).list();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar convites pendentes do leilu00e3o " + leilao.id);
            return List.of();
        }
    }
    
    /**
     * Verifica se um fornecedor ju00e1 foi convidado para um leilu00e3o.
     * 
     * @param fornecedor Usuu00e1rio fornecedor
     * @param leilao Leilu00e3o
     * @return true se ju00e1 foi convidado, false caso contru00e1rio
     */
    public boolean jaConvidado(Usuario fornecedor, Leilao leilao) {
        try {
            return Convite.count("fornecedor = ?1 AND leilao = ?2", fornecedor, leilao) > 0;
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao verificar se o fornecedor ju00e1 foi convidado");
            return false;
        }
    }
    
    /**
     * Cria um novo convite para um fornecedor participar de um leilu00e3o.
     * 
     * @param leilao Leilu00e3o
     * @param fornecedor Usuu00e1rio fornecedor
     * @param comprador Usuu00e1rio comprador (criador do leilu00e3o)
     * @param mensagem Mensagem opcional do comprador
     * @return Novo convite criado
     */
    @Transactional
    public Convite criarConvite(Leilao leilao, Usuario fornecedor, Usuario comprador, String mensagem) {
        try {
            // Verificar se o leilu00e3o existe e estu00e1 vu00e1lido para convites
            if (leilao == null) {
                throw new BusinessException("Leilu00e3o nu00e3o encontrado");
            }
            
            // Verificar se o fornecedor existe
            if (fornecedor == null) {
                throw new BusinessException("Fornecedor nu00e3o encontrado");
            }
            
            // Verificar se o fornecedor ju00e1 foi convidado
            if (jaConvidado(fornecedor, leilao)) {
                throw new BusinessException("Este fornecedor ju00e1 foi convidado para este leilu00e3o");
            }
            
            // Criar o convite
            Convite convite = new Convite(leilao, fornecedor);
            convite.mensagem = mensagem;
            convite.dataCriacao = new Date();
            convite.persist();
            
            // Notificar o fornecedor
            notificarNovoConvite(convite);
            
            // Enviar email
            enviarEmailConvite(convite);
            
            return convite;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            throw new BusinessException("Erro ao criar convite: " + e.getMessage(), e);
        }
    }
    
    /**
     * Aceita um convite para participar de um leilu00e3o.
     * 
     * @param conviteId ID do convite
     * @param fornecedor Usuu00e1rio fornecedor que estu00e1 aceitando
     * @return Convite atualizado
     */
    @Transactional
    public Convite aceitarConvite(Long conviteId, Usuario fornecedor) {
        try {
            // Buscar o convite
            Convite convite = buscarPorId(conviteId);
            if (convite == null) {
                throw new BusinessException("Convite nu00e3o encontrado");
            }
            
            // Verificar se o fornecedor u00e9 o mesmo do convite
            if (!convite.fornecedor.id.equals(fornecedor.id)) {
                throw new BusinessException("Vocu00ea nu00e3o tem permissu00e3o para aceitar este convite");
            }
            
            // Verificar se o convite estu00e1 pendente
            if (convite.status != Convite.Status.PENDENTE) {
                throw new BusinessException("Este convite nu00e3o estu00e1 pendente de resposta");
            }
            
            // Verificar se o leilu00e3o ainda estu00e1 aberto
            if (convite.leilao.status != Leilao.Status.AGENDADO && 
                convite.leilao.status != Leilao.Status.ABERTO) {
                throw new BusinessException("Este leilu00e3o nu00e3o estu00e1 mais aberto para convites");
            }
            
            // Aceitar o convite
            convite.aceitar();
            
            // Notificar o comprador
            notificarRespostaConvite(convite);
            
            return convite;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            throw new BusinessException("Erro ao aceitar convite: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recusa um convite para participar de um leilu00e3o.
     * 
     * @param conviteId ID do convite
     * @param motivo Motivo da recusa (opcional)
     * @param fornecedor Usuu00e1rio fornecedor que estu00e1 recusando
     * @return Convite atualizado
     */
    @Transactional
    public Convite recusarConvite(Long conviteId, String motivo, Usuario fornecedor) {
        try {
            // Buscar o convite
            Convite convite = buscarPorId(conviteId);
            if (convite == null) {
                throw new BusinessException("Convite nu00e3o encontrado");
            }
            
            // Verificar se o fornecedor u00e9 o mesmo do convite
            if (!convite.fornecedor.id.equals(fornecedor.id)) {
                throw new BusinessException("Vocu00ea nu00e3o tem permissu00e3o para recusar este convite");
            }
            
            // Verificar se o convite estu00e1 pendente
            if (convite.status != Convite.Status.PENDENTE) {
                throw new BusinessException("Este convite nu00e3o estu00e1 pendente de resposta");
            }
            
            // Recusar o convite
            convite.recusar();
            convite.motivoRecusa = motivo;
            convite.persist();
            
            // Notificar o comprador
            notificarRespostaConvite(convite);
            
            return convite;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            throw new BusinessException("Erro ao recusar convite: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cancela um convite.
     * 
     * @param conviteId ID do convite
     * @param motivo Motivo do cancelamento (opcional)
     * @param comprador Usuu00e1rio comprador que estu00e1 cancelando
     * @return Convite atualizado
     */
    @Transactional
    public Convite cancelarConvite(Long conviteId, String motivo, Usuario comprador) {
        try {
            // Buscar o convite
            Convite convite = buscarPorId(conviteId);
            if (convite == null) {
                throw new BusinessException("Convite nu00e3o encontrado");
            }
            
            // Verificar se o comprador u00e9 o criador do leilu00e3o
            if (!convite.leilao.criador.id.equals(comprador.id)) {
                throw new BusinessException("Vocu00ea nu00e3o tem permissu00e3o para cancelar este convite");
            }
            
            // Verificar se o convite estu00e1 pendente
            if (convite.status != Convite.Status.PENDENTE) {
                throw new BusinessException("Apenas convites pendentes podem ser cancelados");
            }
            
            // Cancelar o convite
            convite.status = Convite.Status.RECUSADO;
            convite.motivoRecusa = motivo;
            convite.dataResposta = new Date();
            convite.persist();
            
            // Notificar o fornecedor
            notificarCancelamentoConvite(convite);
            
            return convite;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            throw new BusinessException("Erro ao cancelar convite: " + e.getMessage(), e);
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
                "Novo convite para leilu00e3o", 
                "Vocu00ea foi convidado para participar do leilu00e3o '" + convite.leilao.titulo + "'", 
                "/convites"
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
            String mensagem = "O fornecedor " + convite.fornecedor.nomeFantasia + " " + statusTexto + 
                             " seu convite para o leilu00e3o " + convite.leilao.titulo;
            
            if (convite.status == Convite.Status.RECUSADO && convite.motivoRecusa != null) {
                mensagem += ". Motivo: " + convite.motivoRecusa;
            }
            
            notificacaoService.criarNotificacao(
                convite.leilao.criador, 
                "Resposta ao convite", 
                mensagem, 
                "/leiloes/" + convite.leilao.id
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
            String mensagem = "Seu convite para o leilu00e3o '" + convite.leilao.titulo + "' foi cancelado";
            
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
            String assunto = "Novo convite para leilu00e3o: " + convite.leilao.titulo;
            
            StringBuilder corpo = new StringBuilder();
            corpo.append("Olu00e1, ").append(convite.fornecedor.nomeFantasia).append("!\n\n");
            corpo.append("Vocu00ea foi convidado para participar do leilu00e3o \"").append(convite.leilao.titulo).append("\".\n\n");
            
            if (convite.mensagem != null && !convite.mensagem.isEmpty()) {
                corpo.append("Mensagem do comprador: ").append(convite.mensagem).append("\n\n");
            }
            
            corpo.append("Detalhes do leilu00e3o:\n");
            corpo.append("- Tu00edtulo: ").append(convite.leilao.titulo).append("\n");
            corpo.append("- Descriu00e7u00e3o: ").append(convite.leilao.descricao).append("\n");
            corpo.append("- Data de inu00edcio: ").append(convite.leilao.dataInicio).append("\n");
            corpo.append("- Data de tu00e9rmino: ").append(convite.leilao.dataFim).append("\n\n");
            
            corpo.append("Para responder ao convite, acesse o sistema atravu00e9s do link: \n");
            corpo.append("http://sistema.exemplo.com/convites/").append(convite.id).append("\n\n");
            
            corpo.append("Atenciosamente,\n");
            corpo.append("Equipe de Leilu00f5es");
            
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
