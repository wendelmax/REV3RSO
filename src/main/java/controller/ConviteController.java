package controller;

import java.util.Date;
import java.util.List;


import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;

import model.Usuario;
import model.Leilao;
import model.Convite;
import service.NotificacaoService;
import util.RedirectUtil;

@Path("/convites")
public class ConviteController extends BaseController {
    
    @Inject
    NotificacaoService notificacaoService;
    
    
    @CheckedTemplate(basePath = "Convite", requireTypeSafeExpressions = false)
    public static class Templates {
        public static native TemplateInstance listar(Leilao leilao, List<Convite> convites);
        public static native TemplateInstance selecionar(Leilao leilao, List<Usuario> fornecedores);
        public static native TemplateInstance recebidos(List<Convite> convites);
    }
    
    // Exibir formulário para selecionar fornecedores a serem convidados
    @Path("/selecionar/{leilaoId}")
    public Uni<TemplateInstance> selecionar(@PathParam("leilaoId") Long leilaoId) {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para acessar esta página");
            flash("tipo", "danger");
            return RedirectUtil.redirectToTemplate("/usuarios/login");
        }
        
        Leilao leilao = Leilao.findById(leilaoId);
        if (leilao == null) {
            flash("mensagem", "Leilão não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToTemplate("/leiloes");
        }
        
        // Apenas o criador do leilão pode convidar fornecedores
        if (!usuario.equals(leilao.criador)) {
            flash("mensagem", "Você não tem permissão para convidar fornecedores");
            flash("tipo", "danger");
            return RedirectUtil.redirectToTemplate("/leiloes/" + leilaoId);
        }
        
        // Apenas leilões fechados podem receber convites
        if (leilao.tipoLeilao != Leilao.TipoLeilao.FECHADO) {
            flash("mensagem", "Apenas leilões fechados podem receber convites");
            flash("tipo", "danger");
            return RedirectUtil.redirectToTemplate("/leiloes/" + leilaoId);
        }
        
        // Obter lista de fornecedores ativos para convite
        List<Usuario> fornecedores = Usuario.listarFornecedoresAtivos();
        return Uni.createFrom().item(Templates.selecionar(leilao, fornecedores));
    }
    
