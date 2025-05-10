package service;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import model.AreaAtuacao;
import model.FormaPagamento;
import model.Usuario;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Serviço responsável pela inicialização de dados do sistema.
 * Separa a lógica de inicialização de dados da lógica de configuração da aplicação.
 */
@ApplicationScoped
public class DataInitializationService {
    
    private static final Logger LOGGER = Logger.getLogger(DataInitializationService.class.getName());
    
    /**
     * Inicializa dados de desenvolvimento quando a aplicação é iniciada em modo DEV.
     */
    @Transactional
    public void initDevelopmentData(@Observes StartupEvent evt) {
        try {
            // Apenas em modo de desenvolvimento
            if (LaunchMode.current() == LaunchMode.DEVELOPMENT) {
                LOGGER.info("Inicializando dados para ambiente de desenvolvimento");
                
                initAdminUser();
                initBusinessAreas();
                initPaymentMethods();
            }
        } catch (Exception e) {
            LOGGER.severe("Erro ao inicializar dados de desenvolvimento: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Inicializa o usuário administrador se não existir.
     */
    private void initAdminUser() {
        if (Usuario.count("email = ?1", "admin@rev3rso.com") == 0) {
            LOGGER.info("Criando usuário administrador padrão");
            
            Usuario admin = new Usuario();
            admin.razaoSocial = "Administrador";
            admin.nomeFantasia = "Admin REV3RSO";
            admin.email = "admin@rev3rso.com";
            // Em produção, usar senhas seguras e hash apropriado
            admin.senha = "admin123";  
            admin.cnpj = "00.000.000/0001-00";
            admin.endereco = "Av. Teste, 123";
            admin.cidade = "São Paulo";
            admin.uf = "SP";
            admin.cep = "00000-000";
            admin.telefone = "(11) 9999-9999";
            admin.tipoUsuario = Usuario.TipoUsuario.ADMINISTRADOR;
            admin.status = Usuario.Status.ATIVO;
            admin.dataCadastro = new Date();
            admin.persist();
        }
    }
    
    /**
     * Inicializa áreas de atuação padrão se não existirem.
     */
    private void initBusinessAreas() {
        String[] areas = {"Tecnologia", "Saúde", "Educação", "Financeiro", "Varejo"};
        String[] descriptions = {
            "Serviços e produtos de tecnologia",
            "Equipamentos e serviços de saúde",
            "Material didático e serviços educacionais",
            "Serviços financeiros e bancários",
            "Produtos e serviços para comércio varejista"
        };
        
        for (int i = 0; i < areas.length; i++) {
            if (AreaAtuacao.count("descricao = ?1", areas[i]) == 0) {
                LOGGER.info("Criando área de atuação: " + areas[i]);
                
                AreaAtuacao area = new AreaAtuacao();
                area.nome = areas[i];
                area.descricao = areas[i];
                area.detalhes = descriptions[i];
                area.persist();
            }
        }
    }
    
    /**
     * Inicializa formas de pagamento padrão se não existirem.
     */
    private void initPaymentMethods() {
        String[] methods = {"Boleto", "Cartão de Crédito", "Transferência Bancária", "PIX"};
        
        for (String method : methods) {
            if (FormaPagamento.count("descricao = ?1", method) == 0) {
                LOGGER.info("Criando forma de pagamento: " + method);
                
                FormaPagamento payment = new FormaPagamento();
                payment.descricao = method;
                payment.persist();
            }
        }
    }
}
