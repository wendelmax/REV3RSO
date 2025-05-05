package model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "lances")
public class Lance extends PanacheEntity {
    
    @ManyToOne
    @JoinColumn(name = "leilao_id", nullable = false)
    @NotNull(message = "O leilão é obrigatório")
    public Leilao leilao;
    
    @ManyToOne
    @JoinColumn(name = "fornecedor_id", nullable = false)
    @NotNull(message = "O fornecedor é obrigatório")
    public Usuario fornecedor;
    
    @Column(nullable = false)
    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    public BigDecimal valor;
    
    @Column(name = "data_criacao", nullable = false)
    public Date dataCriacao;
    
    @Column(name = "data_atualizacao")
    public Date dataAtualizacao;
    
    @Column(name = "data_cancelamento")
    public Date dataCancelamento;
    
    @Column(name = "cancelado")
    public boolean cancelado = false;
    
    @Column(name = "motivo_cancelamento")
    public String motivoCancelamento;
    
    @Column(name = "condicoes_entrega")
    public String condicoesEntrega;
    
    @Column(name = "prazo_entrega")
    public Integer prazoEntrega;
    
    @Column(name = "prazo_pagamento")
    public Integer prazoPagamento;
    
    @Column(name = "vencedor")
    public boolean vencedor = false;
    
    public Lance() {
        this.dataCriacao = new Date();
    }
    
    // Constructor with required fields
    public Lance(Leilao leilao, Usuario fornecedor, BigDecimal valor) {
        this();
        this.leilao = leilao;
        this.fornecedor = fornecedor;
        this.valor = valor;
    }
    
    // Métodos estáticos para consultas frequentes
    public static Lance menorLanceDoLeilao(Leilao leilao) {
        return find("leilao = ?1 ORDER BY valor ASC", leilao).firstResult();
    }
    
    public static Lance ultimoLanceDoFornecedor(Leilao leilao, Usuario fornecedor) {
        return find("leilao = ?1 AND fornecedor = ?2 ORDER BY dataCriacao DESC", leilao, fornecedor).firstResult();
    }
    
    public static long contarLancesPorLeilao(Leilao leilao) {
        return count("leilao", leilao);
    }
    
    // Helper methods
    public boolean isMenor(Lance outro) {
        if (outro == null) return true;
        return this.valor.compareTo(outro.valor) < 0;
    }
    
    public boolean isValido(Leilao leilao, BigDecimal ultimoMenorValor) {
        if (leilao == null || !leilao.status.equals(Leilao.Status.ABERTO)) {
            return false;
        }
        
        // Verifica se o lance é menor que o último menor valor
        if (ultimoMenorValor != null && this.valor.compareTo(ultimoMenorValor) >= 0) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lance lance = (Lance) o;
        return Objects.equals(id, lance.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    /**
     * Verifica se este lance é o vencedor do leilão.
     * 
     * @return true se este lance for o vencedor, false caso contrário
     */
    public boolean isVencedor() {
        return this.vencedor;
    }
    
    /**
     * Define este lance como vencedor do leilão.
     * Isso deve ser chamado apenas ao encerrar um leilão.
     */
    public void definirComoVencedor() {
        this.vencedor = true;
        this.persist();
    }
}
