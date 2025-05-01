package dto;

import java.util.List;
import java.util.stream.Collectors;

import model.Leilao;

/**
 * DTO (Data Transfer Object) para a entidade Categoria de Leilão.
 * Utilizado para transferência de dados entre camadas, sem expor a entidade diretamente.
 * Implementado como record para garantir imutabilidade e reduzir código boilerplate.
 */
public record CategoriaDTO(
    Long id,
    String nome,
    String descricao,
    int leiloesVinculados
) {
    
    /**
     * Construtor que converte uma entidade Categoria em DTO.
     * 
     * @param categoria A entidade a ser convertida.
     */
    public static CategoriaDTO fromEntity(Leilao.Categoria categoria) {
        int leiloesVinculados = 0;
        if (categoria.leiloes != null) {
            leiloesVinculados = categoria.leiloes.size();
        }
        
        return new CategoriaDTO(
            categoria.id,
            categoria.nome,
            categoria.descricao,
            leiloesVinculados
        );
    }
    
    /**
     * Converte este DTO em uma entidade Categoria.
     * 
     * @return Uma nova instância de Categoria com os dados deste DTO.
     */
    public Leilao.Categoria paraEntidade() {
        Leilao.Categoria categoria = new Leilao.Categoria();
        categoria.nome = this.nome;
        categoria.descricao = this.descricao;
        return categoria;
    }
    
    /**
     * Cria uma lista de DTOs a partir de uma lista de entidades.
     * 
     * @param categorias Lista de entidades Categoria
     * @return Lista de CategoriaDTO
     */
    public static List<CategoriaDTO> converterLista(List<Leilao.Categoria> categorias) {
        return categorias.stream()
            .map(CategoriaDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
