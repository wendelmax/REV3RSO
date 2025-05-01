package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

/**
 * Anotação para indicar que um método ou classe suporta paginação.
 * Quando usado, o PaginationInterceptor irá processar os headers de paginação
 * e modificar os parâmetros do método.
 */
@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pageable {
    /**
     * Tamanho padrão da página se não for especificado.
     * @return Tamanho padrão da página
     */
    @Nonbinding
    int defaultSize() default 0;
    
    /**
     * Tamanho máximo permitido para a página.
     * @return Tamanho máximo da página
     */
    @Nonbinding
    int maxSize() default 0;
}
