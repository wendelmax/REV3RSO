package service;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import model.AreaAtuacao;
import model.FormaPagamento;
import model.Usuario;
import model.Usuario.TipoUsuario;
import model.Usuario.Status;
import io.quarkus.runtime.configuration.ProfileManager;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Serviço responsável pela inicialização de dados do sistema.
 * Separa a lógica de inicialização de dados da lógica de configuração da aplicação.
 */
@ApplicationScoped
public class DataInitializationService {
    
    private static final Logger LOGGER = Logger.getLogger(DataInitializationService.class.getName());
    
    @Inject
    UsuarioService usuarioService;
    
    /**
     * Inicializa dados de desenvolvimento quando a aplicação é iniciada em modo de desenvolvimento.
     */
    void onStart(@Observes StartupEvent ev) {
        if (LaunchMode.current() == LaunchMode.DEVELOPMENT) {
            initDevelopmentData();
        }
    }
    
    /**
     * Inicializa dados para ambiente de desenvolvimento.
     */
    @Transactional
    public void initDevelopmentData() {
        try {
            System.out.println("Inicializando dados para ambiente de desenvolvimento");
            
            // Inicializa usuário administrador
            initAdminUser();
            
            // Inicializa áreas de atuação
            initBusinessAreas();
            
        } catch (Exception e) {
            System.err.println("Erro ao inicializar dados de desenvolvimento: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Inicializa o usuário administrador padrão se não existir.
     */
    private void initAdminUser() {
        try {
            System.out.println("Criando usuário administrador padrão");
            
            // Verifica se já existe um usuário admin
            if (Usuario.encontrarPorEmail("admin@rev3rso.com") == null) {
                Usuario admin = new Usuario();
                admin.nome = "Administrador REV3RSO";
                admin.razaoSocial = "Administrador REV3RSO";
                admin.nomeFantasia = "Admin REV3RSO";
                admin.email = "admin@rev3rso.com";
                admin.senha = "admin123"; // Senha temporária
                admin.tipoUsuario = TipoUsuario.ADMINISTRADOR;
                admin.status = Status.ATIVO;
                admin.cnpj = "00.000.000/0001-00";
                admin.endereco = "Av. Teste, 123";
                admin.cidade = "São Paulo";
                admin.uf = "SP";
                admin.cep = "00000-000";
                admin.telefone = "(11) 9999-9999";
                admin.dataCadastro = new Date();
                admin.ultimaAtualizacao = new Date();
                
                admin.persist();
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar usuário administrador: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Inicializa as áreas de atuação padrão.
     */
    private void initBusinessAreas() {
        try {
            // Lista de áreas de atuação padrão
            List<String> areas = List.of(
                "Tecnologia",
                "Construção Civil",
                "Serviços Gerais",
                "Material de Escritório",
                "Mobiliário"
            );
            
            // Cria cada área se não existir
            for (String area : areas) {
                if (AreaAtuacao.find("descricao", area).firstResult() == null) {
                    System.out.println("Criando área de atuação: " + area);
                    AreaAtuacao novaArea = new AreaAtuacao();
                    novaArea.nome = area;
                    novaArea.descricao = area;
                    novaArea.persist();
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar áreas de atuação: " + e.getMessage());
            e.printStackTrace();
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
