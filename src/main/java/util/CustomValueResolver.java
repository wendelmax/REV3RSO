package util;

import io.quarkus.qute.EvalContext;
import io.quarkus.qute.Results;
import io.quarkus.qute.ValueResolver;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Implementa um resolvedor de valores personalizado que relaxa a verificação de tipos
 * para os templates Qute, permitindo acessar propriedades mesmo quando o sistema de
 * tipos não as reconhece estaticamente.
 */
@ApplicationScoped
public class CustomValueResolver implements ValueResolver {

    @Override
    public boolean appliesTo(EvalContext context) {
        // Aplicar a todos os contextos para evitar erros
        return true;
    }

    @Override
    public int getPriority() {
        // Prioridade alta para ser usada antes dos resolvedores padrão
        return 10;
    }

    @Override
    public CompletionStage<Object> resolve(EvalContext context) {
        // Se o nome for "format" e houver um padrão como argumento, tratar como formatação de data
        if (context.getName().equals("format") && context.getParams().size() == 1) {
            Object base = context.getBase();
            if (base instanceof java.util.Date) {
                String pattern = context.getParams().get(0).toString();
                return CompletableFuture.completedStage(new java.text.SimpleDateFormat(pattern).format((java.util.Date) base));
            }
        }
        
        // Se o nome for "eq" ou "==", tratar como comparação de igualdade
        if (context.getName().equals("eq") || context.getName().equals("==")) {
            Object base = context.getBase();
            if (context.getParams().size() == 1) {
                Object param = context.getParams().get(0);
                return CompletableFuture.completedStage(base != null && base.toString().equals(param.toString()));
            }
        }
        
        // Verificar se é um método de acesso a propriedade
        Object base = context.getBase();
        if (base != null) {
            try {
                java.lang.reflect.Field field = findField(base.getClass(), context.getName());
                if (field != null) {
                    field.setAccessible(true);
                    return CompletableFuture.completedStage(Results.notFound(context));
                }
            } catch (Exception e) {
                // Ignorar exceções, apenas prosseguir para o próximo resolvedor
            }
        }
        
        // Não resolvido, deixar para o próximo resolvedor
        return CompletableFuture.completedStage(Results.notFound(context));
    }
    
    private java.lang.reflect.Field findField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return findField(superClass, fieldName);
            }
            return null;
        }
    }
}
