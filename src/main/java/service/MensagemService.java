package service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import model.Leilao;
import model.Mensagem;
import model.Usuario;
import exception.BusinessException;
import util.ExceptionUtil;

/**
 * Serviço responsável por gerenciar as mensagens do sistema.
 */
@ApplicationScoped
public class MensagemService {
    
    private static final Logger LOGGER = Logger.getLogger(MensagemService.class.getName());
    
    @Inject
    NotificacaoService notificacaoService;
    
    /**
     * Busca uma mensagem pelo ID.
     * 
     * @param id ID da mensagem
     * @return Mensagem encontrada ou null se não existir
     */
    public Mensagem buscarPorId(Long id) {
        return Mensagem.findById(id);
    }
    
    /**
     * Lista todas as mensagens de um leilão.
     * 
     * @param leilao Leilão
     * @return Lista de mensagens
     */
    public List<Mensagem> listarMensagensPorLeilao(Leilao leilao) {
        try {
            return Mensagem.buscarPorLeilao(leilao);
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar mensagens do leilão " + leilao.id);
            return List.of(); // Retorna lista vazia em caso de erro
        }
    }
    
    /**
     * Verifica se um usuário pode fazer perguntas em um leilão.
     * 
     * @param usuario Usuário
     * @param leilao Leilão
     * @return true se o usuário pode fazer perguntas, false caso contrário
     */
    public boolean podePergunta(Usuario usuario, Leilao leilao) {
        try {
            if (usuario == null || leilao == null) {
                return false;
            }
            
            // Verificar tipo de usuário
            if (usuario.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR) {
                // Fornecedor só pode perguntar em leilões abertos ou se foi convidado
                return leilao.status == Leilao.Status.ABERTO && 
                       (leilao.tipoLeilao == Leilao.TipoLeilao.ABERTO || leilao.isConvidado(usuario));
            } else {
                // Comprador só pode perguntar se for o criador do Leilão
                return usuario.equals(leilao.criador);
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao verificar permissão de pergunta");
            return false;
        }
    }
    
    /**
     * Verifica se um usuário pode responder a uma mensagem.
     * 
     * @param usuario Usuário
     * @param mensagem Mensagem
     * @return true se o usuário pode responder, false caso contrário
     */
    public boolean podeResponder(Usuario usuario, Mensagem mensagem) {
        try {
            if (usuario == null || mensagem == null) {
                return false;
            }
            
            if (mensagem.tipo == Mensagem.Tipo.PERGUNTA_FORNECEDOR) {
                // Apenas o comprador pode responder perguntas de fornecedores
                return usuario.equals(mensagem.leilao.criador);
            } else if (mensagem.tipo == Mensagem.Tipo.PERGUNTA_COMPRADOR) {
                // Apenas fornecedores participantes podem responder perguntas de compradores
                return usuario.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR &&
                       (mensagem.leilao.tipoLeilao == Leilao.TipoLeilao.ABERTO || 
                        mensagem.leilao.isConvidado(usuario));
            }
            
            return false;
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao verificar permissão de resposta");
            return false;
        }
    }
    
    /**
     * Cria uma nova pergunta.
     * 
     * @param leilao Leilão
     * @param autor Autor da pergunta
     * @param conteudo Conteúdo da pergunta
     * @return Mensagem criada
     */
    @Transactional
    public Mensagem criarPergunta(Leilao leilao, Usuario autor, String conteudo) {
        try {
            // Verifica permissões
            if (!podePergunta(autor, leilao)) {
                throw BusinessException.accessDenied("pergunta em leilão");
            }
            
            // Criar a nova mensagem
            Mensagem mensagem = new Mensagem();
            mensagem.leilao = leilao;
            mensagem.autor = autor;
            mensagem.conteudo = conteudo;
            mensagem.dataCriacao = new Date();
            
            // Definir o tipo da mensagem
            if (autor.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR) {
                mensagem.tipo = Mensagem.Tipo.PERGUNTA_FORNECEDOR;
            } else {
                mensagem.tipo = Mensagem.Tipo.PERGUNTA_COMPRADOR;
            }
            
            mensagem.persist();
            
            // Notificar o destinatário da pergunta
            notificacaoService.notificarNovaPergunta(mensagem);
            
            LOGGER.info("Pergunta criada com sucesso. ID: " + mensagem.id);
            return mensagem;
        } catch (BusinessException be) {
            throw be; // Propaga exceções de negócio
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao criar pergunta");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Cria uma nova resposta.
     * 
     * @param pergunta Pergunta a ser respondida
     * @param autor Autor da resposta
     * @param conteudo Conteúdo da resposta
     * @return Mensagem de resposta criada
     */
    @Transactional
    public Mensagem criarResposta(Mensagem pergunta, Usuario autor, String conteudo) {
        try {
            // Verifica permissões
            if (!podeResponder(autor, pergunta)) {
                throw BusinessException.accessDenied("resposta a mensagem");
            }
            
            // Criar nova mensagem como resposta
            Mensagem resposta = new Mensagem();
            resposta.leilao = pergunta.leilao;
            resposta.autor = autor;
            resposta.conteudo = conteudo;
            resposta.dataCriacao = new Date();
            resposta.mensagemPai = pergunta;
            
            // Definir o tipo da resposta
            if (pergunta.tipo == Mensagem.Tipo.PERGUNTA_FORNECEDOR) {
                resposta.tipo = Mensagem.Tipo.RESPOSTA_COMPRADOR;
            } else {
                resposta.tipo = Mensagem.Tipo.RESPOSTA_FORNECEDOR;
            }
            
            resposta.persist();
            
            // Marcar pergunta como respondida
            pergunta.respondida = true;
            pergunta.persist();
            
            // Notificar o autor da pergunta sobre a resposta
            notificacaoService.notificarNovaResposta(resposta);
            
            LOGGER.info("Resposta criada com sucesso. ID: " + resposta.id);
            return resposta;
        } catch (BusinessException be) {
            throw be; // Propaga exceções de negócio
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao criar resposta");
            throw new BusinessException(mensagemErro, e);
        }
    }
}
