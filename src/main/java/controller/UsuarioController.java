package controller;

import java.util.Date;
import java.util.List;
import io.smallrye.mutiny.Uni;
import util.RedirectUtil;
import util.ValidationUtil;
import util.PasswordUtil;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.servlet.http.HttpServletRequest;
import model.Usuario;
import model.AreaAtuacao;
import model.Avaliacao;
import service.NotificacaoService;
import service.UsuarioService;
import util.SessionUtil;

@Path("/usuarios")
public class UsuarioController extends BaseController {
    
    @Inject
    NotificacaoService notificacaoService;
    
    @Inject
    UsuarioService usuarioService;
    
    @Inject
    HttpServletRequest request;
    
    @CheckedTemplate(basePath = "Usuarios")
    public static class Templates {
        public static native TemplateInstance cadastro();
        public static native TemplateInstance login();
        public static native TemplateInstance perfil(Usuario usuario, List<Avaliacao> avaliacoes);
        public static native TemplateInstance editar(Usuario usuario);
        public static native TemplateInstance listar(List<Usuario> usuarios);
    }
    
    // Página inicial para cadastro de usuário
    @GET
    @Path("/cadastro")
    public TemplateInstance cadastro() {
        return Templates.cadastro();
    }
    
    // Ação para processar o cadastro de um novo usuário
    @POST
    @Path("/cadastrar")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<Object> cadastrar(
            @FormParam("razaoSocial") @NotBlank String razaoSocial,
            @FormParam("nomeFantasia") @NotBlank String nomeFantasia,
            @FormParam("cnpj") @NotBlank String cnpj,
            @FormParam("endereco") @NotBlank String endereco,
            @FormParam("cidade") @NotBlank String cidade,
            @FormParam("uf") @NotBlank String uf,
            @FormParam("cep") @NotBlank String cep,
            @FormParam("telefone") @NotBlank String telefone,
            @FormParam("email") @NotBlank String email,
            @FormParam("senha") @NotBlank String senha,
            @FormParam("areasAtuacao") List<Long> areaIds,
            @FormParam("tipoUsuario") @NotBlank String tipoUsuario) {
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, corrija os erros no formulário");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/cadastro");
        }
        
        // Verificar duplicidade de e-mail
        if (Usuario.encontrarPorEmail(email) != null) {
            flash("mensagem", "O e-mail informado já está em uso");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/cadastro");
        }
        
        // Verificar duplicidade de CNPJ
        if (Usuario.encontrarPorCnpj(cnpj) != null) {
            flash("mensagem", "O CNPJ informado já está em uso");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/cadastro");
        }
        
        Usuario usuario = new Usuario();
        usuario.razaoSocial = razaoSocial;
        usuario.nomeFantasia = nomeFantasia;
        usuario.cnpj = cnpj;
        usuario.endereco = endereco;
        usuario.cidade = cidade;
        usuario.uf = uf;
        usuario.cep = cep;
        usuario.telefone = telefone;
        usuario.email = email;
        usuario.senha = PasswordUtil.hashPassword(senha); // Criptografando a senha
        
        // Buscar e adicionar as áreas de atuação
        if (areaIds != null && !areaIds.isEmpty()) {
            for (Long areaId : areaIds) {
                AreaAtuacao area = AreaAtuacao.findById(areaId);
                if (area != null) {
                    usuario.areasAtuacao.add(area);
                }
            }
        }
        
        usuario.tipoUsuario = Usuario.TipoUsuario.valueOf(tipoUsuario);
        usuario.dataCadastro = new Date();
        
        usuario.persist();
        
        // Enviar email de boas vindas
        usuarioService.enviarEmailBoasVindas(usuario);
        
        flash("mensagem", "Cadastro realizado com sucesso! Por favor, faça login.");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/usuarios/login");
    }
    
    // Página de login
    @GET
    @Path("/login")
    public TemplateInstance login() {
        return Templates.login();
    }
    
    // Ação para processar o login
    @POST
    @Path("/autenticar")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response autenticar(
            @FormParam("email") @NotBlank String email,
            @FormParam("senha") @NotBlank String senha) {
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, preencha todos os campos");
            flash("tipo", "danger");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        try {
            Usuario usuario = usuarioService.autenticar(email, senha);
            
            if (usuario == null) {
                flash("mensagem", "Email ou senha inválidos");
                flash("tipo", "danger");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            
            // Registrar o usuário na sessão usando SessionUtil
            SessionUtil.setUsuarioLogado(request, usuario);
            
            // Redirecionar para a página inicial
            return Response.ok().build();
        } catch (Exception e) {
            flash("mensagem", "Erro ao realizar login. Por favor, tente novamente.");
            flash("tipo", "danger");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Logout
    @Path("/logout")
    public Uni<Object> logout() {
        SessionUtil.logout(request);
        
        flash("mensagem", "Logout realizado com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/");
    }
    
    // Visualizar perfil do usuário
    @Path("/perfil")
    public TemplateInstance perfil() {
        Usuario usuario = SessionUtil.getUsuarioLogado(request, usuarioService);
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para acessar esta página");
            flash("tipo", "danger");
            return login();
        }
        
        List<Avaliacao> avaliacoes = Avaliacao.listarPorAvaliado(usuario);
        return Templates.perfil(usuario, avaliacoes);
    }
    
    // Visualizar perfil de outro usuário
    @Path("/perfil/{id}")
    public TemplateInstance perfilOutro(Long id) {
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            flash("mensagem", "Usuário não encontrado");
            flash("tipo", "danger");
            return login();
        }
        
        List<Avaliacao> avaliacoes = Avaliacao.listarPorAvaliado(usuario);
        return Templates.perfil(usuario, avaliacoes);
    }
    
    // Página para editar perfil
    @Path("/editar")
    public TemplateInstance editar() {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para acessar esta página");
            flash("tipo", "danger");
            return login();
        }
        
        return Templates.editar(usuario);
    }
    
    // Atualizar perfil
    @POST
    @Path("/atualizar")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<Object> atualizar(
            @FormParam("razaoSocial") @NotBlank String razaoSocial,
            @FormParam("nomeFantasia") @NotBlank String nomeFantasia,
            @FormParam("endereco") @NotBlank String endereco,
            @FormParam("cidade") @NotBlank String cidade,
            @FormParam("uf") @NotBlank String uf,
            @FormParam("cep") @NotBlank String cep,
            @FormParam("telefone") @NotBlank String telefone,
            @FormParam("areasAtuacao") List<Long> areaIds) {
        
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            flash("mensagem", "Você precisa estar logado para acessar esta página");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/login");
        }
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, corrija os erros no formulário");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/editar");
        }
        
        // Atualizar dados do usuário
        usuario.razaoSocial = razaoSocial;
        usuario.nomeFantasia = nomeFantasia;
        usuario.endereco = endereco;
        usuario.cidade = cidade;
        usuario.uf = uf;
        usuario.cep = cep;
        usuario.telefone = telefone;
        
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
        
        usuario.ultimaAtualizacao = new Date();
        
        usuario.persist();
        
        flash("mensagem", "Perfil atualizado com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/usuarios/perfil");
    }
    
    // Usando o método usuarioLogado() herdado de BaseController
    
    // Listar fornecedores (para compradores convidarem para leilões)
    @Path("/fornecedores")
    public TemplateInstance listarFornecedores() {
        Usuario usuario = usuarioLogado();
        if (usuario == null || usuario.tipoUsuario != Usuario.TipoUsuario.COMPRADOR) {
            flash("mensagem", "Você não tem permissão para acessar esta página");
            flash("tipo", "danger");
            return login();
        }
        
        List<Usuario> fornecedores = Usuario.listarFornecedoresAtivos();
        return Templates.listar(fornecedores);
    }
}
