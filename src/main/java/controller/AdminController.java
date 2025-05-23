package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import io.smallrye.mutiny.Uni;

import model.AreaAtuacao;
import model.FormaPagamento;
import model.Leilao;
import model.Usuario;
import service.NotificacaoService;
import util.RedirectUtil;
import security.RequiresAuth;
import security.RequiresRole;

@Path("/admin")
@Produces(MediaType.TEXT_HTML)
@RequiresAuth
@RequiresRole(Usuario.TipoUsuario.ADMINISTRADOR)
public class AdminController extends BaseController {
    
    @Inject
    NotificacaoService notificacaoService;
    
    @CheckedTemplate(basePath = "Admin", requireTypeSafeExpressions = false)
    public static class Templates {
        public static native TemplateInstance dashboard(Map<String, Object> stats);
        public static native TemplateInstance usuarios(List<Usuario> usuarios);
        public static native TemplateInstance editarUsuario(Usuario usuario, List<AreaAtuacao> areasAtuacao);
        public static native TemplateInstance areas(List<AreaAtuacao> areas);
        public static native TemplateInstance formasPagamento(List<FormaPagamento> formasPagamento);
        public static native TemplateInstance leiloes(List<Leilao> leiloes);
    }
    
    // Dashboard admin
    @Path("/dashboard")
    public TemplateInstance dashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsuarios", Usuario.count());
        stats.put("totalLeiloes", Leilao.count());
        stats.put("leiloesAtivos", Leilao.count("status", Leilao.Status.ABERTO));
        stats.put("leiloesConcluidos", Leilao.count("status", Leilao.Status.CONCLUIDO));
        
