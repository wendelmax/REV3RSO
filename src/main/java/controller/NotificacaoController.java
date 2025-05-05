package controller;

import java.util.List;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import model.Notificacao;
import model.Usuario;
import service.NotificacaoService;
import util.RedirectUtil;
import dto.NotificacaoDTO;

/**
 * Controlador para gerenciar as notificações do usuário.
 */
@Path("/notificacoes")
public class NotificacaoController extends BaseController {
    
    @Inject
    NotificacaoService notificacaoService;
    
    @CheckedTemplate(basePath = "Notificacao", requireTypeSafeExpressions = false)
    public static class Templates {
        public static native TemplateInstance listar(List<Notificacao> notificacoes, long naoLidas);
    }
    
    /**
     * Exibe a lista de notificações do usuário logado.
     * 
     * @return Template com a lista de notificações
     */
    @GET
    public TemplateInstance listar() {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para acessar suas notificações");
            flash("tipo", "danger");
            return RedirectUtil.redirectTemplate("/usuarios/login");
        }
        
        List<Notificacao> notificacoes = notificacaoService.buscarNotificacoes(usuario);
        long naoLidas = notificacoes.stream().filter(n -> !n.lida).count();
        
        return Templates.listar(notificacoes, naoLidas);
    }
    
    /**
     * Marca uma notificação como lida.
     * 
     * @param id ID da notificação
     * @return Redirecionamento para a lista de notificações
     */
    @POST
    @Path("/marcar-lida/{id}")
    @Transactional
    public Response marcarComoLida(@PathParam("id") Long id) {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        boolean sucesso = notificacaoService.marcarComoLida(id, usuario);
        
        if (sucesso) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    /**
     * Marca todas as notificações do usuário como lidas.
     * 
     * @return Redirecionamento para a lista de notificações
     */
    @POST
    @Path("/marcar-todas-lidas")
    @Transactional
    public Response marcarTodasComoLidas() {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        int quantidade = notificacaoService.marcarTodasComoLidas(usuario);
        
        return Response.ok()
                .entity(java.util.Map.of("success", true, "count", quantidade))
                .build();
    }
    
    /**
     * Retorna o número de notificações não lidas do usuário logado.
     * Endpoint usado para atualizar o contador no navbar.
     * 
     * @return Número de notificações não lidas
     */
    @GET
    @Path("/nao-lidas/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response contarNaoLidas() {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        List<Notificacao> naoLidas = notificacaoService.buscarNotificacoesNaoLidas(usuario);
        
        return Response.ok(java.util.Map.of("count", naoLidas.size())).build();
    }
    
    /**
     * Retorna as últimas notificações não lidas do usuário logado.
     * Endpoint usado para exibir notificações no dropdown do navbar.
     * 
     * @return Lista das últimas notificações não lidas
     */
    @GET
    @Path("/recentes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response notificacoesRecentes() {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        List<Notificacao> naoLidas = notificacaoService.buscarNotificacoesNaoLidas(usuario);
        List<NotificacaoDTO> dtos = NotificacaoDTO.converterLista(
                naoLidas.stream().limit(5).toList());
        
        return Response.ok(java.util.Map.of(
                "notificacoes", dtos, 
                "total", naoLidas.size())).build();
    }
}
