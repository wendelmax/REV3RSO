package util;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Classe utilitária para redirecionamentos em controladores.
 * Facilita a criação de redirecionamentos para diferentes URLs.
 */
public class RedirectUtil {

    /**
     * Redireciona para uma classe de controlador.
     * 
     * @param controllerClass Classe do controlador de destino
     * @return Objeto Uni contendo o redirecionamento
     */
    public static Uni<Object> redirect(Class<? extends Controller> controllerClass) {
        return Uni.createFrom().item(Response.seeOther(
                UriBuilder.fromResource(controllerClass).build()).build());
    }
    
    /**
     * Redireciona para um método específico de um controlador.
     * 
     * @param controllerClass Classe do controlador de destino
     * @param methodName Nome do método de destino
     * @return Objeto Uni contendo o redirecionamento
     */
    public static Uni<Object> redirectToMethod(Class<? extends Controller> controllerClass, String methodName) {
        return Uni.createFrom().item(Response.seeOther(
                UriBuilder.fromResource(controllerClass).path(controllerClass, methodName).build()).build());
    }
    
    /**
     * Redireciona para um método específico de um controlador com um parâmetro de caminho.
     * 
     * @param controllerClass Classe do controlador de destino
     * @param methodName Nome do método de destino
     * @param pathParam Valor do parâmetro de caminho
     * @return Objeto Uni contendo o redirecionamento
     */
    public static Uni<Object> redirectToMethod(Class<? extends Controller> controllerClass, String methodName, Object pathParam) {
        return Uni.createFrom().item(Response.seeOther(
                UriBuilder.fromResource(controllerClass).path(controllerClass, methodName).build(pathParam)).build());
    }
    
    /**
     * Redireciona para um caminho específico.
     * 
     * @param path Caminho para redirecionamento
     * @return String contendo o comando de redirecionamento
     */
    public static Uni<String> redirectToPath(String path) {
        return Uni.createFrom().item("redirect:" + path);
    }
    
    /**
     * Redireciona para um caminho específico, retornando Uni<Object>.
     * Use este método em controllers que precisam retornar Uni<Object>.
     * 
     * @param path Caminho para redirecionamento
     * @return Uni<Object> contendo o comando de redirecionamento
     */
    public static Uni<Object> redirectToPathAsObject(String path) {
        return Uni.createFrom().item((Object) ("redirect:" + path));
    }
    
    // Removi o redirectToController pois o Controller.redirect não é acessível
    
    /**
     * Cria um objeto TemplateInstance para redirecionamento
     * Útil quando um método retorna TemplateInstance e precisa redirecionar
     * 
     * @param path Caminho para redirecionar
     * @return TemplateInstance para redirecionamento
     */
    public static TemplateInstance redirectTemplate(String path) {
        // No Quarkus Renarde, se retornarmos uma string que começa com 'redirect:', 
        // ele interpreta como um redirecionamento e redireciona para o caminho especificado
        // Criamos um TemplateInstance que gera a string de redirecionamento
        return new io.quarkus.qute.TemplateInstance() {
            @Override
            public String render() {
                return "redirect:" + path;
            }
            
            @Override
            public Uni<String> createUni() {
                return Uni.createFrom().item("redirect:" + path);
            }
        };
    }
    
    /**
     * Redireciona para uma URL completa.
     * 
     * @param url URL completa para redirecionamento
     * @return Objeto Response contendo o redirecionamento
     */
    public static Response redirectToUrl(String url) {
        return Response.seeOther(URI.create(url)).build();
    }
}
