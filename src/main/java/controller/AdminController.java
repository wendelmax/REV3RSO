package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.quarkiverse.renarde.Controller;
import io.quarkiverse.renarde.router.Router;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import util.RedirectUtil;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import io.smallrye.mutiny.Uni;

import model.AreaAtuacao;
import model.FormaPagamento;
import model.Leilao;
import model.Usuario;

@Path("/admin")
@RolesAllowed("ADMINISTRADOR")
public class AdminController extends Controller {
    
    @CheckedTemplate
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
        verificarAdministrador();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsuarios", Usuario.count());
        stats.put("totalLeiloes", Leilao.count());
        stats.put("leiloesAtivos", Leilao.count("status", Leilao.Status.ABERTO));
        stats.put("leiloesConcluidos", Leilao.count("status", Leilao.Status.CONCLUIDO));
        
        return Templates.dashboard(stats);
    }
    
    // Gerenciamento de usuu00e1rios
    @Path("/usuarios")
    public TemplateInstance usuarios() {
        verificarAdministrador();
        List<Usuario> usuarios = Usuario.listAll();
        return Templates.usuarios(usuarios);
    }
    
    @Path("/usuarios/editar/{id}")
    public TemplateInstance editarUsuario(@PathParam("id") Long id) {
        verificarAdministrador();
        
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            flash("mensagem", "Usuário não encontrado");
            flash("tipo", "danger");
            return redirect("/admin/usuarios");
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
        
        verificarAdministrador();
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, corrija os erros no formulário");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/admin/usuarios/editar/" + id);
        }
        
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            flash("mensagem", "Usuu00e1rio não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/admin/usuarios");
        }
        
        // Verificar duplicidade de e-mail
        Usuario existente = Usuario.encontrarPorEmail(email);
        if (existente != null && !existente.id.equals(id)) {
            flash("mensagem", "O e-mail informado ju00e1 estu00e1 em uso");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/admin/usuarios/editar/" + id);
        }
        
        // Atualizar os dados do usuu00e1rio
        usuario.nome = nome;
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
        
        // Atualizar u00e1reas de atuação
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
        
        flash("mensagem", "Usuu00e1rio atualizado com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPath("/admin/usuarios");
    }
    
    @POST
    @Path("/usuarios/resetar-senha/{id}")
    @Transactional
    public Uni<Object> resetarSenha(@PathParam("id") Long id) {
        verificarAdministrador();
        
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            flash("mensagem", "Usuário não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/admin/usuarios");
        }
        
        // Resetar a senha para "123456"
        usuario.senha = "123456";
        usuario.persist();
        
        flash("mensagem", "Senha resetada com sucesso para '123456'");
        flash("tipo", "success");
        return RedirectUtil.redirectToPath("/admin/usuarios");
    }
    
    @POST
    @Path("/usuarios/ativar/{id}")
    @Transactional
    public Uni<Object> ativarUsuario(@PathParam("id") Long id) {
        verificarAdministrador();
        
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            flash("mensagem", "Usuário não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/admin/usuarios");
        }
        
        usuario.status = Usuario.Status.ATIVO;
        usuario.persist();
        
        flash("mensagem", "Usuário ativado com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPath("/admin/usuarios");
    }
    
    @POST
    @Path("/usuarios/suspender/{id}")
    @Transactional
    public Uni<Object> suspenderUsuario(@PathParam("id") Long id) {
        verificarAdministrador();
        
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            flash("mensagem", "Usuário não encontrado");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/admin/usuarios");
        }
        
        usuario.status = Usuario.Status.SUSPENSO;
        usuario.persist();
        
        flash("mensagem", "Usuário suspenso com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPath("/admin/usuarios");
    }
    
    // Gerenciamento de u00e1reas de atuação
    @Path("/areas")
    public TemplateInstance areas() {
        verificarAdministrador();
        List<AreaAtuacao> areas = AreaAtuacao.listAll();
        return Templates.areas(areas);
    }
    
    @POST
    @Path("/areas/salvar")
    @Transactional
    public Uni<Object> salvarArea(
            @FormParam("id") Long id,
            @FormParam("nome") @NotBlank String nome,
            @FormParam("descricao") String descricao) {
        
        verificarAdministrador();
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, informe o nome da área");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/admin/areas");
        }
        
        AreaAtuacao area;
        if (id != null) {
            area = AreaAtuacao.findById(id);
            if (area == null) {
                flash("mensagem", "Área não encontrada");
                flash("tipo", "danger");
                return RedirectUtil.redirectToPath("/admin/areas");
            }
        } else {
            area = new AreaAtuacao();
        }
        
        area.nome = nome;
        area.descricao = descricao;
        area.persist();
        
        flash("mensagem", "Área salva com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPath("/admin/areas");
    }
    
    @POST
    @Path("/areas/excluir")
    @Transactional
    public Uni<Object> excluirArea(@FormParam("id") Long id) {
        verificarAdministrador();
        
        AreaAtuacao area = AreaAtuacao.findById(id);
        if (area == null) {
            flash("mensagem", "Área não encontrada");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/admin/areas");
        }
        
        // Verificar se a área está em uso
        if (area.usuarios != null && !area.usuarios.isEmpty()) {
            flash("mensagem", "Essa área está sendo utilizada por usuários e não pode ser excluída");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/admin/areas");
        }
        
        area.delete();
        
        flash("mensagem", "Área excluída com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPath("/admin/areas");
    }
    
    // Gerenciamento de formas de pagamento
    @Path("/formas-pagamento")
    public TemplateInstance formasPagamento() {
        verificarAdministrador();
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
        
        verificarAdministrador();
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, informe o nome da forma de pagamento");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/admin/formas-pagamento");
        }
        
        FormaPagamento forma;
        if (id != null) {
            forma = FormaPagamento.findById(id);
            if (forma == null) {
                flash("mensagem", "Forma de pagamento não encontrada");
                flash("tipo", "danger");
                return RedirectUtil.redirectToPath("/admin/formas-pagamento");
            }
        } else {
            forma = new FormaPagamento();
        }
        
        forma.nome = nome;
        forma.descricao = descricao;
        forma.persist();
        
        flash("mensagem", "Forma de pagamento salva com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPath("/admin/formas-pagamento");
    }
    
    @POST
    @Path("/formas-pagamento/excluir")
    @Transactional
    public Uni<Object> excluirFormaPagamento(@FormParam("id") Long id) {
        verificarAdministrador();
        
        FormaPagamento forma = FormaPagamento.findById(id);
        if (forma == null) {
            flash("mensagem", "Forma de pagamento não encontrada");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/admin/formas-pagamento");
        }
        
        // Verificar se a forma de pagamento está em uso
        if (forma.leiloes != null && !forma.leiloes.isEmpty()) {
            flash("mensagem", "Essa forma de pagamento está sendo utilizada por leilões e não pode ser excluída");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPath("/admin/formas-pagamento");
        }
        
        forma.delete();
        
        flash("mensagem", "Forma de pagamento excluída com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPath("/admin/formas-pagamento");
    }
    
    // Mu00e9todo auxiliar para verificar se o usuu00e1rio u00e9 administrador
    private void verificarAdministrador() {
        Usuario usuario = usuarioLogado();
        if (usuario == null || usuario.tipoUsuario != Usuario.TipoUsuario.ADMINISTRADOR) {
            flash("mensagem", "Você não tem permissão para acessar essa u00e1rea");
            flash("tipo", "danger");
            // não retorna valor, apenas exibe a mensagem
        }
    }
    
    // Mu00e9todo auxiliar para obter o usuu00e1rio logado
    protected Usuario usuarioLogado() {
        // Usando a API correta para obter valores da sessu00e3o no Renarde
        Long usuarioId = request().session().getAttribute("usuarioId") != null ? 
                      Long.valueOf(request().session().getAttribute("usuarioId").toString()) : null;
        if (usuarioId == null) {
            return null;
        }
        return Usuario.findById(usuarioId);
    }
}
