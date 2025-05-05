package controller;

import java.util.Date;
import java.util.List;

import annotation.Pageable;
import model.Avaliacao;
import model.Leilao;
import model.Usuario;
import model.Lance;
import util.PaginationUtil;
import util.RedirectUtil;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/avaliacoes")
public class AvaliacaoController extends BaseController {
    
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance avaliar(Leilao leilao, Usuario avaliado);
        public static native TemplateInstance minhas(List<Avaliacao> avaliacoes);
        public static native TemplateInstance recebidas(List<Avaliacao> avaliacoes);
    }
    
    // Formulário para avaliar um usuário
    @Path("/avaliar/{leilaoId}/{avaliado}")
    public TemplateInstance avaliar(@PathParam("leilaoId") Long leilaoId, @PathParam("avaliado") Long avaliadoId) {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para acessar esta página");
            flash("tipo", "danger");
            return RedirectUtil.redirectTemplate("/usuarios/login");
        }
        
        Leilao leilao = Leilao.findById(leilaoId);
        if (leilao == null) {
            flash("mensagem", "Leilão não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectTemplate("/leiloes");
        }
        
        Usuario avaliado = Usuario.findById(avaliadoId);
        if (avaliado == null) {
            flash("mensagem", "Usuário não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectTemplate("/leiloes/" + leilaoId);
        }
        
        // Verificar se o leilão está concluído
        if (leilao.status != Leilao.Status.CONCLUIDO) {
            flash("mensagem", "Só é possível avaliar após a conclusão do leilão");
            flash("tipo", "danger");
            return RedirectUtil.redirectTemplate("/leiloes/" + leilaoId);
        }
        
        // Verificar se o usuário tem relação com o leilão
        boolean relacionado = false;
        
        // Caso 1: Comprador avaliando o fornecedor vencedor
        if (usuario.equals(leilao.criador)) {
            Lance lanceVencedor = Lance.menorLanceDoLeilao(leilao);
            if (lanceVencedor != null && lanceVencedor.fornecedor.equals(avaliado)) {
                relacionado = true;
            }
        }
        // Caso 2: Fornecedor vencedor avaliando o comprador
        else if (usuario.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR) {
            Lance lanceVencedor = Lance.menorLanceDoLeilao(leilao);
            if (lanceVencedor != null && lanceVencedor.fornecedor.equals(usuario) && avaliado.equals(leilao.criador)) {
                relacionado = true;
            }
        }
        
        if (!relacionado) {
            flash("mensagem", "Você não tem permissão para avaliar este usuário");
            flash("tipo", "danger");
            return RedirectUtil.redirectTemplate("/leiloes/" + leilaoId);
        }
        
        // Verificar se já avaliou este usuário neste leilão
        if (Avaliacao.jaAvaliou(leilao, usuario, avaliado)) {
            flash("mensagem", "Você já avaliou este usuário neste leilão");
            flash("tipo", "danger");
            return RedirectUtil.redirectTemplate("/leiloes/" + leilaoId);
        }
        
        return Templates.avaliar(leilao, avaliado);
    }
    
    // Ação para salvar a avaliação
    @POST
    @Path("/salvar")
    @Transactional
    public Uni<Object> salvar(
            @FormParam("leilaoId") @NotNull Long leilaoId,
            @FormParam("avaliadoId") @NotNull Long avaliadoId,
            @FormParam("nota") @NotNull @Min(1) @Max(5) Integer nota,
            @FormParam("comentario") @NotBlank String comentario) {
        
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para realizar esta ação");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/login");
        }
        
        Leilao leilao = Leilao.findById(leilaoId);
        if (leilao == null) {
            flash("mensagem", "Leilão não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/leiloes");
        }
        
        Usuario avaliado = Usuario.findById(avaliadoId);
        if (avaliado == null) {
            flash("mensagem", "Usuário não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/leiloes/" + leilaoId);
        }
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, corrija os erros no formulário");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/avaliacoes/avaliar/" + leilaoId + "/" + avaliadoId);
        }
        
        // Verificar se já avaliou este usuário neste leilão
        if (Avaliacao.jaAvaliou(leilao, usuario, avaliado)) {
            flash("mensagem", "Você já avaliou este usuário neste leilão");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/leiloes/" + leilaoId);
        }
        
        // Criar nova avaliação
        Avaliacao avaliacao = new Avaliacao(leilao, usuario, avaliado, nota, comentario);
        avaliacao.persist();
        
        // Atualizar pontuação do avaliado com base na nota
        // 5 estrelas: +1 ponto
        // 3-4 estrelas: +0.5 ponto
        // 1-2 estrelas: -0.5 ponto
        double pontuacao = 0;
        if (nota >= 5) {
            pontuacao = 1.0;
        } else if (nota >= 3) {
            pontuacao = 0.5;
        } else {
            pontuacao = -0.5;
        }
        
        avaliado.atualizarPontuacao(pontuacao);
        
        flash("mensagem", "Avaliação enviada com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/leiloes/" + leilaoId);
    }
    
    // Adicionar réplica a uma avaliação recebida
    @POST
    @Path("/replica/{id}")
    @Transactional
    public Uni<Object> replica(
            @PathParam("id") Long id,
            @FormParam("replica") @NotBlank String replica) {
        
        Avaliacao avaliacao = Avaliacao.findById(id);
        if (avaliacao == null) {
            flash("mensagem", "Avaliação não encontrada");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/avaliacoes/recebidas");
        }
        
        Usuario usuario = usuarioLogado();
        if (usuario == null || !usuario.equals(avaliacao.avaliado)) {
            flash("mensagem", "Você não tem permissão para responder a esta avaliação");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/avaliacoes/recebidas");
        }
        
        if (validationFailed()) {
            flash("mensagem", "A réplica não pode estar em branco");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/avaliacoes/recebidas");
        }
        
        // Adicionar réplica
        avaliacao.adicionarReplica(replica);
        
        flash("mensagem", "Réplica adicionada com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/avaliacoes/recebidas");
    }
    
    // Visualizar avaliações feitas pelo usuário logado
    @Path("/minhas")
    @Pageable
    public TemplateInstance minhasAvaliacoes() {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para acessar esta página");
            flash("tipo", "danger");
            return RedirectUtil.redirectTemplate("/usuarios/login");
        }
        
        List<Avaliacao> todasAvaliacoes = Avaliacao.listarPorAvaliador(usuario);
        PaginationUtil.PagedResult<Avaliacao> avaliacoes = applyPagination(todasAvaliacoes);
        
        // Quarkus Qute espera apenas um argumento se o template for definido assim
        return Templates.minhas(avaliacoes.getContent());
    }
    
    /**
     * Aplica a paginação em uma lista de avaliações
     * 
     * @param lista Lista completa de avaliações
     * @return Resultado paginado
     */
    private <T> PaginationUtil.PagedResult<T> applyPagination(List<T> lista) {
        // Valores padrão para paginação
        int page = 0;
        int size = 10;
        
        // Em uma implementação real, obteríamos os parâmetros da requisição
        // No Renarde, o método getRequest() está disponível na classe Controller
        String pageParam = request().getParameter("page");
        String sizeParam = request().getParameter("size");
        
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                // Ignora e usa o valor padrão
            }
        }
        
        if (sizeParam != null && !sizeParam.isEmpty()) {
            try {
                size = Integer.parseInt(sizeParam);
            } catch (NumberFormatException e) {
                // Ignora e usa o valor padrão
            }
        }
        
        // Aplica a paginação usando o método correto
        return PaginationUtil.PagedResult.of(lista, page, size, 5);  // 5 é o número máximo de links de página
    }
    
    /**
     * Obtém um parâmetro da requisição atual
     * 
     * @param name Nome do parâmetro
     * @return Valor do parâmetro ou null se não existir
     */
    private String getRequestParameter(String name) {
        return request().getParameter(name);
    }
    
    // Visualizar avaliações recebidas pelo usuário logado
    @Path("/recebidas")
    @Pageable
    public TemplateInstance avaliacoesRecebidas() {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para acessar esta página");
            flash("tipo", "danger");
            return RedirectUtil.redirectTemplate("/usuarios/login");
        }
        
        List<Avaliacao> todasAvaliacoes = Avaliacao.listarPorAvaliado(usuario);
        PaginationUtil.PagedResult<Avaliacao> avaliacoes = applyPagination(todasAvaliacoes);
        
        // Quarkus Qute espera apenas um argumento se o template for definido assim
        return Templates.recebidas(avaliacoes.getContent());
    }
    
    // Implementação do método usuarioLogado já está no BaseController, não precisamos reimplementar
    // O método a seguir foi mantido apenas como documentação do comportamento
    /* 
    @Override
    protected Usuario usuarioLogado() {
        Long usuarioId = session().get("usuarioId", Long.class);
        if (usuarioId == null) {
            return null;
        }
        return Usuario.findById(usuarioId);
    }
}
