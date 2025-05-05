package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidade que representa um usuário do sistema.
 * Pode ser comprador, fornecedor ou administrador.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@ToString(exclude = {"leiloesCriados", "lances", "avaliacoesRecebidas", "avaliacoesRealizadas"})
public class Usuario extends PanacheEntity {
    
    public enum TipoUsuario {
        COMPRADOR,
        FORNECEDOR,
        ADMINISTRADOR
    }
    
    public enum Status {
        ATIVO,
        SUSPENSO,
        INATIVO
    }
    
    @NotBlank(message = "Razão social é obrigatória")
    @Column(name = "razao_social", nullable = false)
    public String razaoSocial;
    
    @NotBlank(message = "Nome fantasia é obrigatório")
    @Column(name = "nome_fantasia", nullable = false)
    public String nomeFantasia;
    
    @NotBlank(message = "CNPJ é obrigatório")
    @Column(unique = true, nullable = false)
    public String cnpj;
    
    @NotBlank(message = "Endereço é obrigatório")
    public String endereco;
    
    @NotBlank(message = "Cidade é obrigatória")
    public String cidade;
    
    @NotBlank(message = "UF é obrigatória")
    public String uf;
    
    @NotBlank(message = "CEP é obrigatório")
    public String cep;
    
    @NotBlank(message = "Telefone é obrigatório")
    public String telefone;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Column(unique = true, nullable = false)
    public String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Column(nullable = false)
    public String senha;
    
    // Relacionamento com áreas de atuação (para fornecedores)
    @ManyToMany
    @JoinTable(
        name = "usuario_areas",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "area_id")
    )
    public List<AreaAtuacao> areasAtuacao = new ArrayList<>();
    
    // Motivo da suspensão, se aplicável
    @Column(name = "motivo_suspensao")
    public String motivoSuspensao;
    
    // Data da última suspensão
    @Column(name = "data_suspensao")
    public Date dataSuspensao;
    
    // Data da última reativação
    @Column(name = "data_reativacao")
    public Date dataReativacao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TipoUsuario tipoUsuario;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status status;
    
    public double pontuacao;
    
    @Column(name = "total_avaliacoes")
    public long totalAvaliacoes;
    
    @Column(name = "data_cadastro")
    public Date dataCadastro;
    
    @Column(name = "ultima_atualizacao")
    public Date ultimaAtualizacao;
    
    @Column(name = "ultimo_acesso")
    public Date ultimoAcesso;
    
    @OneToMany(mappedBy = "criador")
    public List<Leilao> leiloesCriados;
    
    @OneToMany(mappedBy = "fornecedor")
    public List<Lance> lances;
    
    @OneToMany(mappedBy = "avaliado")
    public List<Avaliacao> avaliacoesRecebidas;
    
    @OneToMany(mappedBy = "avaliador")
    public List<Avaliacao> avaliacoesRealizadas;
    
    /**
     * Construtor padrão com inicialização de campos essenciais.
     */
    public Usuario() {
        this.dataCadastro = new Date();
        this.status = Status.ATIVO;
        this.pontuacao = 0;
        this.totalAvaliacoes = 0;
    }
    
    // Métodos estáticos para consultas frequentes
    public static List<Usuario> listarFornecedoresAtivos() {
        return list("tipoUsuario = ?1 and status = ?2", 
                   TipoUsuario.FORNECEDOR, Status.ATIVO);
    }
    
    public static Usuario encontrarPorEmail(String email) {
        return find("email", email).firstResult();
    }
    
    public static Usuario encontrarPorCnpj(String cnpj) {
        return find("cnpj", cnpj).firstResult();
    }
    
    public static List<Usuario> encontrarPorAreaAtuacao(Long areaId) {
        return list("select u from Usuario u join u.areasAtuacao a where a.id = ?1 and u.status = ?2", 
                   areaId, Status.ATIVO);
    }
    
    // Métodos de instância para operações comuns
    public void suspender(String motivo) {
        this.status = Status.SUSPENSO;
        this.motivoSuspensao = motivo;
        this.dataSuspensao = new Date();
        this.persist();
    }
    
    public void reativar() {
        this.status = Status.ATIVO;
        this.dataReativacao = new Date();
        this.persist();
    }
    
    public void atualizarPontuacao(double delta) {
        this.pontuacao += delta;
        this.persist();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
