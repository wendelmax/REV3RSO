package model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

/**
 * Entidade que representa um token de recuperação de senha.
 */
@Entity
@Table(name = "tokens_recuperacao")
public class TokenRecuperacao extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(nullable = false, unique = true)
    public String token;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    public Usuario usuario;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date dataCriacao;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date dataExpiracao;
    
    @Column(nullable = false)
    public boolean utilizado;
    
    @Temporal(TemporalType.TIMESTAMP)
    public Date dataUtilizacao;
    
    /**
     * Construtor padrão
     */
    public TokenRecuperacao() {
        this.dataCriacao = new Date();
        this.utilizado = false;
    }
    
    /**
     * Construtor com token e usuário
     * 
     * @param token Token de recuperação
     * @param usuario Usuário associado ao token
     * @param horasValidade Horas de validade do token
     */
    public TokenRecuperacao(String token, Usuario usuario, int horasValidade) {
        this();
        this.token = token;
        this.usuario = usuario;
        
        // Definir data de expiração (padrão: 24 horas)
        if (horasValidade <= 0) {
            horasValidade = 24;
        }
        
        Date dataExp = new Date();
        dataExp.setTime(dataExp.getTime() + (horasValidade * 60 * 60 * 1000));
        this.dataExpiracao = dataExp;
    }
}
