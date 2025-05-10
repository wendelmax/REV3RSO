package controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.smallrye.mutiny.Uni;


import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import util.RedirectUtil;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import io.quarkus.panache.common.Sort;

import model.Leilao;
import model.Usuario;
import model.Lance;
import service.NotificacaoService;
import security.RequiresAuth;
import security.RequiresRole;

@Path("/lances")
@Produces(MediaType.TEXT_HTML)
public class LanceController extends BaseController {
    
    @Inject
    NotificacaoService notificacaoService;
    
    @CheckedTemplate(basePath = "Lance", requireTypeSafeExpressions = false)
    public static class Templates {
        public static native TemplateInstance historico(Leilao leilao);
        public static native TemplateInstance listar(Leilao leilao);
    }
    
    // Ação para dar um novo lance
    @POST
    @Path("/dar")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @RequiresAuth
    @RequiresRole(Usuario.TipoUsuario.FORNECEDOR)
    public Uni<String> darLance(
            @FormParam("leilaoId") @NotNull Long leilaoId,
            @FormParam("valor") @NotBlank String valorStr,
            @FormParam("condicoesEntrega") String condicoesEntrega,
            @FormParam("prazoEntrega") Integer prazoEntrega,
            @FormParam("prazoPagamento") Integer prazoPagamento) {
        
        Leilao leilao = Leilao.findById(leilaoId);
        if (leilao == null) {
            flash("mensagem", "Leilão não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/leiloes");
        }
        
        Usuario usuario = usuarioLogado();
        if (usuario == null || usuario.tipoUsuario != Usuario.TipoUsuario.FORNECEDOR) {
            flash("mensagem", "Você não tem permissão para dar lances");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/leiloes/" + leilaoId);
        }
        
        // Verificar se o fornecedor pode participar deste leilão
        boolean podeParticipar = leilao.status == Leilao.Status.ABERTO &&
                                (leilao.tipoLeilao == Leilao.TipoLeilao.ABERTO || 
                                 leilao.isConvidado(usuario));
        
        if (!podeParticipar) {
            flash("mensagem", "Você não pode participar deste leilão");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/leiloes/" + leilaoId);
        }
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, corrija os erros no formulário");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/leiloes/" + leilaoId);
        }
        
        BigDecimal valor;
        try {
            // Converter o valor para BigDecimal, substituindo vírgula por ponto
            valor = new BigDecimal(valorStr.replace(",", "."));
        } catch (NumberFormatException e) {
            flash("mensagem", "Valor inválido");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/leiloes/" + leilaoId);
        }
        
        // Verificar se o valor é maior que zero
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            flash("mensagem", "O valor do lance deve ser maior que zero");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/leiloes/" + leilaoId);
        }
        
        // Obter o menor lance atual, se houver
        Lance menorLanceAtual = Lance.menorLanceDoLeilao(leilao);
        
        // Verificar se o novo lance é menor que o atual menor lance
        if (menorLanceAtual != null && valor.compareTo(menorLanceAtual.valor) >= 0) {
            flash("mensagem", "Seu lance deve ser menor que o lance atual de " + menorLanceAtual.valor);
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/leiloes/" + leilaoId);
        }
        
        // Criar o novo lance
        Lance lance = new Lance();
        lance.leilao = leilao;
        lance.fornecedor = usuario;
        lance.valor = valor;
        lance.condicoesEntrega = condicoesEntrega;
        lance.prazoEntrega = prazoEntrega;
        lance.prazoPagamento = prazoPagamento;
        lance.dataCriacao = new Date();
        
        lance.persist();
        
        // Notificar o comprador sobre o novo lance
        notificacaoService.notificarNovoLance(lance);
        
        flash("mensagem", "Lance registrado com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPath("/leiloes/" + leilaoId);
    }
    
    // Visualizar histórico de lances de um leilão
    @Path("/historico/{leilaoId}")
    @RequiresAuth
    public TemplateInstance historico(@PathParam("leilaoId") Long leilaoId) {
        Leilao leilao = Leilao.findById(leilaoId);
        if (leilao == null) {
            flash("mensagem", "Leilão não encontrado");
            flash("tipo", "danger");
            return null;
        }
        
        Usuario usuario = usuarioLogado();
        
        // Verificar se o usuário pode ver os lances
        if (!usuario.equals(leilao.criador) && 
            (leilao.tipoLeilao == Leilao.TipoLeilao.FECHADO && !leilao.isConvidado(usuario))) {
            flash("mensagem", "Você não tem permissão para ver os lances deste leilão");
            flash("tipo", "danger");
            return null;
        }
        
        return Templates.historico(leilao);
    }
    
    // Registrar um novo lance
    @POST
    @Path("/registrar/{leilaoId}")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @RequiresAuth
    @RequiresRole(Usuario.TipoUsuario.FORNECEDOR)
    public void registrar(
            @PathParam("leilaoId") Long leilaoId,
            @FormParam("valor") @NotNull(message = "O valor do lance é obrigatório") String valorStr) {
        
        Leilao leilao = Leilao.findById(leilaoId);
        if (leilao == null) {
            flash("mensagem", "Leilão não encontrado");
            flash("tipo", "danger");
            return;
        }
        
        Usuario fornecedor = usuarioLogado();
        
        // Verificar se o fornecedor pode participar do leilão
        if (leilao.tipoLeilao == Leilao.TipoLeilao.FECHADO && !leilao.isConvidado(fornecedor)) {
            flash("mensagem", "Você não está convidado para participar deste leilão");
            flash("tipo", "danger");
            return;
        }
        
        // Verificar se o leilão está aberto
        if (leilao.status != Leilao.Status.ABERTO) {
            flash("mensagem", "Este leilão não está aberto para lances");
            flash("tipo", "danger");
            return;
        }
        
        // Converter o valor do lance
        BigDecimal valor;
        try {
            valor = new BigDecimal(valorStr.replace(",", "."));
        } catch (NumberFormatException e) {
            flash("mensagem", "Valor do lance inválido");
            flash("tipo", "danger");
            return;
        }
        
        // Verificar se o valor é válido
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            flash("mensagem", "O valor do lance deve ser maior que zero");
            flash("tipo", "danger");
            return;
        }
        
        // Verificar se o valor é menor que o lance anterior
        Lance ultimoLance = Lance.find("leilao = ?1 order by dataHora desc", leilao).firstResult();
        if (ultimoLance != null && valor.compareTo(ultimoLance.valor) >= 0) {
            flash("mensagem", "O valor do lance deve ser menor que o lance anterior");
            flash("tipo", "danger");
            return;
        }
        
        // Criar e salvar o lance
        Lance lance = new Lance();
        lance.leilao = leilao;
        lance.fornecedor = fornecedor;
        lance.valor = valor;
        lance.persist();
        
        // Notificar o comprador sobre o novo lance
        notificacaoService.notificarNovoLance(lance);
        
        flash("mensagem", "Lance registrado com sucesso!");
        flash("tipo", "success");
    }
    
    // Listar lances de um leilão
    @Path("/listar/{leilaoId}")
    @RequiresAuth
    public TemplateInstance listar(@PathParam("leilaoId") Long leilaoId) {
        Leilao leilao = Leilao.findById(leilaoId);
        if (leilao == null) {
            flash("mensagem", "Leilão não encontrado");
            flash("tipo", "danger");
            return null;
        }
        
        Usuario usuario = usuarioLogado();
        
        // Verificar se o usuário pode ver os lances
        if (!usuario.equals(leilao.criador) && 
            (leilao.tipoLeilao == Leilao.TipoLeilao.FECHADO && !leilao.isConvidado(usuario))) {
            flash("mensagem", "Você não tem permissão para ver os lances deste leilão");
            flash("tipo", "danger");
            return null;
        }
        
        return Templates.listar(leilao);
    }
}
