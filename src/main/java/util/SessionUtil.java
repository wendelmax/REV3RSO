package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.Usuario;
import service.UsuarioService;

public class SessionUtil {
    
    private static final String USUARIO_ID = "usuarioId";
    private static final String TIPO_USUARIO = "tipoUsuario";
    
    public static void setUsuarioLogado(HttpServletRequest request, Usuario usuario) {
        HttpSession session = request.getSession();
        session.setAttribute(USUARIO_ID, usuario.id);
        session.setAttribute(TIPO_USUARIO, usuario.tipoUsuario.toString());
    }
    
    public static Usuario getUsuarioLogado(HttpServletRequest request, UsuarioService usuarioService) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long usuarioId = (Long) session.getAttribute(USUARIO_ID);
            if (usuarioId != null) {
                return usuarioService.buscarPorId(usuarioId);
            }
        }
        return null;
    }
    
    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
    
    public static boolean isUsuarioLogado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(USUARIO_ID) != null;
    }
} 