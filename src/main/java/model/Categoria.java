package model;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Representa uma categoria de leilão no sistema.
 * Permite categorizar e filtrar leilões por tipo.
 */
@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"leiloes"})
public class Categoria extends PanacheEntity {
    
    @Column(nullable = false, unique = true)
    public String nome;
    
    @Column
    public String descricao;
    
    // Relacionamento com leilões
    @OneToMany(mappedBy = "categoria")
    public List<Leilao> leiloes = new ArrayList<>();
    
    /**
     * Construtor com parâmetros para criação.
     * 
     * @param nome Nome da categoria
     * @param descricao Descrição da categoria
     */
    public Categoria(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }
    
    // Métodos estáticos para consultas
    
    /**
     * Busca uma categoria por nome
     * @param nome o nome da categoria
     * @return a categoria encontrada ou null
     */
    public static Categoria buscarPorNome(String nome) {
        return find("nome", nome).firstResult();
    }
    
    /**
     * Lista todas as categorias ordenadas por nome
     * @return lista de categorias
     */
    public static List<Categoria> listarOrdenado() {
        return list("order by nome");
    }
    
    // Sobrescrita de métodos
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Categoria)) {
            return false;
        }
        Categoria other = (Categoria) obj;
        return this.id != null && this.id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
