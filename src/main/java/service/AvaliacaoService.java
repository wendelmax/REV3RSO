package service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import model.Avaliacao;
import model.Leilao;
import model.Usuario;
import exception.BusinessException;
import util.ExceptionUtil;

/**
 * Serviço responsável por gerenciar as avaliações entre usuários após leilões.
 * Centraliza a lógica de negócio e regras para criar e gerenciar avaliações.
 */
@ApplicationScoped
public class AvaliacaoService {
    
    private static final Logger LOGGER = Logger.getLogger(AvaliacaoService.class.getName());
    
    @Inject
    NotificacaoService notificacaoService;
    
    /**
     * Busca uma avaliação pelo ID.
     * 
     * @param id ID da avaliação
     * @return Avaliação encontrada ou null se não existir
     */
    public Avaliacao buscarPorId(Long id) {
        return Avaliacao.findById(id);
    }
    
    /**
     * Lista todas as avaliações feitas por um usuário.
     * 
     * @param avaliador Usuário avaliador
     * @return Lista de avaliações
     */
    public List<Avaliacao> listarAvaliacoesFeitas(Usuario avaliador) {
        try {
            return Avaliacao.find("avaliador = ?1 ORDER BY dataCriacao DESC", avaliador).list();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar avaliações feitas pelo usuário " + avaliador.id);
            return List.of();
        }
    }
    
    /**
     * Lista todas as avaliações recebidas por um usuário.
     * 
     * @param avaliado Usuário avaliado
     * @return Lista de avaliações
     */
    public List<Avaliacao> listarAvaliacoesRecebidas(Usuario avaliado) {
        try {
            return Avaliacao.find("avaliado = ?1 ORDER BY dataCriacao DESC", avaliado).list();
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao listar avaliações recebidas pelo usuário " + avaliado.id);
            return List.of();
        }
    }
    
    /**
     * Calcula a média das notas recebidas por um usuário.
     * 
     * @param usuario Usuário
     * @return Média das notas ou 0 se não houver avaliações
     */
    public double calcularMediaAvaliacoes(Usuario usuario) {
        try {
            List<?> resultado = Avaliacao.getEntityManager()
                .createQuery("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.avaliado = :avaliado")
                .setParameter("avaliado", usuario)
                .getResultList();
            
            if (resultado != null && !resultado.isEmpty() && resultado.get(0) != null) {
                return (Double) resultado.get(0);
            }
            
            return 0;
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao calcular média de avaliações");
            return 0;
        }
    }
    
    /**
     * Verifica se um usuário já avaliou outro em um leilão específico.
     * 
     * @param avaliador Usuário avaliador
     * @param avaliado Usuário avaliado
     * @param leilao Leilão
     * @return true se já existe avaliação, false caso contrário
     */
    public boolean jaAvaliou(Usuario avaliador, Usuario avaliado, Leilao leilao) {
        try {
            return Avaliacao.count(
                "avaliador = ?1 AND avaliado = ?2 AND leilao = ?3", 
                avaliador, avaliado, leilao) > 0;
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao verificar se usuário já avaliou");
            return false;
        }
    }
    
    /**
     * Verifica se um usuário pode avaliar outro em um leilão específico.
     * 
     * @param avaliador Usuário avaliador
     * @param avaliado Usuário avaliado
     * @param leilao Leilão
     * @return true se pode avaliar, false caso contrário
     */
    public boolean podeAvaliar(Usuario avaliador, Usuario avaliado, Leilao leilao) {
        try {
            if (avaliador == null || avaliado == null || leilao == null) {
                return false;
            }
            
            // Verificar se o leilão está concluído
            if (leilao.status != Leilao.Status.CONCLUIDO) {
                return false;
            }
            
            // Verificar se não avaliou ainda
            if (jaAvaliou(avaliador, avaliado, leilao)) {
                return false;
            }
            
            // Casos permitidos:
            // 1. Comprador (criador do leilão) avaliando um fornecedor que participou
            if (avaliador.equals(leilao.criador) && avaliado.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR) {
                return leilao.temParticipacao(avaliado);
            }
            
            // 2. Fornecedor que participou avaliando o comprador (criador do leilão)
            if (avaliador.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR && 
                avaliado.equals(leilao.criador)) {
                return leilao.temParticipacao(avaliador);
            }
            
            return false;
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao verificar se usuário pode avaliar");
            return false;
        }
    }
    
