package dto;

import java.util.List;
import java.util.stream.Collectors;

import model.AreaAtuacao;

/**
 * DTO (Data Transfer Object) para a entidade AreaAtuacao.
 * Utilizado para transferência de dados entre camadas, sem expor a entidade diretamente.
 * Implementado como record para garantir imutabilidade e reduzir código boilerplate.
 */
public record AreaAtuacaoDTO(
    Long id,
    String descricao,
    int fornecedoresVinculados
) {
    
    /**
     * Construtor que converte uma entidade AreaAtuacao em DTO.
     * 
     * @param areaAtuacao A entidade a ser convertida.
     */
    public static AreaAtuacaoDTO fromEntity(AreaAtuacao areaAtuacao) {
        int fornecedoresVinculados = 0;
        if (areaAtuacao.fornecedores != null) {
            fornecedoresVinculados = areaAtuacao.fornecedores.size();
        }
        
        return new AreaAtuacaoDTO(
            areaAtuacao.id,
            areaAtuacao.descricao,
            fornecedoresVinculados
        );
    }
    
    /**
     * Converte este DTO em uma entidade AreaAtuacao.
     * 
     * @return Uma nova instância de AreaAtuacao com os dados deste DTO.
     */
    public AreaAtuacao paraEntidade() {
        AreaAtuacao areaAtuacao = new AreaAtuacao();
        areaAtuacao.descricao = this.descricao;
        return areaAtuacao;
    }
    
    /**
     * Cria uma lista de DTOs a partir de uma lista de entidades.
     * 
     * @param areasAtuacao Lista de entidades AreaAtuacao
     * @return Lista de AreaAtuacaoDTO
     */
    public static List<AreaAtuacaoDTO> converterLista(List<AreaAtuacao> areasAtuacao) {
        return areasAtuacao.stream()
            .map(AreaAtuacaoDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
