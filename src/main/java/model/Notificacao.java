package model;

import java.util.Date;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Entidade que representa uma notificação no sistema.
 */
@Entity
public class Notificacao extends PanacheEntity {
    
    /**
     * Usuário destinatário da notificação.
     */
    @ManyToOne(optional = false)
    public Usuario usuario;
    
    /**
     * Título da notificação.
     */
    @Column(nullable = false)
    public String titulo;
    
    /**
     * Mensagem da notificação.
     */
    @Column(nullable = false, length = 1000)
    public String mensagem;
    
    /**
     * Link para redirecionamento.
     */
    @Column
    public String link;
    
    /**
     * Data de envio da notificação.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date dataEnvio;
    
    /**
     * Indica se a notificação foi lida.
     */
    @Column(nullable = false)
    public boolean lida;
    
    /**
     * Data em que a notificação foi lida.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    public Date dataLeitura;
    
    /**
     * Tipo de notificação.
     */
    @Column
    public TipoNotificacao tipo = TipoNotificacao.SISTEMA;
    
    /**
     * Enumeration com os tipos de notificação.
     */
    public enum TipoNotificacao {
        /**
         * Notificação do sistema.
         */
        SISTEMA,
        
        /**
         * Notificação de mensagem.
         */
        MENSAGEM,
        
        /**
         * Notificação de leilão.
         */
        LEILAO,
        
        /**
         * Notificação de proposta.
         */
        PROPOSTA,
        
        /**
         * Notificação de usuário.
         */
        USUARIO
    }
}
