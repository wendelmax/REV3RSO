package interceptor;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import model.Usuario;
import service.AuthService;

import java.util.Base64;

/**
 * Interceptor para validação de autenticação.
 */
@Interceptor
@Autenticado
@Priority(1000)
public class AuthInterceptor {
    
    @Context
    HttpHeaders headers;
    
    @Context
    SecurityContext securityContext;
    
    @Inject
    AuthService authService;
    
    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .header("WWW-Authenticate", "Basic realm=\"REV3RSO\"")
                .build();
        }
        
        // Decodifica o header de autenticação
        String credentials = new String(Base64.getDecoder().decode(authHeader.substring(6)));
        String[] parts = credentials.split(":");
        
        if (parts.length != 2) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .header("WWW-Authenticate", "Basic realm=\"REV3RSO\"")
                .build();
        }
        
        String email = parts[0];
        String senha = parts[1];
        
        // Autentica o usuário
        Usuario usuario = authService.autenticar(email, senha);
        
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .header("WWW-Authenticate", "Basic realm=\"REV3RSO\"")
                .build();
        }
        
        // Adiciona o usuário ao contexto
        context.getContextData().put("usuario", usuario);
        
        // Continua a execução
        return context.proceed();
    }
} 