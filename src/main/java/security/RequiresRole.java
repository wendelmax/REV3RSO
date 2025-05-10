package security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import model.Usuario.TipoUsuario;

/**
 * Anotação para marcar endpoints que requerem um tipo específico de usuário.
 * Se o usuário não tiver o tipo requerido, será redirecionado para a página de erro 403.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
    TipoUsuario[] value();
} 