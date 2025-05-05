package service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import model.TokenRecuperacao;
import model.Usuario;
import util.ExceptionUtil;
import exception.BusinessException;

/**
 * Serviço responsável pelas operações relacionadas aos usuários do sistema.
 */
@ApplicationScoped
public class UsuarioService {
    
    private static final Logger LOGGER = Logger.getLogger(UsuarioService.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Inject
    EmailService emailService;

    @Inject
    NotificacaoService notificacaoService;
    
    /**
     * Concluir o processo de recuperação de senha, definindo uma nova senha.
     * 
     * @param token Token de recuperação
     * @param novaSenha Nova senha
     * @return true se a senha foi alterada com sucesso, false caso contrário
     */
    @Transactional
    public boolean confirmarRecuperacaoSenha(String token, String novaSenha) {
        try {
            if (token == null || token.isEmpty()) {
                throw new BusinessException("Token inválido");
            }
            
            // Validar nova senha
            if (novaSenha.length() < 6) {
                throw new BusinessException("A nova senha deve ter no mínimo 6 caracteres");
            }
            
            // Buscar token
            TokenRecuperacao tokenRecuperacao = TokenRecuperacao.find("token", token).firstResult();
            
            if (tokenRecuperacao == null) {
                throw new BusinessException("Token inválido ou expirado");
            }
            
            // Verificar validade
            if (tokenRecuperacao.utilizado) {
                throw new BusinessException("Este token já foi utilizado");
            }
            
            if (tokenRecuperacao.dataExpiracao.before(new Date())) {
                throw new BusinessException("Este token expirou");
            }
            
            // Atualizar senha
            Usuario usuario = tokenRecuperacao.usuario;
            usuario.senha = criptografarSenha(novaSenha);
            usuario.persist();
            
            // Marcar token como utilizado
            tokenRecuperacao.utilizado = true;
            tokenRecuperacao.dataUtilizacao = new Date();
            tokenRecuperacao.persist();
            
            // Enviar email de confirmação
            enviarEmailConfirmacaoRecuperacaoSenha(usuario);
            
            LOGGER.info("Recuperação de senha concluída para o usuário ID: " + usuario.id);
            return true;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao confirmar recuperação de senha");
            return false;
        }
    }
    
    /**
     * Autenticar usuário com email e senha.
     * 
     * @param email Email do usuário
     * @param senha Senha
     * @return Usuário autenticado ou null se as credenciais forem inválidas
     */
    public Usuario autenticar(String email, String senha) {
        try {
            if (email == null || email.isEmpty() || senha == null || senha.isEmpty()) {
                return null;
            }
            
            Usuario usuario = buscarPorEmail(email);
            
            if (usuario == null || usuario.status != model.Usuario.Status.ATIVO) {
                return null;
            }
            
            if (!verificarSenha(senha, usuario.senha)) {
                return null;
            }
            
            // Registrar último acesso
            registrarAcesso(usuario);
            
            return usuario;
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao autenticar usuário");
            return null;
        }
    }
    
    /**
     * Registra o último acesso do usuário.
     * 
     * @param usuario Usuário
     */
    @Transactional
    public void registrarAcesso(Usuario usuario) {
        try {
            usuario.ultimoAcesso = new Date();
            usuario.persist();
        } catch (Exception e) {
            LOGGER.severe("Erro ao registrar acesso: " + e.getMessage());
        }
    }
    
    /**
     * Verifica se um email está em formato válido.
     * 
     * @param email Email a ser validado
     * @return true se o email é válido, false caso contrário
     */
    public boolean isEmailValido(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Criptografa uma senha usando o algoritmo SHA-256.
     * 
     * @param senha Senha em texto plano
     * @return Senha criptografada
     */
    public String criptografarSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.severe("Erro ao criptografar senha: " + e.getMessage());
            return senha; // Fallback inseguro, apenas para não quebrar o fluxo
        }
    }
    
