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
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import model.Usuario;
import model.AreaAtuacao;
import model.Avaliacao;
import service.NotificacaoService;
import service.UsuarioService;

@Path("/usuarios")
public class UsuarioController extends BaseController {
    
    @Inject
    NotificacaoService notificacaoService;
    
    @Inject
    UsuarioService usuarioService;
    
    @CheckedTemplate(basePath = "Usuarios", requireTypeSafeExpressions = false)
    public static class Templates {
        public static native TemplateInstance cadastro();
        public static native TemplateInstance login();
        public static native TemplateInstance perfil(Usuario usuario, List<Avaliacao> avaliacoes);
        public static native TemplateInstance editar(Usuario usuario);
        public static native TemplateInstance listar(List<Usuario> usuarios);
    }
    
    // Página inicial para cadastro de usuário
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
            @FormParam("areasAtuacao") @NotBlank List<AreaAtuacao> areasAtuacao,
            @FormParam("tipoUsuario") @NotBlank String tipoUsuario) {
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, corrija os erros no formulário");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/cadastro");
        }
        
        // Validações específicas
        if (!ValidationUtil.isValidEmail(email)) {
            flash("mensagem", "Email inválido");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/cadastro");
        }
        
        if (!ValidationUtil.isValidCNPJ(cnpj)) {
            flash("mensagem", "CNPJ inválido");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/cadastro");
        }
        
        if (!ValidationUtil.isValidCEP(cep)) {
            flash("mensagem", "CEP inválido");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/cadastro");
        }
        
        // Verificar se já existe usuário com o mesmo CNPJ ou email
        if (Usuario.encontrarPorCnpj(cnpj) != null) {
            flash("mensagem", "Já existe um usuário cadastrado com este CNPJ");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/cadastro");
        }
        
        if (Usuario.encontrarPorEmail(email) != null) {
            flash("mensagem", "Já existe um usuário cadastrado com este email");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/cadastro");
        }
        
        // Criar novo usuário
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
        usuario.areasAtuacao = areasAtuacao;
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
    @Path("/login")
    public TemplateInstance login() {
        return Templates.login();
    }
    
    // Ação para processar o login
    @POST
    @Path("/autenticar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<Object> autenticar(
            @FormParam("email") @NotBlank String email,
            @FormParam("senha") @NotBlank String senha) {
        
        if (validationFailed()) {
            flash("mensagem", "Email e senha são obrigatórios");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/login");
        }
        
        Usuario usuario = Usuario.encontrarPorEmail(email);
        
        // Tenta realizar login via AutenticacaoService
        Usuario usuarioAutenticado = autenticacaoService.login(email, senha);
        
        if (usuarioAutenticado == null) {
            flash("mensagem", "Email ou senha incorretos");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/login");
        }
        
        // Verificar se o usuário está ativo
        if (usuarioAutenticado.status != Usuario.Status.ATIVO) {
            // Forçar logout caso o usuário tenha sido autenticado mas esteja inativo
            autenticacaoService.logout();
            
            flash("mensagem", "Sua conta está suspensa ou inativa");
            flash("tipo", "danger");
            return RedirectUtil.redirectToPathAsObject("/usuarios/login");
        }
        
        // O serviço de autenticação já salvou o ID do usuário e o tipo de usuário na sessão
        
        flash("mensagem", "Login realizado com sucesso!");
        flash("tipo", "success");
        
        // Redirecionar para a página adequada com base no tipo de usuário
        if (usuario.tipoUsuario == Usuario.TipoUsuario.ADMINISTRADOR) {
            return RedirectUtil.redirect(AdminController.class);
        } else if (usuario.tipoUsuario == Usuario.TipoUsuario.COMPRADOR) {
            return RedirectUtil.redirect(LeilaoController.class);
        } else {
            return RedirectUtil.redirectToPathAsObject("/leiloes/disponiveis");
        }
    }
    
    // Logout
    @Path("/logout")
    public Uni<Object> logout() {
        // Usando o serviço de autenticação para fazer logout
        autenticacaoService.logout();
        
        flash("mensagem", "Logout realizado com sucesso!");
        flash("tipo", "success");
        return RedirectUtil.redirectToPathAsObject("/");
    }
    
    // Visualizar perfil do usuário
    @Path("/perfil")
    public TemplateInstance perfil() {
        Usuario usuario = usuarioLogado();
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
            @FormParam("areaAtuacao") @NotBlank List<AreaAtuacao> areaAtuacao) {
        
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
        usuario.areasAtuacao = areaAtuacao;
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
