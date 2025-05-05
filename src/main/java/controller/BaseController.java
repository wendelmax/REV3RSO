package controller;

import io.quarkiverse.renarde.Controller;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

import model.Usuario;
import service.AutenticacaoService;
import service.PaginationService;
import util.PaginationUtil;
import util.RedirectUtil;

import java.util.List;

/**
 * Controlador base que estende o Controller do Renarde.
 * Fornece funcionalidades comuns para todos os controladores da aplicação.
 */
public abstract class BaseController extends Controller {
    
    @Inject
    protected PaginationService paginationService;
    
    @Inject
    protected AutenticacaoService autenticacaoService;
    
    @Context
    protected HttpHeaders headers;
    
    /**
     * Obtém o usuário atualmente logado.
     * 
     * @return Usuário logado ou null se não houver usuário logado
     */
    protected Usuario usuarioLogado() {
        return autenticacaoService.getUsuarioLogado();
    }
    
    /**
     * Verifica se há um usuário logado.
     * 
     * @return true se houver um usuário logado, false caso contrário
     */
    protected boolean isUsuarioLogado() {
        return autenticacaoService.getUsuarioLogado() != null;
    }
    
    /**
     * Obtém um parâmetro da requisição atual
     * 
     * @param name Nome do parâmetro
     * @return Valor do parâmetro ou null se não existir
     */
    protected String getRequestParameter(String name) {
        io.vertx.ext.web.RoutingContext ctx = io.quarkus.arc.Arc.container().instance(io.vertx.ext.web.RoutingContext.class).get();
        return ctx.request().getParam(name);
    }
    
    /**
     * Adiciona uma mensagem de erro no flash.
     * 
     * @param mensagem Mensagem de erro
     */
    protected void flashErro(String mensagem) {
        flash("mensagem", mensagem);
        flash("tipo", "danger");
    }
    
    /**
     * Adiciona uma mensagem de sucesso no flash.
     * 
     * @param mensagem Mensagem de sucesso
     */
    protected void flashSucesso(String mensagem) {
        flash("mensagem", mensagem);
        flash("tipo", "success");
    }
    
    /**
     * Adiciona uma mensagem de alerta no flash.
     * 
     * @param mensagem Mensagem de alerta
     */
    protected void flashAlerta(String mensagem) {
        flash("mensagem", mensagem);
        flash("tipo", "warning");
    }
    
    /**
     * Adiciona uma mensagem informativa no flash.
     * 
     * @param mensagem Mensagem informativa
     */
    protected void flashInfo(String mensagem) {
        flash("mensagem", mensagem);
        flash("tipo", "info");
    }
    
    /**
     * Obtém os parâmetros de paginação dos headers da requisição.
     * 
     * @param defaultPage Página padrão se não for especificada
     * @param defaultSize Tamanho padrão da página se não for especificado
     * @return Array com [página, tamanho]
     */
    protected int[] getPaginationParams(int defaultPage, int defaultSize) {
        int page = defaultPage;
        int size = defaultSize;
        
        // Se a paginação por header estiver habilitada, tenta obter os parâmetros dos headers
        if (paginationService.isHeaderPaginationEnabled() && headers != null) {
            String pageHeader = paginationService.getPageHeaderName();
            String sizeHeader = paginationService.getSizeHeaderName();
            
            if (headers.getRequestHeaders().containsKey(pageHeader)) {
                try {
                    page = Integer.parseInt(headers.getRequestHeader(pageHeader).get(0));
                } catch (NumberFormatException e) {
                    // Ignora e usa o valor padrão
                }
            }
            
            if (headers.getRequestHeaders().containsKey(sizeHeader)) {
                try {
                    size = Integer.parseInt(headers.getRequestHeader(sizeHeader).get(0));
                    // Limita ao tamanho máximo
                    size = Math.min(size, paginationService.getMaxPageSize());
                } catch (NumberFormatException e) {
                    // Ignora e usa o valor padrão
                }
            }
        }
        
        return new int[] { page, size };
    }
    
    /**
     * Aplica paginação a uma lista de itens com base nos headers da requisição.
     * 
     * @param <T> Tipo dos itens da lista
     * @param items Lista completa de itens
     * @return Resultado paginado
     */
    protected <T> PaginationUtil.PagedResult<T> applyPagination(List<T> items) {
        int[] params = getPaginationParams(0, paginationService.getDefaultPageSize());
        int page = params[0];
        int size = params[1];
        
        return PaginationUtil.PagedResult.of(items, page, size, 5);
    }
    
    /**
     * Aplica paginação a uma lista de itens com base nos parâmetros fornecidos.
     * 
     * @param <T> Tipo dos itens da lista
     * @param items Lista completa de itens
     * @param page Página solicitada
     * @param size Tamanho da página solicitado
     * @return Resultado paginado
     */
    protected <T> PaginationUtil.PagedResult<T> applyPagination(List<T> items, int page, int size) {
        // Garante que o tamanho não exceda o máximo
        size = Math.min(size, paginationService.getMaxPageSize());
        return PaginationUtil.PagedResult.of(items, page, size, 5);
    }
    
    /**
     * Verifica se o usuário tem permissão para realizar uma ação em um recurso.
     * 
     * @param usuario Usuário a ser verificado
     * @param acao Nome da ação
     * @param recurso Recurso a ser acessado
     * @return true se o usuário tem permissão, false caso contrário
     */
    protected boolean temPermissao(Usuario usuario, String acao, Object recurso) {
        return autenticacaoService.temPermissao(usuario, acao, recurso);
    }
    
    /**
     * Redireciona para uma rota segura.
     * Se o usuário não estiver autenticado, redireciona para a página de login.
     * 
     * @param targetClass Classe do controlador de destino
     * @return Redirecionamento
     */
    protected Uni<Object> redirecionarSeguro(Class<? extends Controller> targetClass) {
        if (usuarioLogado() == null) {
            flashErro("Você precisa estar logado para acessar esta página");
            return RedirectUtil.redirect(UsuarioController.class);
        }
        return RedirectUtil.redirect(targetClass);
    }
}
