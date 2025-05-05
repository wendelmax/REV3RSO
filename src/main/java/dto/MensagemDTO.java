package dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import model.Mensagem;

/**
 * DTO (Data Transfer Object) para a entidade Mensagem.
 * Utilizado para transferência de dados entre camadas, sem expor a entidade diretamente.
 * Implementado como record para garantir imutabilidade e reduzir código boilerplate.
 */
public record MensagemDTO(
    Long id,
    Long leilaoId,
    String conteudo,
    String tipoMensagem,
    Long autorId,
    String nomeAutor,
    Date dataCriacao,
    boolean respondida,
    Long mensagemPaiId
) {
    
    /**
     * Construtor que converte uma entidade Mensagem em DTO.
     * 
     * @param mensagem A entidade a ser convertida.
     */
    public static MensagemDTO fromEntity(Mensagem mensagem) {
        return new MensagemDTO(
            mensagem.id,
            mensagem.leilao != null ? mensagem.leilao.id : null,
            mensagem.conteudo,
            mensagem.tipo != null ? mensagem.tipo.name() : null,
            mensagem.autor != null ? mensagem.autor.id : null,
            mensagem.autor != null ? mensagem.autor.nomeFantasia : null,
            mensagem.dataCriacao,
            mensagem.respondida,
            mensagem.mensagemPai != null ? mensagem.mensagemPai.id : null
        );
    }
    
    /**
     * Converte este DTO em uma entidade Mensagem.
     * Observação: Este método deve ser usado apenas para novas mensagens,
     * pois não preenche todos os campos da entidade.
     * 
     * @return Uma nova instância de Mensagem com os dados deste DTO.
     */
    public Mensagem paraEntidade() {
        Mensagem mensagem = new Mensagem();
        mensagem.conteudo = this.conteudo;
        
        // Outros campos como leilao, autor, tipo, devem ser preenchidos pelo serviço
        return mensagem;
    }
    
    /**
     * Cria uma lista de DTOs a partir de uma lista de entidades.
     * 
     * @param mensagens Lista de entidades Mensagem
     * @return Lista de MensagemDTO
     */
    public static List<MensagemDTO> converterLista(List<Mensagem> mensagens) {
        return mensagens.stream()
            .map(MensagemDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
