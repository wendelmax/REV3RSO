package interceptor;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para verificar permissões específicas.
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequerPermissao {
    /**
     * Tipo de permissão requerida.
     */
    TipoPermissao[] value() default {};
    
    /**
     * Tipos de permissão disponíveis.
     */
    enum TipoPermissao {
        ADMIN,
        COMPRADOR,
        FORNECEDOR
    }
} 