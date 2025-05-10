package security;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import model.Usuario;
import service.AutenticacaoService;
import util.RedirectUtil;
import security.SecurityBinding;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

/**
 * Interceptor que processa as anotações de segurança.
 * Verifica autenticação e roles dos usuários.
 */
@SecurityBinding
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class SecurityInterceptor {

    @Inject
    AutenticacaoService autenticacaoService;

    @Context
    UriInfo uriInfo;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        // Verificar autenticação
        if (context.getMethod().isAnnotationPresent(RequiresAuth.class) ||
            context.getMethod().getDeclaringClass().isAnnotationPresent(RequiresAuth.class)) {
            
            Usuario usuario = autenticacaoService.getUsuarioLogado();
            if (usuario == null) {
                return RedirectUtil.redirectToPathAsObject("/usuarios/login");
            }
        }

        // Verificar roles
        if (context.getMethod().isAnnotationPresent(RequiresRole.class) ||
            context.getMethod().getDeclaringClass().isAnnotationPresent(RequiresRole.class)) {
            
            Usuario usuario = autenticacaoService.getUsuarioLogado();
            if (usuario == null) {
                return RedirectUtil.redirectToPathAsObject("/usuarios/login");
            }

            RequiresRole requiresRole = context.getMethod().getAnnotation(RequiresRole.class);
            if (requiresRole == null) {
                requiresRole = context.getMethod().getDeclaringClass().getAnnotation(RequiresRole.class);
            }

            boolean hasRole = false;
            for (Usuario.TipoUsuario role : requiresRole.value()) {
                if (usuario.tipoUsuario == role) {
                    hasRole = true;
                    break;
                }
            }

            if (!hasRole) {
                return RedirectUtil.redirectToPathAsObject("/error/403");
            }
        }

        return context.proceed();
    }
} 