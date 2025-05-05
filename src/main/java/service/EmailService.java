package service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.Convite;
import model.Leilao;
import model.Usuario;

/**
 * Serviço responsável pelo envio de emails no sistema.
 * Utiliza o serviço de email do Quarkus para envio das mensagens.
 */
@ApplicationScoped
public class EmailService {
    
    @Inject
    Mailer mailer;
    
    /**
     * Envia um email de notificação sobre um convite para participar de um leilão.
     * 
     * @param convite O convite a ser notificado
     */
    public void enviarEmailConvite(Convite convite) {
        if (convite == null || convite.fornecedor == null || convite.leilao == null) {
            return;
        }
        
        Usuario fornecedor = convite.fornecedor;
        Leilao leilao = convite.leilao;
        
        String assunto = "Convite para participar do leilão: " + leilao.titulo;
        String corpo = String.format(
            "Olá %s,\n\n" +
            "Você foi convidado a participar do leilão \"%s\" que ocorrerá de %tF a %tF.\n\n" +
            "Para mais detalhes e enviar seus lances, acesse a plataforma REV3RSO.\n\n" +
            "Atenciosamente,\n" +
            "Equipe REV3RSO",
            fornecedor.nomeFantasia,
            leilao.titulo,
            leilao.dataInicio,
            leilao.dataFim
        );
        
        enviarEmail(fornecedor.email, assunto, corpo);
    }
    
    /**
     * Envia um email de notificação sobre a aprovação de um fornecedor no sistema.
     * 
     * @param fornecedor O fornecedor aprovado
     * @param senha A senha temporária gerada para o fornecedor
     */
    public void enviarEmailAprovacaoFornecedor(Usuario fornecedor, String senha) {
        if (fornecedor == null) {
            return;
        }
        
        String assunto = "Bem-vindo ao REV3RSO - Fornecedor Aprovado";
        String corpo = String.format(
            "Olá %s,\n\n" +
            "Temos o prazer de informar que seu cadastro como fornecedor foi aprovado no sistema REV3RSO.\n\n" +
            "Seus dados de acesso são:\n" +
            "Email: %s\n" +
            "Senha temporária: %s\n\n" +
            "Recomendamos que altere sua senha após o primeiro acesso.\n\n" +
            "Atenciosamente,\n" +
            "Equipe REV3RSO",
            fornecedor.nomeFantasia,
            fornecedor.email,
            senha
        );
        
        enviarEmail(fornecedor.email, assunto, corpo);
    }
    
    /**
     * Envia um email de notificação sobre a recuperação de senha.
     * 
     * @param usuario O usuário que solicitou a recuperação
     * @param token O token de recuperação
     */
    public void enviarEmailRecuperacaoSenha(Usuario usuario, String token) {
        if (usuario == null) {
            return;
        }
        
        String assunto = "REV3RSO - Recuperação de Senha";
        String corpo = String.format(
            "Olá %s,\n\n" +
            "Recebemos uma solicitação de recuperação de senha para sua conta.\n\n" +
            "Para definir uma nova senha, acesse o link:\n" +
            "http://localhost:8080/recuperar-senha/%s\n\n" +
            "Se você não solicitou esta recuperação, por favor ignore este email.\n\n" +
            "Atenciosamente,\n" +
            "Equipe REV3RSO",
            usuario.nomeFantasia,
            token
        );
        
        enviarEmail(usuario.email, assunto, corpo);
    }
    
    /**
     * Envia um email de notificação sobre o encerramento de um leilão.
     * 
     * @param leilao O leilão encerrado
     * @param vencedor O fornecedor vencedor, se houver
     */
    public void enviarEmailEncerramentoLeilao(Leilao leilao, Usuario vencedor) {
        if (leilao == null || leilao.criador == null) {
            return;
        }
        
        // Email para o criador do leilão
        String assuntoComprador = "Seu leilão foi encerrado: " + leilao.titulo;
        String corpoComprador = String.format(
            "Olá %s,\n\n" +
            "Seu leilão \"%s\" foi encerrado.\n\n" +
            "%s\n\n" + 
            "Acesse a plataforma REV3RSO para mais detalhes.\n\n" +
            "Atenciosamente,\n" +
            "Equipe REV3RSO",
            leilao.criador.nomeFantasia,
            leilao.titulo,
            vencedor != null ? 
                "O fornecedor vencedor foi " + vencedor.nomeFantasia + " com lance de R$ " + leilao.valorVencedor :
                "Não houveram lances para este leilão."
        );
        
        enviarEmail(leilao.criador.email, assuntoComprador, corpoComprador);
        
        // Email para o fornecedor vencedor, se houver
        if (vencedor != null) {
            String assuntoFornecedor = "Parabéns! Você venceu o leilão: " + leilao.titulo;
            String corpoFornecedor = String.format(
                "Olá %s,\n\n" +
                "Parabéns! Você venceu o leilão \"%s\" com o lance de R$ %s.\n\n" +
                "O comprador entrará em contato para finalizar a negociação.\n\n" +
                "Atenciosamente,\n" +
                "Equipe REV3RSO",
                vencedor.nomeFantasia,
                leilao.titulo,
                leilao.valorVencedor
            );
            
            enviarEmail(vencedor.email, assuntoFornecedor, corpoFornecedor);
        }
    }
    
    /**
     * Método para envio de emails.
     *
     * @param destinatario Email do destinatário
     * @param assunto Assunto do email
     * @param corpo Corpo do email
     */
    public void enviarEmail(String destinatario, String assunto, String corpo) {
        try {
            mailer.send(Mail.withText(
                destinatario,
                assunto,
                corpo
            ));
        } catch (Exception e) {
            // Log do erro, mas não falha a operação
            System.err.println("Erro ao enviar email: " + e.getMessage());
        }
    }
}
