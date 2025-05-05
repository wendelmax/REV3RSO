package model;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Representa uma área de atuação para fornecedores no sistema.
 * Permite categorizar e filtrar fornecedores por especialidade.
 */
@Entity
@Table(name = "areas_atuacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"fornecedores"})
public class AreaAtuacao extends PanacheEntity {
    
    @Column(nullable = false, unique = true)
    public String descricao;
    
    @Column(columnDefinition = "TEXT")
    public String detalhes;
    
    // Relacionamento com usuários
    @ManyToMany(mappedBy = "areasAtuacao")
    public List<Usuario> fornecedores = new ArrayList<>();
    
    /**
     * Construtor com parâmetros para criação.
     * 
     * @param descricao Descrição da área de atuação
     */
    public AreaAtuacao(String descricao) {
        this.descricao = descricao;
    }
    
    // Métodos estáticos para consultas
    
    /**
     * Busca uma área de atuação por descrição
     * @param descricao a descrição da área
     * @return a área de atuação encontrada ou null
     */
    public static AreaAtuacao buscarPorDescricao(String descricao) {
        return find("descricao", descricao).firstResult();
    }
    
    /**
     * Lista todas as áreas de atuação ordenadas por descrição
     * @return lista de áreas de atuação
     */
    public static List<AreaAtuacao> listarOrdenado() {
        return list("order by descricao");
    }
    
    /**
     * Conta quantos fornecedores estão associados a esta área
     * @param areaId id da área
     * @return número de fornecedores
     */
    public static long contarFornecedores(Long areaId) {
        AreaAtuacao area = findById(areaId);
        if (area != null) {
            return area.fornecedores.size();
        }
        return 0;
    }
}
