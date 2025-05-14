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
import jakarta.ws.rs.Produces;
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response cadastrar(
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
        
        try {
            // Validar dados obrigatórios
            if (razaoSocial == null || razaoSocial.trim().isEmpty() ||
                nomeFantasia == null || nomeFantasia.trim().isEmpty() ||
                cnpj == null || cnpj.trim().isEmpty() ||
                endereco == null || endereco.trim().isEmpty() ||
                cidade == null || cidade.trim().isEmpty() ||
                uf == null || uf.trim().isEmpty() ||
                cep == null || cep.trim().isEmpty() ||
                telefone == null || telefone.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                senha == null || senha.trim().isEmpty() ||
                tipoUsuario == null || tipoUsuario.trim().isEmpty()) {
                
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Todos os campos são obrigatórios")
                    .build();
            }
            
            // Validar formato do email
            if (!usuarioService.isEmailValido(email)) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Formato de email inválido")
                    .build();
            }
            
            // Verificar duplicidade de e-mail
            if (Usuario.encontrarPorEmail(email) != null) {
                return Response.status(Response.Status.CONFLICT)
                    .entity("O e-mail informado já está em uso")
                    .build();
            }
            
            // Verificar duplicidade de CNPJ
            if (Usuario.encontrarPorCnpj(cnpj) != null) {
                return Response.status(Response.Status.CONFLICT)
                    .entity("O CNPJ informado já está em uso")
                    .build();
            }
            
            // Validar tipo de usuário
            try {
                Usuario.TipoUsuario.valueOf(tipoUsuario);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Tipo de usuário inválido")
                    .build();
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
            usuario.senha = usuarioService.criptografarSenha(senha);
            usuario.tipoUsuario = Usuario.TipoUsuario.valueOf(tipoUsuario);
            usuario.dataCadastro = new Date();
            usuario.status = Usuario.Status.ATIVO;
            
            // Buscar e adicionar as áreas de atuação
            if (areaIds != null && !areaIds.isEmpty()) {
                for (Long areaId : areaIds) {
                    AreaAtuacao area = AreaAtuacao.findById(areaId);
                    if (area != null) {
                        usuario.areasAtuacao.add(area);
                    }
                }
            }
            
            usuario.persist();
            
            // Enviar email de boas vindas
            usuarioService.enviarEmailBoasVindas(usuario);
            
            return Response.status(Response.Status.CREATED)
                .entity("Usuário cadastrado com sucesso")
                .build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erro ao cadastrar usuário: " + e.getMessage())
                .build();
        }
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
    @Produces(MediaType.TEXT_HTML)
    public Uni<Object> autenticar(
            @FormParam("email") String email,
            @FormParam("senha") String senha) {
        
        try {
            if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
                flash("mensagem", "Por favor, preencha todos os campos");
                flash("tipo", "danger");
                return RedirectUtil.redirectToPathAsObject("/usuarios/login");
            }
            
            Usuario usuario = usuarioService.autenticar(email.trim(), senha);
            
            if (usuario == null) {
                flash("mensagem", "Email ou senha inválidos");
                flash("tipo", "danger");
                return RedirectUtil.redirectToPathAsObject("/usuarios/login");
            }
            
            // Registrar o usuário na sessão
            SessionUtil.setUsuarioLogado(request, usuario);
            
            // Registrar último acesso
            usuarioService.registrarAcesso(usuario);
            
            // Redirecionar para a página inicial
            flash("mensagem", "Login realizado com sucesso!");
            flash("tipo", "success");
            return RedirectUtil.redirectToPathAsObject("/");
                
        } catch (Exception e) {
            flash("mensagem", "Erro ao realizar login: " + e.getMessage());
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/login");
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
