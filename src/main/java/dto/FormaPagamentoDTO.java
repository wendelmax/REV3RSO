package dto;

import java.util.List;
import java.util.stream.Collectors;

import model.FormaPagamento;

/**
 * DTO (Data Transfer Object) para a entidade FormaPagamento.
 * Utilizado para transferência de dados entre camadas, sem expor a entidade diretamente.
 * Implementado como record para garantir imutabilidade e reduzir código boilerplate.
 */
public record FormaPagamentoDTO(
    Long id,
    String descricao,
    int leiloesVinculados
) {
    
    /**
     * Construtor que converte uma entidade FormaPagamento em DTO.
     * 
     * @param formaPagamento A entidade a ser convertida.
     */
    public static FormaPagamentoDTO fromEntity(FormaPagamento formaPagamento) {
        int leiloesVinculados = 0;
        if (formaPagamento.leiloes != null) {
            leiloesVinculados = formaPagamento.leiloes.size();
        }
        
        return new FormaPagamentoDTO(
            formaPagamento.id,
            formaPagamento.descricao,
            leiloesVinculados
        );
    }
    
    /**
     * Converte este DTO em uma entidade FormaPagamento.
     * 
     * @return Uma nova instância de FormaPagamento com os dados deste DTO.
     */
    public FormaPagamento paraEntidade() {
        FormaPagamento formaPagamento = new FormaPagamento();
        formaPagamento.descricao = this.descricao;
        return formaPagamento;
    }
    
    /**
     * Cria uma lista de DTOs a partir de uma lista de entidades.
     * 
     * @param formasPagamento Lista de entidades FormaPagamento
     * @return Lista de FormaPagamentoDTO
     */
    public static List<FormaPagamentoDTO> converterLista(List<FormaPagamento> formasPagamento) {
        return formasPagamento.stream()
            .map(FormaPagamentoDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
