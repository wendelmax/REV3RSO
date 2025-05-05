package dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import model.Convite;

/**
 * DTO (Data Transfer Object) para a entidade Convite.
 * Utilizado para transferência de dados entre camadas, sem expor a entidade diretamente.
 * Implementado como record para garantir imutabilidade e reduzir código boilerplate.
 */
public record ConviteDTO(
    Long id,
    Long leilaoId,
    String tituloLeilao,
    Long fornecedorId,
    String nomeFornecedor,
    Date dataEnvio,
    Date dataResposta,
    String status
) {
    
    /**
     * Construtor que converte uma entidade Convite em DTO.
     * 
     * @param convite A entidade a ser convertida.
     */
    public static ConviteDTO fromEntity(Convite convite) {
        return new ConviteDTO(
            convite.id,
            convite.leilao != null ? convite.leilao.id : null,
            convite.leilao != null ? convite.leilao.titulo : null,
            convite.fornecedor != null ? convite.fornecedor.id : null,
            convite.fornecedor != null ? convite.fornecedor.nomeFantasia : null,
            convite.dataEnvio,
            convite.dataResposta,
            convite.status != null ? convite.status.name() : null
        );
    }
    
    /**
     * Cria uma lista de DTOs a partir de uma lista de entidades.
     * 
     * @param convites Lista de entidades Convite
     * @return Lista de ConviteDTO
     */
    public static List<ConviteDTO> converterLista(List<Convite> convites) {
        return convites.stream()
            .map(ConviteDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