    // Enviar convites para os fornecedores selecionados
    @POST
    @Path("/enviar")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<Object> enviarConvites(
            @FormParam("leilaoId") @NotNull Long leilaoId,
            @FormParam("fornecedorIds") @NotEmpty List<Long> fornecedorIds) {
        
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
        
        // Apenas o criador do leilão pode convidar fornecedores
        if (!usuario.equals(leilao.criador)) {
            flash("mensagem", "Você não tem permissão para convidar fornecedores");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/leiloes/" + leilaoId);
        }
        
        // Apenas leilões fechados podem receber convites
        if (leilao.tipoLeilao != Leilao.TipoLeilao.FECHADO) {
            flash("mensagem", "Apenas leilões fechados podem receber convites");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/leiloes/" + leilaoId);
        }
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, selecione pelo menos um fornecedor");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/convites/selecionar/" + leilaoId);
        }
        
        int convitesEnviados = 0;
        
        // Processar cada fornecedor selecionado
        for (Long fornecedorId : fornecedorIds) {
            Usuario fornecedor = Usuario.findById(fornecedorId);
            if (fornecedor != null && fornecedor.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR) {
                
                // Verificar se já existe convite para este fornecedor neste leilão
                if (!Convite.existeConvite(leilao, fornecedor)) {
                    // Criar novo convite
                    Convite convite = new Convite();
                    convite.leilao = leilao;
                    convite.fornecedor = fornecedor;
                    convite.dataEnvio = new Date();
                    convite.status = Convite.Status.PENDENTE;
                    
                    convite.persist();
                    convitesEnviados++;
                    
                    // Enviar notificação por email
                    notificacaoService.notificarConvite(convite);
                }
            }
        }
        
        flash("mensagem", convitesEnviados + " convites enviados com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/convites/listar/" + leilaoId);
    }
    
    // Listar todos os convites de um leilão
    @Path("/listar/{leilaoId}")
    public Uni<Object> listar(@PathParam("leilaoId") Long leilaoId) {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para acessar esta página");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/login");
        }
        
        Leilao leilao = Leilao.findById(leilaoId);
        if (leilao == null) {
            flash("mensagem", "Leilão não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/leiloes");
        }
        
        // Apenas o criador do leilão pode ver a lista de convites
        if (!usuario.equals(leilao.criador)) {
            flash("mensagem", "Você não tem permissão para acessar esta página");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/leiloes/" + leilaoId);
        }
        
        List<Convite> convites = Convite.listarPorLeilao(leilao);
        return Uni.createFrom().item(Templates.listar(leilao, convites));
    }
    
    // Listar convites recebidos pelo usuário logado
    @Path("/recebidos")
    public Uni<Object> convitesRecebidos() {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para acessar esta página");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/login");
        }
        
        if (usuario.tipoUsuario != Usuario.TipoUsuario.FORNECEDOR) {
            flash("mensagem", "Apenas fornecedores podem receber convites");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/");
        }
        
        List<Convite> convites = Convite.listarPorFornecedor(usuario);
        return Uni.createFrom().item(Templates.recebidos(convites));
    }
    
    // Aceitar um convite
    @Path("/aceitar/{id}")
    @Transactional
    public Uni<Object> aceitarConvite(@PathParam("id") Long conviteId) {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para realizar esta ação");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/login");
        }
        
        Convite convite = Convite.findById(conviteId);
        if (convite == null) {
            flash("mensagem", "Convite não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/convites/recebidos");
        }
        
        // Verificar se o convite pertence ao usuário logado
        if (!usuario.equals(convite.fornecedor)) {
            flash("mensagem", "Este convite não pertence a você");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/convites/recebidos");
        }
        
        // Verificar se o convite está pendente
        if (convite.status != Convite.Status.PENDENTE) {
            flash("mensagem", "Este convite já foi " + 
                 (convite.status == Convite.Status.ACEITO ? "aceito" : "recusado"));
            flash("tipo", "warning");
            return RedirectUtil.redirectToPathAsObject("/convites/recebidos");
        }
        
        // Atualizar status do convite
        convite.status = Convite.Status.ACEITO;
        convite.dataResposta = new Date();
        convite.persist();
        
        // Notificar o comprador
        notificacaoService.notificarConviteAceito(convite);
        
        flash("mensagem", "Convite aceito com sucesso! Você pode participar do leilão agora.");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/leiloes/" + convite.leilao.id);
    }
    
    // Recusar um convite
    @Path("/recusar/{id}")
    @Transactional
    public Uni<Object> recusarConvite(@PathParam("id") Long conviteId) {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para realizar esta ação");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/login");
        }
        
        Convite convite = Convite.findById(conviteId);
        if (convite == null) {
            flash("mensagem", "Convite não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/convites/recebidos");
        }
        
        // Verificar se o convite pertence ao usuário logado
        if (!usuario.equals(convite.fornecedor)) {
            flash("mensagem", "Este convite não pertence a você");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/convites/recebidos");
        }
        
        // Verificar se o convite está pendente
        if (convite.status != Convite.Status.PENDENTE) {
            flash("mensagem", "Este convite já foi " + 
                 (convite.status == Convite.Status.ACEITO ? "aceito" : "recusado"));
            flash("tipo", "warning");
            return RedirectUtil.redirectToPathAsObject("/convites/recebidos");
        }
        
        // Atualizar status do convite
        convite.status = Convite.Status.RECUSADO;
        convite.dataResposta = new Date();
        convite.persist();
        
        // Notificar o comprador
        notificacaoService.notificarConviteRecusado(convite);
        
        flash("mensagem", "Convite recusado com sucesso.");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/convites/recebidos");
    }
    
    // Usando o método usuarioLogado() herdado de BaseController
}
