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
 * Representa uma forma de pagamento que pode ser utilizada nos leilões.
 * Baseado no modelo de dados original do REV3RSO.
 */
@Entity
@Table(name = "formas_pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"leiloes"})
public class FormaPagamento extends PanacheEntity {
    
    @Column(name = "descricao", nullable = false, unique = true)
    public String descricao;
    
    // Relacionamento com leilões
    @OneToMany(mappedBy = "formaPagamento")
    public List<Leilao> leiloes = new ArrayList<>();
    
    /**
     * Construtor com parâmetros para criação.
     * 
     * @param descricao Descrição da forma de pagamento
     */
    public FormaPagamento(String descricao) {
        this.descricao = descricao;
    }
    
    // Métodos estáticos para consultas
    
    /**
     * Busca uma forma de pagamento por descrição
     * @param descricao a descrição da forma de pagamento
     * @return a forma de pagamento encontrada ou null
     */
    public static FormaPagamento buscarPorDescricao(String descricao) {
        return find("descricao", descricao).firstResult();
    }
    
    /**
     * Lista todas as formas de pagamento ordenadas por descrição
     * @return lista de formas de pagamento
     */
    public static List<FormaPagamento> listarOrdenado() {
        return list("order by descricao");
    }
}
