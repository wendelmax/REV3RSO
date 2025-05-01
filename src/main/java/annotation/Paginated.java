package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação utilizada para marcar métodos de controladores que suportam paginação.
 * Quando um endpoint é marcado com esta anotação, o sistema irá verificar o header
 * "X-Pagination" para decidir se os resultados devem ser paginados.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Paginated {
}
