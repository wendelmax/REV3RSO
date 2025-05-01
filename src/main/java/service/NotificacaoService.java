package service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import model.Mensagem;
import model.Notificacao;
import model.Usuario;

/**
 * Serviço responsável por gerenciar as notificações do sistema.
 */
@ApplicationScoped
public class NotificacaoService {
    
    private static final Logger LOGGER = Logger.getLogger(NotificacaoService.class.getName());
    
    /**
     * Notifica o destinatário sobre uma nova pergunta.
     * 
     * @param mensagem Mensagem contendo a pergunta
     */
    @Transactional
    public void notificarNovaPergunta(Mensagem mensagem) {
        try {
            Usuario destinatario;
            String titulo;
            
            // Determina o destinatário da notificação
            if (mensagem.tipo == Mensagem.Tipo.PERGUNTA_FORNECEDOR) {
                // Pergunta de fornecedor vai para o comprador (dono do leilão)
                destinatario = mensagem.leilao.criador;
                titulo = "Nova pergunta no seu leilão: " + mensagem.leilao.titulo;
            } else {
                // Pergunta do comprador vai para todos os fornecedores participantes do leilão
                // Mas esta implementação enviaria para todos. Seria necessário um mecanismo
                // para enviar para todos os fornecedores. Por enquanto, não enviamos notificação
                LOGGER.info("Notificação para pergunta de comprador não implementada");
                return;
            }
            
            criarNotificacao(
                destinatario,
                titulo,
                "Nova pergunta de " + mensagem.autor.nome + ": " + 
                (mensagem.conteudo.length() > 100 ? mensagem.conteudo.substring(0, 97) + "..." : mensagem.conteudo),
                "/mensagens/leilao/" + mensagem.leilao.id
            );
            
            LOGGER.info("Notificação de nova pergunta enviada para " + destinatario.nome);
        } catch (Exception e) {
            LOGGER.severe("Erro ao enviar notificação de nova pergunta: " + e.getMessage());
        }
    }
    
    /**
     * Notifica o destinatário sobre uma nova resposta.
     * 
     * @param resposta Mensagem contendo a resposta
     */
    @Transactional
    public void notificarNovaResposta(Mensagem resposta) {
        try {
            // O destinatário da notificação é o autor da pergunta
            Usuario destinatario = resposta.mensagemPai.autor;
            
            String titulo = "Nova resposta à sua pergunta no leilão: " + resposta.leilao.titulo;
            
            criarNotificacao(
                destinatario,
                titulo,
                "Resposta de " + resposta.autor.nome + ": " + 
                (resposta.conteudo.length() > 100 ? resposta.conteudo.substring(0, 97) + "..." : resposta.conteudo),
                "/mensagens/leilao/" + resposta.leilao.id
            );
            
            LOGGER.info("Notificação de nova resposta enviada para " + destinatario.nome);
        } catch (Exception e) {
            LOGGER.severe("Erro ao enviar notificação de nova resposta: " + e.getMessage());
        }
    }
    
    /**
     * Cria uma nova notificação para um usuário.
     * 
     * @param destinatario Usuário destinatário
     * @param titulo Título da notificação
     * @param mensagem Mensagem da notificação
     * @param link Link para redirecionamento
     * @return Notificação criada
     */
    @Transactional
    public Notificacao criarNotificacao(Usuario destinatario, String titulo, String mensagem, String link) {
        Notificacao notificacao = new Notificacao();
        notificacao.usuario = destinatario;
        notificacao.titulo = titulo;
        notificacao.mensagem = mensagem;
        notificacao.link = link;
        notificacao.dataEnvio = new Date();
        notificacao.lida = false;
        
        notificacao.persist();
        return notificacao;
    }
    
    /**
     * Busca as notificações não lidas de um usuário.
     * 
     * @param usuario Usuário
     * @return Lista de notificações não lidas
     */
    public List<Notificacao> buscarNotificacoesNaoLidas(Usuario usuario) {
        return Notificacao.find("usuario = ?1 AND lida = false ORDER BY dataEnvio DESC", usuario).list();
    }
    
    /**
     * Busca todas as notificações de um usuário.
     * 
     * @param usuario Usuário
     * @return Lista de notificações
     */
    public List<Notificacao> buscarNotificacoes(Usuario usuario) {
        return Notificacao.find("usuario = ?1 ORDER BY dataEnvio DESC", usuario).list();
    }
    
    /**
     * Marca uma notificação como lida.
     * 
     * @param notificacaoId ID da notificação
     * @param usuario Usuário dono da notificação
     * @return true se a operação foi concluída com sucesso, false caso contrário
     */
    @Transactional
    public boolean marcarComoLida(Long notificacaoId, Usuario usuario) {
        try {
            Notificacao notificacao = Notificacao.findById(notificacaoId);
            
            // Verifica se a notificação existe e pertence ao usuário
            if (notificacao == null || !notificacao.usuario.equals(usuario)) {
                return false;
            }
            
            notificacao.lida = true;
            notificacao.dataLeitura = new Date();
            notificacao.persist();
            
            return true;
        } catch (Exception e) {
            LOGGER.severe("Erro ao marcar notificação como lida: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Marca todas as notificações de um usuário como lidas.
     * 
     * @param usuario Usuário
     * @return Número de notificações marcadas como lidas
     */
    @Transactional
    public int marcarTodasComoLidas(Usuario usuario) {
        try {
            List<Notificacao> notificacoes = buscarNotificacoesNaoLidas(usuario);
            
            Date dataLeitura = new Date();
            for (Notificacao notificacao : notificacoes) {
                notificacao.lida = true;
                notificacao.dataLeitura = dataLeitura;
                notificacao.persist();
            }
            
            return notificacoes.size();
        } catch (Exception e) {
            LOGGER.severe("Erro ao marcar todas notificações como lidas: " + e.getMessage());
            return 0;
        }
    }
}
