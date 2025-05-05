package dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import model.Avaliacao;

/**
 * DTO (Data Transfer Object) para a entidade Avaliacao.
 * Utilizado para transferência de dados entre camadas, sem expor a entidade diretamente.
 * Implementado como record para garantir imutabilidade e reduzir código boilerplate.
 */
public record AvaliacaoDTO(
    Long id,
    Long leilaoId,
    String tituloLeilao,
    Long avaliadoId,
    String nomeAvaliado,
    Long avaliadorId,
    String nomeAvaliador,
    Integer nota,
    String comentario,
    Date dataCriacao
) {
    
    /**
     * Construtor que converte uma entidade Avaliacao em DTO.
     * 
     * @param avaliacao A entidade a ser convertida.
     */
    public static AvaliacaoDTO fromEntity(Avaliacao avaliacao) {
        return new AvaliacaoDTO(
            avaliacao.id,
            avaliacao.leilao != null ? avaliacao.leilao.id : null,
            avaliacao.leilao != null ? avaliacao.leilao.titulo : null,
            avaliacao.avaliado != null ? avaliacao.avaliado.id : null,
            avaliacao.avaliado != null ? avaliacao.avaliado.nomeFantasia : null,
            avaliacao.avaliador != null ? avaliacao.avaliador.id : null,
            avaliacao.avaliador != null ? avaliacao.avaliador.nomeFantasia : null,
            avaliacao.nota,
            avaliacao.comentario,
            avaliacao.dataAvaliacao
        );
    }
    
    /**
     * Converte este DTO em uma entidade Avaliacao.
     * Observação: Este método deve ser usado apenas para novas avaliações,
     * pois não preenche todos os campos da entidade.
     * 
     * @return Uma nova instância de Avaliacao com os dados deste DTO.
     */
    public Avaliacao paraEntidade() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.nota = this.nota;
        avaliacao.comentario = this.comentario;
        
        // Outros campos como leilão, avaliador, avaliado, devem ser preenchidos pelo serviço
        return avaliacao;
    }
    
    /**
     * Cria uma lista de DTOs a partir de uma lista de entidades.
     * 
     * @param avaliacoes Lista de entidades Avaliacao
     * @return Lista de AvaliacaoDTO
     */
    public static List<AvaliacaoDTO> converterLista(List<Avaliacao> avaliacoes) {
        return avaliacoes.stream()
            .map(AvaliacaoDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
