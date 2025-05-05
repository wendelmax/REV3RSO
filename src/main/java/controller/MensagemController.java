package controller;

import java.util.List;

import io.smallrye.mutiny.Uni;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import annotation.RequiresAuth;
import model.Usuario;
import util.RedirectUtil;
import model.Leilao;
import model.Mensagem;
import service.MensagemService;

/**
 * Controlador responsável por gerenciar as operações relacionadas às mensagens.
 * Implementa o padrão MVC para as funcionalidades de comunicação entre usuários.
 */
@Path("/mensagens")
public class MensagemController extends BaseController {
    
    @Inject
    MensagemService mensagemService;
    
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance listar(Leilao leilao, List<Mensagem> mensagens);
        public static native TemplateInstance responder(Mensagem mensagem);
    }
    
    /**
     * Lista todas as mensagens de um leilão.
     * 
     * @param leilaoId ID do leilão.
     * @return Template com a lista de mensagens.
     */
    @GET
    @Path("/leilao/{id}")
    @RequiresAuth
    public Uni<Object> listar(@PathParam("id") Long leilaoId) {
        Leilao leilao = Leilao.findById(leilaoId);
        if (leilao == null) {
            flashErro("Leilão não encontrado");
            return RedirectUtil.redirect(LeilaoController.class);
        }
        
        Usuario usuario = usuarioLogado();
        
        // Verifica permissão usando o serviço de autenticação
        if (!temPermissao(usuario, "visualizar", leilao)) {
            flashErro("Você não tem permissão para acessar esta página");
            return RedirectUtil.redirect(LeilaoController.class);
        }
        
        List<Mensagem> mensagens = mensagemService.listarMensagensPorLeilao(leilao);
        return Uni.createFrom().item(Templates.listar(leilao, mensagens));
    }
    
    /**
     * Envia uma nova pergunta em um leilão.
     * 
     * @param leilaoId ID do leilão.
     * @param conteudo Conteúdo da pergunta.
     * @return Redirecionamento para a página de mensagens do leilão.
     */
    @POST
    @Path("/perguntar")
    @Transactional
    @RequiresAuth
    public Uni<Object> enviarPergunta(
            @FormParam("leilaoId") @NotNull Long leilaoId,
            @FormParam("conteudo") @NotBlank String conteudo) {
        
        Usuario usuario = usuarioLogado();
        
        Leilao leilao = Leilao.findById(leilaoId);
        if (leilao == null) {
            flashErro("Leilão não encontrado");
            return RedirectUtil.redirectToPathAsObject("/leiloes");
        }
        
        if (validationFailed()) {
            flashErro("Por favor, preencha o conteúdo da pergunta");
            return RedirectUtil.redirectToPathAsObject("/mensagens/leilao/" + leilaoId);
        }
        
        // Verifica permissões usando o serviço
        if (!mensagemService.podePergunta(usuario, leilao)) {
            flashErro("Você não tem permissão para enviar perguntas neste Leilão");
            return RedirectUtil.redirectToPathAsObject("/leiloes/" + leilaoId);
        }
        
        // Delega a criação da mensagem para o serviço
        try {
            mensagemService.criarPergunta(leilao, usuario, conteudo);
            flashSucesso("Pergunta enviada com sucesso!");
        } catch (Exception e) {
            flashErro("Erro ao enviar pergunta: " + e.getMessage());
        }
        
        return RedirectUtil.redirectToPathAsObject("/mensagens/leilao/" + leilaoId);
    }
    
    /**
     * Exibe o formulário para responder uma mensagem.
     * 
     * @param mensagemId ID da mensagem a ser respondida.
     * @return Template com o formulário de resposta.
     */
    @GET
    @Path("/responder/{id}")
    @RequiresAuth
    public Uni<Object> responder(@PathParam("id") Long mensagemId) {
        Mensagem mensagem = mensagemService.buscarPorId(mensagemId);
        if (mensagem == null) {
            flashErro("Mensagem não encontrada");
            return RedirectUtil.redirect(LeilaoController.class);
        }
        
        Usuario usuario = usuarioLogado();
        
        // Verifica permissões usando o serviço
        if (!mensagemService.podeResponder(usuario, mensagem)) {
            flashErro("Você não tem permissão para responder esta mensagem");
            return RedirectUtil.redirectToPathAsObject("/mensagens/leilao/" + mensagem.leilao.id);
        }
        
        return Uni.createFrom().item(Templates.responder(mensagem));
    }
    
    /**
     * Envia uma resposta a uma mensagem.
     * 
     * @param mensagemId ID da mensagem a ser respondida.
     * @param conteudo Conteúdo da resposta.
     * @return Redirecionamento para a página de mensagens do leilão.
     */
    @POST
    @Path("/responder")
    @Transactional
    @RequiresAuth
    public Uni<Object> enviarResposta(
            @FormParam("mensagemId") @NotNull Long mensagemId,
            @FormParam("conteudo") @NotBlank String conteudo) {
        
        Usuario usuario = usuarioLogado();
        
        Mensagem pergunta = mensagemService.buscarPorId(mensagemId);
        if (pergunta == null) {
            flashErro("Mensagem não encontrada");
            return RedirectUtil.redirectToPathAsObject("/leiloes");
        }
        
        if (validationFailed()) {
            flashErro("Por favor, preencha o conteúdo da resposta");
            return RedirectUtil.redirectToPathAsObject("/mensagens/responder/" + mensagemId);
        }
        
        // Verifica permissões usando o serviço
        if (!mensagemService.podeResponder(usuario, pergunta)) {
            flashErro("Você não tem permissão para responder esta mensagem");
            return RedirectUtil.redirectToPathAsObject("/mensagens/leilao/" + pergunta.leilao.id);
        }
        
        // Delega a criação da resposta para o serviço
        try {
            mensagemService.criarResposta(pergunta, usuario, conteudo);
            flashSucesso("Resposta enviada com sucesso!");
        } catch (Exception e) {
            flashErro("Erro ao enviar resposta: " + e.getMessage());
        }
        
        return RedirectUtil.redirectToPathAsObject("/mensagens/leilao/" + pergunta.leilao.id);
    }
}
