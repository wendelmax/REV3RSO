package dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import model.Anexo;

/**
 * DTO (Data Transfer Object) para a classe interna Anexo do Leilão.
 * Utilizado para transferência de dados entre camadas, sem expor a entidade diretamente.
 * Implementado como record para garantir imutabilidade e reduzir código boilerplate.
 */
public record AnexoDTO(
    String nomeArquivo,
    String caminhoArquivo,
    String tipoArquivo,
    Date dataUpload
) {
    
    /**
     * Construtor que converte uma entidade Anexo em DTO.
     * 
     * @param anexo A entidade a ser convertida.
     */
    public static AnexoDTO fromEntity(Anexo anexo) {
        return new AnexoDTO(
            anexo.nome,
            anexo.caminhoArquivo,
            anexo.tipoArquivo,
            anexo.dataUpload
        );
    }
    
    /**
     * Cria uma lista de DTOs a partir de uma lista de entidades.
     * 
     * @param anexos Lista de entidades Anexo
     * @return Lista de AnexoDTO
     */
    public static List<AnexoDTO> converterLista(List<Anexo> anexos) {
        if (anexos == null) {
            return List.of();
        }
        
        return anexos.stream()
            .map(AnexoDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
