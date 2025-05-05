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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "mensagens")
public class Mensagem extends PanacheEntity {
    
    public enum Tipo {
        PERGUNTA_FORNECEDOR,
        PERGUNTA_COMPRADOR,
        RESPOSTA_FORNECEDOR,
        RESPOSTA_COMPRADOR
    }
    
    @ManyToOne
    @JoinColumn(name = "leilao_id", nullable = false)
    @NotNull(message = "O leilão é obrigatório")
    public Leilao leilao;
    
    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    @NotNull(message = "O autor é obrigatório")
    public Usuario autor;
    
    @NotBlank(message = "O conteúdo da mensagem é obrigatório")
    @Column(columnDefinition = "TEXT", nullable = false)
    public String conteudo;
    
    @Column(name = "data_criacao", nullable = false)
    public Date dataCriacao;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    public Tipo tipo;
    
    @Column(name = "respondida")
    public boolean respondida = false;
    
    @ManyToOne
    @JoinColumn(name = "mensagem_pai_id")
    public Mensagem mensagemPai;
    
    public Mensagem() {
        this.dataCriacao = new Date();
    }
    
    public Mensagem(Leilao leilao, Usuario autor, String conteudo) {
        this();
        this.leilao = leilao;
        this.autor = autor;
        this.conteudo = conteudo;
    }
    
    // Adicionar método para buscar mensagens por leilão
    public static List<Mensagem> buscarPorLeilao(Leilao leilao) {
        return list("leilao = ?1 ORDER BY dataCriacao", leilao);
    }
    
    // Métodos estáticos para consultas frequentes
    public static List<Mensagem> listarPorLeilao(Leilao leilao) {
        return list("leilao = ?1 ORDER BY dataCriacao ASC", leilao);
    }
    
    public static List<Mensagem> listarPerguntasPorLeilao(Leilao leilao) {
        return list("leilao = ?1 AND (tipo = 'PERGUNTA_FORNECEDOR' OR tipo = 'PERGUNTA_COMPRADOR') ORDER BY dataCriacao ASC", leilao);
    }
    
    public static List<Mensagem> listarMensagensPorAutor(Leilao leilao, Usuario autor) {
        return list("leilao = ?1 AND autor = ?2 ORDER BY dataCriacao ASC", leilao, autor);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mensagem mensagem = (Mensagem) o;
        return Objects.equals(id, mensagem.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
