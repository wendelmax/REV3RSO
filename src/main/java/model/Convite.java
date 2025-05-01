package model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "convites")
public class Convite extends PanacheEntity {
    
    public enum Status {
        PENDENTE,
        ACEITO,
        RECUSADO
    }
    
    @ManyToOne
    @JoinColumn(name = "leilao_id", nullable = false)
    @NotNull(message = "O leilão é obrigatório")
    public Leilao leilao;
    
    @ManyToOne
    @JoinColumn(name = "fornecedor_id", nullable = false)
    @NotNull(message = "O fornecedor é obrigatório")
    public Usuario fornecedor;
    
    @Column(name = "data_envio", nullable = false)
    public Date dataEnvio;
    
    @Column(name = "data_resposta")
    public Date dataResposta;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status status;
    
    public Convite() {
        this.dataEnvio = new Date();
        this.status = Status.PENDENTE;
    }
    
    public Convite(Leilao leilao, Usuario fornecedor) {
        this();
        this.leilao = leilao;
        this.fornecedor = fornecedor;
    }
    
    // Métodos estáticos para consultas frequentes
    public static List<Convite> listarPorLeilao(Leilao leilao) {
        return list("leilao", leilao);
    }
    
    public static List<Convite> listarPorFornecedor(Usuario fornecedor) {
        return list("fornecedor = ?1 ORDER BY dataEnvio DESC", fornecedor);
    }
    
    public static List<Convite> listarConvitesPendentes(Usuario fornecedor) {
        return list("fornecedor = ?1 AND status = ?2 ORDER BY dataEnvio DESC", 
                   fornecedor, Status.PENDENTE);
    }
    
    public static boolean existeConvite(Leilao leilao, Usuario fornecedor) {
        return count("leilao = ?1 AND fornecedor = ?2", leilao, fornecedor) > 0;
    }
    
    // Métodos para aceitar ou recusar convites
    public void aceitar() {
        this.status = Status.ACEITO;
        this.dataResposta = new Date();
        this.persist();
    }
    
    public void recusar() {
        this.status = Status.RECUSADO;
        this.dataResposta = new Date();
        this.persist();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Convite convite = (Convite) o;
        return Objects.equals(id, convite.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
