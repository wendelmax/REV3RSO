package model;

import java.util.Date;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidade que representa um anexo de um leilu00e3o.
 * Pode ser um arquivo de especificau00e7u00e3o, imagem ou qualquer outro documento relevante.
 */
@Entity
@Table(name = "anexos")
public class Anexo extends PanacheEntity {
    
    @Column(nullable = false)
    public String nome;
    
    @Column(nullable = false)
    public String caminhoArquivo;
    
    @Column(name = "tipo_arquivo")
    public String tipoArquivo;
    
    @Column
    public long tamanho;
    
    @Column(name = "data_upload", nullable = false)
    public Date dataUpload;
    
    @ManyToOne
    @JoinColumn(name = "leilao_id", nullable = false)
    public Leilao leilao;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    public Usuario usuario;
    
    public Anexo() {
        this.dataUpload = new Date();
    }
    
    public Anexo(String nome, String caminhoArquivo, String tipoArquivo, long tamanho, Leilao leilao, Usuario usuario) {
        this();
        this.nome = nome;
        this.caminhoArquivo = caminhoArquivo;
        this.tipoArquivo = tipoArquivo;
        this.tamanho = tamanho;
        this.leilao = leilao;
        this.usuario = usuario;
    }
    
    // Mu00e9todos estu00e1ticos para consultas frequentes
    public static java.util.List<Anexo> listarPorLeilao(Leilao leilao) {
        return list("leilao", leilao);
    }
}
