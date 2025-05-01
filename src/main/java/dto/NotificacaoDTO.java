package dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import model.Notificacao;

/**
 * DTO (Data Transfer Object) para a entidade Notificacao.
 * Utilizado para transferência de dados entre camadas, sem expor a entidade diretamente.
 * Implementado como record para garantir imutabilidade e reduzir código boilerplate.
 */
public record NotificacaoDTO(
    Long id,
    String titulo,
    String mensagem,
    String link,
    Date dataEnvio,
    boolean lida,
    Date dataLeitura,
    String tipo
) {
    
    /**
     * Construtor que converte uma entidade Notificacao em DTO.
     * 
     * @param notificacao A entidade a ser convertida.
     */
    public static NotificacaoDTO fromEntity(Notificacao notificacao) {
        return new NotificacaoDTO(
            notificacao.id,
            notificacao.titulo,
            notificacao.mensagem,
            notificacao.link,
            notificacao.dataEnvio,
            notificacao.lida,
            notificacao.dataLeitura,
            notificacao.tipo != null ? notificacao.tipo.name() : null
        );
    }
    
    /**
     * Cria uma lista de DTOs a partir de uma lista de entidades.
     * 
     * @param notificacoes Lista de entidades Notificacao
     * @return Lista de NotificacaoDTO
     */
    public static List<NotificacaoDTO> converterLista(List<Notificacao> notificacoes) {
        return notificacoes.stream()
            .map(NotificacaoDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
