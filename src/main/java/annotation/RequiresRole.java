package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.interceptor.InterceptorBinding;

/**
 * Anotação para indicar que um método ou classe requer um papel/função específico.
 * Utilizada em conjunto com interceptadores para verificação de autorização.
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequiresRole {
    /**
     * O papel/função requerido para acessar o recurso.
     */
    String value();
}
