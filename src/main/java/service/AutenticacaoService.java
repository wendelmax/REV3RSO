package service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import model.Leilao;
import model.Usuario;
import util.ExceptionUtil;

/**
 * Serviço responsável pela autenticação e autorização de usuários.
 */
@ApplicationScoped
public class AutenticacaoService {
    
    private static final Logger LOGGER = Logger.getLogger(AutenticacaoService.class.getName());
    
    @Inject
    HttpServletRequest request;
    
    /**
     * Realiza o login de um usuário.
     * 
     * @param email Email do usuário
     * @param senha Senha do usuário
     * @return Usuário logado ou null se as credenciais forem inválidas
     */
    @Transactional
    public Usuario login(String email, String senha) {
        try {
            Usuario usuario = Usuario.find("email", email).firstResult();
            if (usuario != null && verificarSenha(senha, usuario.senha)) {
                // Registra o login
                LOGGER.info("Usuário logado com sucesso: " + email);
                
                // Armazena o ID do usuário na sessão
                request.getSession().setAttribute("usuarioId", usuario.id);
                
                return usuario;
            }
            
            // Credenciais inválidas
            LOGGER.warning("Tentativa de login com credenciais inválidas: " + email);
            return null;
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao tentar realizar login");
            return null;
        }
    }
    
    /**
     * Realiza o logout do usuário atual.
     */
    public void logout() {
        try {
            // Remove os atributos da sessão
            request.getSession().removeAttribute("usuarioId");
            request.getSession().invalidate();
            
            LOGGER.info("Logout realizado com sucesso");
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao tentar realizar logout");
        }
    }
    
    /**
     * Obtém o usuário logado.
     * 
     * @return Usuário logado ou null se não houver usuário logado
     */
    public Usuario getUsuarioLogado() {
        try {
            // Obtém o ID do usuário da sessão
            Long usuarioId = request.getSession().getAttribute("usuarioId") != null ?
                    Long.valueOf(request.getSession().getAttribute("usuarioId").toString()) : null;
            
            if (usuarioId == null) {
                return null;
            }
            
            return Usuario.findById(usuarioId);
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Erro ao tentar obter usuário logado");
            return null;
        }
    }
    
    /**
     * Verifica se uma senha corresponde à senha armazenada.
     * 
     * @param senhaDigitada Senha digitada pelo usuário
     * @param senhaArmazenada Senha armazenada no banco
     * @return true se as senhas corresponderem, false caso contrário
     */
    private boolean verificarSenha(String senhaDigitada, String senhaArmazenada) {
        // Implementação simplificada - em produção, usar bcrypt ou similar
        return senhaArmazenada.equals(senhaDigitada);
    }
    
    /**
     * Verifica se o usuário tem permissão para realizar uma ação em um recurso.
     * 
     * @param usuario Usuário a ser verificado
     * @param acao Nome da ação
     * @param recurso Recurso a ser acessado
     * @return true se o usuário tem permissão, false caso contrário
     */
    public boolean temPermissao(Usuario usuario, String acao, Object recurso) {
        if (usuario == null) {
            return false;
        }
        
        // Administradores têm permissão total
        if (usuario.administrador) {
            return true;
        }
        
        // Verifica permissões específicas
        if (recurso instanceof Leilao) {
            return temPermissaoLeilao(usuario, acao, (Leilao) recurso);
        }
        
        // Implementar outras verificações conforme necessário
        
        return false;
    }
    
    /**
     * Verifica se o usuário tem permissão para realizar uma ação em um leilão.
     * 
     * @param usuario Usuário a ser verificado
     * @param acao Nome da ação
     * @param leilao Leilão a ser acessado
     * @return true se o usuário tem permissão, false caso contrário
     */
    private boolean temPermissaoLeilao(Usuario usuario, String acao, Leilao leilao) {
        // Criador do leilão tem permissão ampla
        if (usuario.equals(leilao.criador)) {
            return true;
        }
        
        // Fornecedor pode visualizar leilões abertos ou que foi convidado
        if (usuario.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR) {
            if (acao.equals("visualizar")) {
                return leilao.tipoLeilao == Leilao.TipoLeilao.ABERTO || leilao.isConvidado(usuario);
            }
        }
        
        return false;
    }
}