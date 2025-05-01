package interceptor;

import java.io.IOException;
import java.lang.reflect.Method;

import annotation.RequiresRole;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Usuario;
import service.AutenticacaoService;

/**
 * Interceptador que verifica automaticamente se um usuário possui o papel/função requerido.
 * Utilizado em conjunto com a anotação @RequiresRole.
 */
@Interceptor
@RequiresRole("")
@Priority(Interceptor.Priority.APPLICATION + 10) // Prioridade após o AuthenticationInterceptor
public class RoleInterceptor {

    @Inject
    AutenticacaoService autenticacaoService;
    
    @Inject
    HttpServletRequest request;
    
    @Inject
    HttpServletResponse response;
    
    /**
     * Método invocado em torno do método anotado com @RequiresRole.
     * Verifica se o usuário possui o papel/função requerido e, caso contrário, 
     * redireciona para a página inicial com mensagem de erro.
     * 
     * @param context Contexto da invocação
     * @return Resultado da invocação do método, se o usuário possuir o papel/função requerido
     * @throws Exception Se ocorrer algum erro durante a invocação
     */
    @AroundInvoke
    public Object checkRole(InvocationContext context) throws Exception {
        // Obtém o usuário logado
        Usuario usuario = autenticacaoService.getUsuarioLogado();
        if (usuario == null) {
            // Usuário não está autenticado, adiciona mensagem na sessão
            request.getSession().setAttribute("mensagem", "Você precisa estar logado para acessar esta página");
            request.getSession().setAttribute("tipo", "danger");
            
            // Redireciona para a página de login
            response.sendRedirect("/usuarios/login");
            return null;
        }
        
        // Obtém o papel/função requerido da anotação
        String requiredRole = getRequiredRole(context);
        
        // Verifica se o usuário possui o papel/função requerido
        if (!hasRole(usuario, requiredRole)) {
            // Usuário não possui o papel/função requerido, adiciona mensagem na sessão
            request.getSession().setAttribute("mensagem", "Você não tem permissão para acessar esta página");
            request.getSession().setAttribute("tipo", "danger");
            
            // Redireciona para a página inicial
            response.sendRedirect("/");
            return null;
        }
        
        // Usuário possui o papel/função requerido, continua a execução do método
        return context.proceed();
    }
    
    /**
     * Obtém o papel/função requerido da anotação @RequiresRole.
     * 
     * @param context Contexto da invocação
     * @return O papel/função requerido ou uma string vazia se não houver
     */
    private String getRequiredRole(InvocationContext context) {
        // Tenta obter a anotação do método
        Method method = context.getMethod();
        RequiresRole methodAnnotation = method.getAnnotation(RequiresRole.class);
        if (methodAnnotation != null) {
            return methodAnnotation.value();
        }
        
        // Se não encontrar no método, tenta obter da classe
        Class<?> targetClass = context.getTarget().getClass();
        RequiresRole classAnnotation = targetClass.getAnnotation(RequiresRole.class);
        if (classAnnotation != null) {
            return classAnnotation.value();
        }
        
        // Se não encontrar em nenhum lugar, retorna string vazia
        return "";
    }
    
    /**
     * Verifica se o usuário possui o papel/função especificado.
     * 
     * @param usuario O usuário a ser verificado
     * @param role O papel/função a ser verificado
     * @return true se o usuário possuir o papel/função, false caso contrário
     */
    private boolean hasRole(Usuario usuario, String role) {
        if (role.isEmpty()) {
            return true; // Se não especificar papel/função, qualquer usuário tem acesso
        }
        
        switch (role) {
            case "COMPRADOR":
                return usuario.tipoUsuario == Usuario.TipoUsuario.COMPRADOR;
            case "FORNECEDOR":
                return usuario.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR;
            case "ADMINISTRADOR":
                return usuario.administrador;
            default:
                return false;
        }
    }
}