    /**
     * Verifica se a senha fornecida corresponde à senha criptografada.
     * 
     * @param senhaPlana Senha em texto plano
     * @param senhaCriptografada Senha criptografada
     * @return true se as senhas correspondem, false caso contrário
     */
    public boolean verificarSenha(String senhaPlana, String senhaCriptografada) {
        try {
            String senhaCriptografadaVerificacao = criptografarSenha(senhaPlana);
            return senhaCriptografadaVerificacao.equals(senhaCriptografada);
        } catch (Exception e) {
            LOGGER.severe("Erro ao verificar senha: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gera um token aleatório para recuperação de senha.
     * 
     * @return Token gerado
     */
    private String gerarTokenRecuperacao() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Inicia o processo de recuperação de senha para um usuário.
     * 
     * @param email Email do usuário que deseja recuperar a senha
     * @return true se o processo foi iniciado com sucesso, false caso contrário
     */
    @Transactional
    public boolean solicitarRecuperacaoSenha(String email) {
        try {
            if (email == null || email.isEmpty()) {
                throw new BusinessException("Email não informado");
            }
            
            if (!isEmailValido(email)) {
                throw new BusinessException("Formato de email inválido");
            }
            
            Usuario usuario = buscarPorEmail(email);
            
            if (usuario == null) {
                LOGGER.warning("Tentativa de recuperação de senha para email não cadastrado: " + email);
                return false; // Não informamos ao usuário que o email não existe por segurança
            }
            
            if (usuario.status != model.Usuario.Status.ATIVO) {
                LOGGER.warning("Tentativa de recuperação de senha para usuário inativo: " + email);
                return false; // Não informamos ao usuário que a conta está inativa por segurança
            }
            
            // Gerar token
            String token = gerarTokenRecuperacao();
            
            // Salvar token no banco de dados
            TokenRecuperacao tokenRecuperacao = new TokenRecuperacao();
            tokenRecuperacao.token = token;
            tokenRecuperacao.usuario = usuario;
            tokenRecuperacao.dataCriacao = new Date();
            
            // Token válido por 24 horas
            Calendar calendario = Calendar.getInstance();
            calendario.add(Calendar.DAY_OF_MONTH, 1);
            tokenRecuperacao.dataExpiracao = calendario.getTime();
            
            tokenRecuperacao.utilizado = false;
            tokenRecuperacao.persist();
            
            // Enviar email com o token
            enviarEmailRecuperacaoSenha(usuario, token);
            
            LOGGER.info("Recuperação de senha solicitada para o usuário ID: " + usuario.id);
            return true;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao solicitar recuperação de senha");
            return false;
        }
    }
    
    /**
     * Busca um usuário pelo seu email.
     * 
     * @param email Email do usuário a ser buscado
     * @return Usuário encontrado ou null se não existir
     */
    public Usuario buscarPorEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        return Usuario.find("email", email).firstResult();
    }
    
    /**
     * Busca um usuário pelo seu ID.
     * 
     * @param id ID do usuário a ser buscado
     * @return Usuário encontrado ou null se não existir
     */
    public Usuario buscarUsuarioPorId(Long id) {
        if (id == null) {
            return null;
        }
        return Usuario.findById(id);
    }
    
    /**
     * Envia email de boas-vindas para um novo usuário.
     * 
     * @param usuario Usuário recém-cadastrado
     */
    private void enviarEmailBoasVindas(Usuario usuario) {
        try {
            String assunto = "Bem-vindo ao Sistema de Leilões";
            
            StringBuilder corpo = new StringBuilder();
            corpo.append("Olá, ").append(usuario.nomeFantasia).append("!\n\n");
            corpo.append("Seja bem-vindo ao nosso Sistema de Leilões. Sua conta foi criada com sucesso.\n\n");
            
            corpo.append("Dados do seu cadastro:\n");
            corpo.append("- Email: ").append(usuario.email).append("\n");
            corpo.append("- Tipo de usuário: ").append(usuario.tipoUsuario).append("\n\n");
            
            corpo.append("Acesse o sistema através do link: http://sistema.exemplo.com/login\n\n");
            
            corpo.append("Atenciosamente,\n");
            corpo.append("Equipe de Leilões");
            
            emailService.enviarEmail(
                usuario.email,
                assunto,
                corpo.toString()
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao enviar email de boas-vindas: " + e.getMessage());
        }
    }    
    /**
     * Envia email de confirmação de alteração de senha.
     * 
     * @param usuario Usuário que alterou a senha
     */
    private void enviarEmailAlteracaoSenha(Usuario usuario) {
        try {
            String assunto = "Alteração de senha realizada";
            
            StringBuilder corpo = new StringBuilder();
            corpo.append("Olá, ").append(usuario.nomeFantasia).append("!\n\n");
            corpo.append("Sua senha foi alterada com sucesso em nossa plataforma.\n\n");
            
            corpo.append("Se você não realizou esta alteração, entre em contato imediatamente com nossa equipe de suporte.\n\n");
            
            corpo.append("Atenciosamente,\n");
            corpo.append("Equipe de Leilões");
            
            emailService.enviarEmail(
                usuario.email,
                assunto,
                corpo.toString()
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao enviar email de alteração de senha: " + e.getMessage());
        }
    }
    
    /**
     * Envia email de recuperação de senha.
     * 
     * @param usuario Usuário que solicitou a recuperação
     * @param token Token gerado para recuperação
     */
    private void enviarEmailRecuperacaoSenha(Usuario usuario, String token) {
        try {
            String assunto = "Recuperação de senha";
            
            StringBuilder corpo = new StringBuilder();
            corpo.append("Olá, ").append(usuario.nomeFantasia).append("!\n\n");
            corpo.append("Recebemos uma solicitação para recuperação de senha da sua conta.\n\n");
            
            corpo.append("Para criar uma nova senha, acesse o link abaixo:\n");
            corpo.append("http://sistema.exemplo.com/recuperar-senha/").append(token).append("\n\n");
            
            corpo.append("Este link expira em 24 horas.\n\n");
            
            corpo.append("Se você não solicitou esta recuperação, ignore este email.\n\n");
            
            corpo.append("Atenciosamente,\n");
            corpo.append("Equipe de Leilões");
            
            emailService.enviarEmail(
                usuario.email,
                assunto,
                corpo.toString()
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao enviar email de recuperação de senha: " + e.getMessage());
        }
    }
    
    /**
     * Envia email de confirmação de recuperação de senha.
     * 
     * @param usuario Usuário que recuperou a senha
     */
    private void enviarEmailConfirmacaoRecuperacaoSenha(Usuario usuario) {
        try {
            String assunto = "Senha recuperada com sucesso";
            
            StringBuilder corpo = new StringBuilder();
            corpo.append("Olá, ").append(usuario.nomeFantasia).append("!\n\n");
            corpo.append("Sua senha foi redefinida com sucesso em nossa plataforma.\n\n");
            
            corpo.append("Você já pode acessar o sistema com sua nova senha através do link: http://sistema.exemplo.com/login\n\n");
            
            corpo.append("Se você não realizou esta recuperação, entre em contato imediatamente com nossa equipe de suporte.\n\n");
            
            corpo.append("Atenciosamente,\n");
            corpo.append("Equipe de Leilões");
            
            emailService.enviarEmail(
                usuario.email,
                assunto,
                corpo.toString()
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao enviar email de confirmação de recuperação de senha: " + e.getMessage());
        }
    }
    
    /**
     * Envia email informando sobre a desativação da conta.
     * 
     * @param usuario Usuário desativado
     * @param motivo Motivo da desativação
     */
    private void enviarEmailDesativacao(Usuario usuario, String motivo) {
        try {
            String assunto = "Sua conta foi desativada";
            
            StringBuilder corpo = new StringBuilder();
            corpo.append("Olá, ").append(usuario.nomeFantasia).append("!\n\n");
            corpo.append("Sua conta em nosso sistema foi desativada por um administrador.\n\n");
            
            if (motivo != null && !motivo.isEmpty()) {
                corpo.append("Motivo: ").append(motivo).append("\n\n");
            }
            
            corpo.append("Se você acredita que houve um engano ou deseja esclarecimentos, entre em contato com nossa equipe de suporte.\n\n");
            
            corpo.append("Atenciosamente,\n");
            corpo.append("Equipe de Leilões");
            
            emailService.enviarEmail(
                usuario.email,
                assunto,
                corpo.toString()
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao enviar email de desativação: " + e.getMessage());
        }
    }
    
    /**
     * Envia email informando sobre a reativação da conta.
     * 
     * @param usuario Usuário reativado
     */
    private void enviarEmailReativacao(Usuario usuario) {
        try {
            String assunto = "Sua conta foi reativada";
            
            StringBuilder corpo = new StringBuilder();
            corpo.append("Olá, ").append(usuario.nomeFantasia).append("!\n\n");
            corpo.append("Temos o prazer de informar que sua conta em nosso sistema foi reativada.\n\n");
            
            corpo.append("Você já pode acessar o sistema normalmente através do link: http://sistema.exemplo.com/login\n\n");
            
            corpo.append("Atenciosamente,\n");
            corpo.append("Equipe de Leilões");
            
            emailService.enviarEmail(
                usuario.email,
                assunto,
                corpo.toString()
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao enviar email de reativação: " + e.getMessage());
        }
    }
    
    /**
     * Promove um usuário a administrador.
     * 
     * @param usuarioId ID do usuário a ser promovido
     * @param administradorAtual Administrador atual que está realizando a promoção
     * @return Usuário promovido
     */
    @Transactional
    public Usuario promoverAdministrador(Long usuarioId, Usuario administradorAtual) {
        try {
            Usuario usuarioAlvo = buscarUsuarioPorId(usuarioId);
            
            if (usuarioAlvo == null) {
                throw new BusinessException("Usuário não encontrado");
            }
            
            // Verificar permissão
            if (usuarioAlvo.tipoUsuario != Usuario.TipoUsuario.ADMINISTRADOR) {
                throw new BusinessException("Apenas administradores podem promover outros usuários");
            }
            
            if (usuarioAlvo.tipoUsuario == Usuario.TipoUsuario.ADMINISTRADOR) {
                throw new BusinessException("Este usuário já é administrador");
            }
            
            // Promover usuário
            usuarioAlvo.tipoUsuario = Usuario.TipoUsuario.ADMINISTRADOR;
            usuarioAlvo.persist();
            
            // Notificar o usuário
            notificarPromocaoAdministrador(usuarioAlvo);
            
            LOGGER.info("Usuário promovido a administrador. ID: " + usuarioAlvo.id);
            return usuarioAlvo;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao promover usuário a administrador");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Remove privilégios de administrador de um usuário.
     * 
     * @param usuarioId ID do usuário a ter os privilégios removidos
     * @param administradorAtual Administrador atual que está realizando a ação
     * @return Usuário atualizado
     */
    @Transactional
    public Usuario removerAdministrador(Long usuarioId, Usuario administradorAtual) {
        try {
            Usuario usuario = buscarUsuarioPorId(usuarioId);
            
            if (usuario == null) {
                throw new BusinessException("Usuário não encontrado");
            }
            
            // Verificar permissão
            if (administradorAtual.tipoUsuario != Usuario.TipoUsuario.ADMINISTRADOR) {
                throw new BusinessException("Apenas administradores podem remover privilégios de outros usuários");
            }
            
            if (usuario.tipoUsuario != Usuario.TipoUsuario.ADMINISTRADOR) {
                throw new BusinessException("Este usuário não é administrador");
            }
            
            // Garantir que pelo menos um administrador permaneça no sistema
            long totalAdmins = Usuario.count("tipoUsuario = ?1", Usuario.TipoUsuario.ADMINISTRADOR);
            if (totalAdmins <= 1) {
                throw new BusinessException("Operação não permitida. O sistema deve ter pelo menos um administrador");
            }
            
            usuario.persist();
            
            LOGGER.info("Privilégios de administrador removidos. Usuário ID: " + usuario.id);
            return usuario;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            String mensagemErro = ExceptionUtil.handleException(e, "Erro ao remover privilégios de administrador");
            throw new BusinessException(mensagemErro, e);
        }
    }
    
    /**
     * Notifica um usuário sobre sua promoção a administrador.
     * 
     * @param usuario Usuário promovido
     */
    private void notificarPromocaoAdministrador(Usuario usuario) {
        try {
            notificacaoService.criarNotificacao(
                usuario,
                "Você foi promovido a administrador",
                "Você agora possui privilégios de administrador no sistema.",
                "/perfil"
            );
        } catch (Exception e) {
            LOGGER.severe("Erro ao notificar sobre promoção a administrador: " + e.getMessage());
        }
    }
}
