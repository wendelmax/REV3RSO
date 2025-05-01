package dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import model.Lance;

/**
 * DTO (Data Transfer Object) para a entidade Lance.
 * Utilizado para transferência de dados entre camadas, sem expor a entidade diretamente.
 * Implementado como record para garantir imutabilidade e reduzir código boilerplate.
 */
public record LanceDTO(
    Long id,
    Long leilaoId,
    String tituloLeilao,
    Long fornecedorId,
    String nomeFornecedor,
    BigDecimal valor,
    String condicoesEntrega,
    Integer prazoEntrega,
    Integer prazoPagamento,
    Date dataCriacao,
    boolean vencedor
) {
    
    /**
     * Construtor que converte uma entidade Lance em DTO.
     * 
     * @param lance A entidade a ser convertida.
     */
    public static LanceDTO fromEntity(Lance lance) {
        return new LanceDTO(
            lance.id,
            lance.leilao != null ? lance.leilao.id : null,
            lance.leilao != null ? lance.leilao.titulo : null,
            lance.fornecedor != null ? lance.fornecedor.id : null,
            lance.fornecedor != null ? lance.fornecedor.nomeFantasia : null,
            lance.valor,
            lance.condicoesEntrega,
            lance.prazoEntrega,
            lance.prazoPagamento,
            lance.dataCriacao,
            lance.vencedor
        );
    }
    
    /**
     * Converte este DTO em uma entidade Lance para registro.
     * Observação: Este método deve ser usado apenas para novos lances,
     * pois não preenche todos os campos da entidade.
     * 
     * @return Uma nova instância de Lance com os dados deste DTO.
     */
    public Lance paraEntidade() {
        Lance lance = new Lance();
        lance.valor = this.valor;
        lance.condicoesEntrega = this.condicoesEntrega;
        lance.prazoEntrega = this.prazoEntrega;
        lance.prazoPagamento = this.prazoPagamento;
        lance.dataCriacao = new Date();
        
        // Outros campos como leilão, fornecedor, vencedor, devem ser preenchidos pelo serviço
        return lance;
    }
    
    /**
     * Cria uma lista de DTOs a partir de uma lista de entidades.
     * 
     * @param lances Lista de entidades Lance
     * @return Lista de LanceDTO
     */
    public static List<LanceDTO> converterLista(List<Lance> lances) {
        return lances.stream()
            .map(LanceDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
