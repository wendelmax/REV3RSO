package model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "avaliacoes")
public class Avaliacao extends PanacheEntity {
    
    @ManyToOne
    @JoinColumn(name = "leilao_id", nullable = false)
    @NotNull(message = "O leilão é obrigatório")
    public Leilao leilao;
    
    @ManyToOne
    @JoinColumn(name = "avaliador_id", nullable = false)
    @NotNull(message = "O avaliador é obrigatório")
    public Usuario avaliador;
    
    @ManyToOne
    @JoinColumn(name = "avaliado_id", nullable = false)
    @NotNull(message = "O avaliado é obrigatório")
    public Usuario avaliado;
    
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    @Column(nullable = false)
    public int nota;
    
    @NotBlank(message = "O comentário é obrigatório")
    @Column(columnDefinition = "TEXT", nullable = false)
    public String comentario;
    
    @Column(columnDefinition = "TEXT")
    public String replica;
    
    @Column(name = "data_avaliacao", nullable = false)
    public Date dataAvaliacao;
    
    @Column(name = "data_replica")
    public Date dataReplica;
    
    public Avaliacao() {
        this.dataAvaliacao = new Date();
    }
    
    public Avaliacao(Leilao leilao, Usuario avaliador, Usuario avaliado, int nota, String comentario) {
        this();
        this.leilao = leilao;
        this.avaliador = avaliador;
        this.avaliado = avaliado;
        this.nota = nota;
        this.comentario = comentario;
    }
    
    // Métodos estáticos para consultas frequentes
    public static List<Avaliacao> listarPorAvaliado(Usuario avaliado) {
        return list("avaliado = ?1 ORDER BY dataAvaliacao DESC", avaliado);
    }
    
    public static List<Avaliacao> listarPorAvaliador(Usuario avaliador) {
        return list("avaliador = ?1 ORDER BY dataAvaliacao DESC", avaliador);
    }
    
    public static List<Avaliacao> listarPorLeilao(Leilao leilao) {
        return list("leilao = ?1 ORDER BY dataAvaliacao DESC", leilao);
    }
    
    public static boolean jaAvaliou(Leilao leilao, Usuario avaliador, Usuario avaliado) {
        return count("leilao = ?1 AND avaliador = ?2 AND avaliado = ?3", 
                    leilao, avaliador, avaliado) > 0;
    }
    
    public static double calcularMediaAvaliado(Usuario avaliado) {
        return find("avaliado", avaliado).stream()
               .mapToInt(a -> ((Avaliacao)a).nota)
               .average()
               .orElse(0.0);
    }
    
    // Método para adicionar réplica
    public void adicionarReplica(String replica) {
        this.replica = replica;
        this.dataReplica = new Date();
        this.persist();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avaliacao avaliacao = (Avaliacao) o;
        return Objects.equals(id, avaliacao.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
