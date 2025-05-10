package service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import model.Usuario;
import model.Usuario.Status;

import java.util.Date;

/**
 * Serviço responsável pela autenticação de usuários.
 */
@ApplicationScoped
public class AuthService {
    
    @Inject
    UsuarioService usuarioService;
    
    /**
     * Autentica um usuário com email e senha.
     * 
     * @param email Email do usuário
     * @param senha Senha do usuário
     * @return Usuário autenticado ou null se a autenticação falhar
     */
    @Transactional
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = Usuario.encontrarPorEmail(email);
        
        if (usuario != null && 
            usuario.status == Status.ATIVO && 
            usuario.senha.equals(senha)) { // TODO: Implementar hash de senha
            
            // Atualiza último acesso
            usuario.ultimoAcesso = new Date();
            usuario.persist();
            
            return usuario;
        }
        
        return null;
    }
    
    /**
     * Verifica se um usuário está autenticado.
     * 
     * @param usuario Usuário a ser verificado
     * @return true se o usuário estiver autenticado e ativo
     */
    public boolean isAutenticado(Usuario usuario) {
        return usuario != null && usuario.status == Status.ATIVO;
    }
    
    /**
     * Verifica se um usuário tem permissão de administrador.
     * 
     * @param usuario Usuário a ser verificado
     * @return true se o usuário for administrador
     */
    public boolean isAdmin(Usuario usuario) {
        return isAutenticado(usuario) && 
               usuario.tipoUsuario == Usuario.TipoUsuario.ADMINISTRADOR;
    }
    
    /**
     * Verifica se um usuário tem permissão de comprador.
     * 
     * @param usuario Usuário a ser verificado
     * @return true se o usuário for comprador
     */
    public boolean isComprador(Usuario usuario) {
        return isAutenticado(usuario) && 
               usuario.tipoUsuario == Usuario.TipoUsuario.COMPRADOR;
    }
    
    /**
     * Verifica se um usuário tem permissão de fornecedor.
     * 
     * @param usuario Usuário a ser verificado
     * @return true se o usuário for fornecedor
     */
    public boolean isFornecedor(Usuario usuario) {
        return isAutenticado(usuario) && 
               usuario.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR;
    }
} 