    /**
     * Cria uma nova avaliação.
     * 
     * @param leilao Leilão
     * @param avaliador Usuário avaliador
     * @param avaliado Usuário avaliado
     * @param nota Nota (1-5)
     * @param comentario Comentário
     * @return Avaliação criada
     */
    @Transactional
    public Avaliacao criarAvaliacao(Leilao leilao, Usuario avaliador, Usuario avaliado,
                                  Integer nota, String comentario) {
        try {
            // Verificar permissão
            if (!podeAvaliar(avaliador, avaliado, leilao)) {
                throw new BusinessException("Você não tem permissão para avaliar este usuário neste leilão");
            }
            
            // Validar nota
            if (nota < 1 || nota > 5) {
                throw new BusinessException("A nota deve ser entre 1 e 5");
            }
            
            // Criar avaliação
            Avaliacao avaliacao = new Avaliacao();
            avaliacao.leilao = leilao;
            avaliacao.avaliador = avaliador;
            avaliacao.avaliado = avaliado;
            avaliacao.nota = nota;
            avaliacao.comentario = comentario;
            avaliacao.dataCriacao = new Date();
            
            avaliacao.persist();
            
            // Notificar o avaliado
            notificarAvaliacao(avaliacao);
            
            // Atualizar a reputação do avaliado
            atualizarReputacao(avaliado);
            
            LOGGER.info("Avaliação criada com sucesso. ID: " + avaliacao.id);
            return avaliacao;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao criar avaliação");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Registra uma réplica a uma avaliação recebida.
     * 
     * @param avaliacaoId ID da avaliação
     * @param replica Texto da réplica
     * @param usuario Usuário que está replicando
     * @return Avaliação atualizada
     */
    @Transactional
    public Avaliacao replicarAvaliacao(Long avaliacaoId, String replica, Usuario usuario) {
        try {
            Avaliacao avaliacao = buscarPorId(avaliacaoId);
            
            if (avaliacao == null) {
                throw new BusinessException("Avaliação não encontrada");
            }
            
            // Apenas o avaliado pode replicar
            if (!usuario.equals(avaliacao.avaliado)) {
                throw new BusinessException("Apenas o usuário avaliado pode criar uma réplica");
            }
            
            // Verificar se já tem réplica
            if (avaliacao.replica != null && !avaliacao.replica.isEmpty()) {
                throw new BusinessException("Esta avaliação já possui uma réplica");
            }
            
            // Registrar réplica
            avaliacao.replica = replica;
            avaliacao.dataReplica = new Date();
            
            avaliacao.persist();
            
            // Notificar o avaliador
            notificarReplica(avaliacao);
            
            LOGGER.info("Réplica registrada com sucesso. Avaliação ID: " + avaliacao.id);
            return avaliacao;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao registrar réplica");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Atualiza a reputação (pontuação) de um usuário com base em suas avaliações.
     * 
     * @param usuario Usuário
     */
    @Transactional
    public void atualizarReputacao(Usuario usuario) {
        try {
            double media = calcularMediaAvaliacoes(usuario);
            long total = Avaliacao.count("avaliado", usuario);
            
            // Atualizar a pontuação do usuário
            usuario.pontuacao = media;
            usuario.totalAvaliacoes = total;
            usuario.persist();
            
            LOGGER.info("Reputação atualizada. Usuário: " + usuario.id + ", Pontuação: " + media);
        } catch (Exception e) {
            LOGGER.severe("Erro ao atualizar reputação: " + e.getMessage());
        }
    }
    
    /**
     * Notifica um usuário sobre uma nova avaliação recebida.
     * 
     * @param avaliacao Avaliação
     */
    private void notificarAvaliacao(Avaliacao avaliacao) {
        try {
            notificacaoService.criarNotificacao(
                avaliacao.avaliado,
                "Nova avaliação recebida",
                "Você recebeu uma avaliação com nota " + avaliacao.nota + " de " + avaliacao.avaliador.nome,
                "/avaliacoes/recebidas"
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao notificar sobre nova avaliação: " + e.getMessage());
        }
    }
    
    /**
     * Notifica um usuário sobre uma réplica à sua avaliação.
     * 
     * @param avaliacao Avaliação
     */
    private void notificarReplica(Avaliacao avaliacao) {
        try {
            notificacaoService.criarNotificacao(
                avaliacao.avaliador,
                "Réplica à sua avaliação",
                avaliacao.avaliado.nome + " respondeu à sua avaliação no leilão " + avaliacao.leilao.titulo,
                "/avaliacoes/minhas"
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao notificar sobre réplica: " + e.getMessage());
        }
    }
}