        return Templates.dashboard(stats);
    }
    
    // Gerenciamento de usuários
    @Path("/usuarios")
    public TemplateInstance usuarios() {
        List<Usuario> usuarios = Usuario.listAll();
        return Templates.usuarios(usuarios);
    }
    
    @Path("/usuarios/editar/{id}")
    public TemplateInstance editarUsuario(@PathParam("id") Long id) {
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            flash("mensagem", "Usuário não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectTemplate("/admin/usuarios");
        }
        
        List<AreaAtuacao> areasDisponiveis = AreaAtuacao.listAll();
        return Templates.editarUsuario(usuario, areasDisponiveis);
    }
    
    @POST
    @Path("/usuarios/salvar/{id}")
    @Transactional
    public Uni<Object> salvarUsuario(@PathParam("id") Long id,
                                  @FormParam("nome") @NotBlank String nome,
                                  @FormParam("email") @NotBlank String email,
                                  @FormParam("razaoSocial") String razaoSocial,
                                  @FormParam("nomeFantasia") String nomeFantasia,
                                  @FormParam("cnpj") String cnpj,
                                  @FormParam("endereco") String endereco,
                                  @FormParam("cidade") String cidade,
                                  @FormParam("uf") String uf,
                                  @FormParam("cep") String cep,
                                  @FormParam("telefone") String telefone,
                                  @FormParam("tipoUsuario") @NotNull Usuario.TipoUsuario tipoUsuario,
                                  @FormParam("status") @NotNull Usuario.Status status,
                                  @FormParam("areaIds") List<Long> areaIds) {
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, corrija os erros no formulário");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/admin/usuarios/editar/" + id);
        }
        
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            flash("mensagem", "Usuário não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/admin/usuarios");
        }
        
        // Verificar duplicidade de e-mail
        Usuario existente = Usuario.encontrarPorEmail(email);
        if (existente != null && !existente.id.equals(id)) {
            flash("mensagem", "O e-mail informado já está em uso");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/admin/usuarios/editar/" + id);
        }
        
        // Atualizar os dados do usuário
        usuario.nomeFantasia = nome;
        usuario.email = email;
        usuario.razaoSocial = razaoSocial;
        usuario.nomeFantasia = nomeFantasia;
        usuario.cnpj = cnpj;
        usuario.endereco = endereco;
        usuario.cidade = cidade;
        usuario.uf = uf;
        usuario.cep = cep;
        usuario.telefone = telefone;
        usuario.tipoUsuario = tipoUsuario;
        usuario.status = status;
        
        // Atualizar áreas de atuação
        usuario.areasAtuacao.clear();
        if (areaIds != null && !areaIds.isEmpty()) {
            for (Long areaId : areaIds) {
                AreaAtuacao area = AreaAtuacao.findById(areaId);
                if (area != null) {
                    usuario.areasAtuacao.add(area);
                }
            }
        }
        
        usuario.persist();
        
        flash("mensagem", "Usuário atualizado com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/admin/usuarios");
    }
    
    @POST
    @Path("/usuarios/resetar-senha/{id}")
    @Transactional
    public Uni<Object> resetarSenha(@PathParam("id") Long id) {
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            flash("mensagem", "Usuário não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/admin/usuarios");
        }
        
        // Resetar a senha para "123456"
        usuario.senha = "123456";
        usuario.persist();
        
        flash("mensagem", "Senha resetada com sucesso para '123456'");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/admin/usuarios");
    }
    
    @POST
    @Path("/usuarios/ativar/{id}")
    @Transactional
    public Uni<Object> ativarUsuario(@PathParam("id") Long id) {
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            flash("mensagem", "Usuário não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/admin/usuarios");
        }
        
        usuario.status = Usuario.Status.ATIVO;
        usuario.persist();
        
        flash("mensagem", "Usuário ativado com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/admin/usuarios");
    }
    
    @POST
    @Path("/usuarios/suspender/{id}")
    @Transactional
    public Uni<Object> suspenderUsuario(@PathParam("id") Long id) {
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            flash("mensagem", "Usuário não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/admin/usuarios");
        }
        
        usuario.status = Usuario.Status.SUSPENSO;
        usuario.persist();
        
        flash("mensagem", "Usuário suspenso com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/admin/usuarios");
    }
    
    // Gerenciamento de áreas de atuação
    @Path("/areas")
    public TemplateInstance areas() {
        List<AreaAtuacao> areas = AreaAtuacao.listAll();
        return Templates.areas(areas);
    }
    
    @POST
    @Path("/areas/salvar")
    @Transactional
    public Uni<Object> salvarArea(
            @FormParam("id") Long id,
            @FormParam("descricao") @NotBlank String descricao,
            @FormParam("detalhes") String detalhes) {
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, informe o nome da área");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/admin/areas");
        }
        
        AreaAtuacao area;
        if (id != null) {
            area = AreaAtuacao.findById(id);
            if (area == null) {
                flash("mensagem", "Área não encontrada");
                flash("tipo", "danger");
                return RedirectUtil.redirectToPathAsObject("/admin/areas");
            }
        } else {
            area = new AreaAtuacao();
        }
        
        area.descricao = descricao;
        area.detalhes = detalhes;
        area.persist();
        
        flash("mensagem", "Área salva com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/admin/areas");
    }
    
    @POST
    @Path("/areas/excluir")
    @Transactional
    public Uni<Object> excluirArea(@FormParam("id") Long id) {
        AreaAtuacao area = AreaAtuacao.findById(id);
        if (area == null) {
            flash("mensagem", "Área não encontrada");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/admin/areas");
        }
        
        // Verificar se a área está em uso
        if (area.fornecedores != null && !area.fornecedores.isEmpty()) {
            flash("mensagem", "Essa área está sendo utilizada por usuários e não pode ser excluída");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/admin/areas");
        }
        
        area.delete();
        
        flash("mensagem", "Área excluída com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/admin/areas");
    }
    
    // Gerenciamento de formas de pagamento
    @Path("/formas-pagamento")
    public TemplateInstance formasPagamento() {
        List<FormaPagamento> formasPagamento = FormaPagamento.listAll();
        return Templates.formasPagamento(formasPagamento);
    }
    
    @POST
    @Path("/formas-pagamento/salvar")
    @Transactional
    public Uni<Object> salvarFormaPagamento(
            @FormParam("id") Long id,
            @FormParam("nome") @NotBlank String nome,
            @FormParam("descricao") String descricao) {
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, informe o nome da forma de pagamento");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/admin/formas-pagamento");
        }
        
        FormaPagamento forma;
        if (id != null) {
            forma = FormaPagamento.findById(id);
            if (forma == null) {
                flash("mensagem", "Forma de pagamento não encontrada");
                flash("tipo", "danger");
                return RedirectUtil.redirectToPathAsObject("/admin/formas-pagamento");
            }
        } else {
            forma = new FormaPagamento();
        }
        
        forma.descricao = descricao;
        forma.persist();
        
        flash("mensagem", "Forma de pagamento salva com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/admin/formas-pagamento");
    }
    
    @POST
    @Path("/formas-pagamento/excluir")
    @Transactional
    public Uni<Object> excluirFormaPagamento(@FormParam("id") Long id) {
        FormaPagamento forma = FormaPagamento.findById(id);
        if (forma == null) {
            flash("mensagem", "Forma de pagamento não encontrada");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/admin/formas-pagamento");
        }
        
        // Verificar se a forma de pagamento está em uso
        if (forma.leiloes != null && !forma.leiloes.isEmpty()) {
            flash("mensagem", "Essa forma de pagamento está sendo utilizada por leilões e não pode ser excluída");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/admin/formas-pagamento");
        }
        
        forma.delete();
        
        flash("mensagem", "Forma de pagamento excluída com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/admin/formas-pagamento");
    }
    
    // Gerenciamento de leilões
    @Path("/leiloes")
    public TemplateInstance leiloes() {
        List<Leilao> leiloes = Leilao.listAll();
        return Templates.leiloes(leiloes);
    }
}
