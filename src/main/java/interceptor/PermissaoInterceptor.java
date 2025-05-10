package interceptor;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Response;
import model.Usuario;
import service.AuthService;

/**
 * Interceptor para verificação de permissões.
 */
@Interceptor
@RequerPermissao
@Priority(2000)
public class PermissaoInterceptor {
    
    @Inject
    AuthService authService;
    
    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        // Obtém o usuário do contexto
        Usuario usuario = (Usuario) context.getContextData().get("usuario");
        
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        // Obtém a anotação de permissão
        RequerPermissao permissao = context.getMethod().getAnnotation(RequerPermissao.class);
        if (permissao == null) {
            permissao = context.getTarget().getClass().getAnnotation(RequerPermissao.class);
        }
        
        if (permissao == null) {
            return context.proceed();
        }
        
        // Verifica as permissões
        for (RequerPermissao.TipoPermissao tipo : permissao.value()) {
            switch (tipo) {
                case ADMIN:
                    if (authService.isAdmin(usuario)) {
                        return context.proceed();
                    }
                    break;
                case COMPRADOR:
                    if (authService.isComprador(usuario)) {
                        return context.proceed();
                    }
                    break;
                case FORNECEDOR:
                    if (authService.isFornecedor(usuario)) {
                        return context.proceed();
                    }
                    break;
            }
        }
        
        // Se chegou aqui, o usuário não tem permissão
        return Response.status(Response.Status.FORBIDDEN).build();
    }
} 