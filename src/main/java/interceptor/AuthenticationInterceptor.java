package interceptor;

import annotation.RequiresAuth;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AutenticacaoService;

/**
 * Interceptador que verifica automaticamente se um usuário está autenticado.
 * Utilizado em conjunto com a anotação @RequiresAuth.
 */
@Interceptor
@RequiresAuth
@Priority(Interceptor.Priority.APPLICATION)
public class AuthenticationInterceptor {

    @Inject
    AutenticacaoService autenticacaoService;
    
    @Inject
    HttpServletRequest request;
    
    @Inject
    HttpServletResponse response;
    
    /**
     * Método invocado em torno do método anotado com @RequiresAuth.
     * Verifica se o usuário está autenticado e, caso contrário, redireciona para a página de login.
     * 
     * @param context Contexto da invocação
     * @return Resultado da invocação do método, se o usuário estiver autenticado
     * @throws Exception Se ocorrer algum erro durante a invocação
     */
    @AroundInvoke
    public Object checkAuthentication(InvocationContext context) throws Exception {
        if (autenticacaoService.getUsuarioLogado() == null) {
            // Usuário não está autenticado, adiciona mensagem na sessão
            request.getSession().setAttribute("mensagem", "Você precisa estar logado para acessar esta página");
            request.getSession().setAttribute("tipo", "danger");
            
            // Redireciona para a página de login
            response.sendRedirect("/usuarios/login");
            return null;
        }
        
        // Usuário está autenticado, continua a execução do método
        return context.proceed();
    }
}
