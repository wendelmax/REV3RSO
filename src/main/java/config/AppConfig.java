package config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.logging.Logger;

/**
 * Configuração central da aplicação.
 * Responsável por inicializar configurações e registrar componentes necessários.
 * 
 * Nota: A inicialização de dados está em {@link service.DataInitializationService}
 */
@ApplicationScoped
public class AppConfig {
    
    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
    
    @Inject
    @ConfigProperty(name = "app.name", defaultValue = "REV3RSO")
    String appName;
    
    @Inject
    @ConfigProperty(name = "app.version", defaultValue = "1.0.0")
    String appVersion;
    
    /**
     * Método executado na inicialização da aplicação.
     * Registra informações sobre a aplicação e inicializa configurações necessárias.
     * 
     * @param event Evento de inicialização
     */
    public void onStart(@Observes StartupEvent event) {
        LOGGER.info("Iniciando aplicação: " + appName + " v" + appVersion);
        
        // Configurações e registros de componentes
        initializeApplicationSettings();
    }
    
    /**
     * Inicializa configurações da aplicação.
     * Nota: Carga de dados é responsabilidade do DataInitializationService
     */
    private void initializeApplicationSettings() {
        LOGGER.info("Inicializando configurações da aplicação...");
        
        // Aqui ficam apenas configurações relacionadas à infraestrutura da aplicação
        // e não dados de negócio
    }
    
    /**
     * Obtém o nome da aplicação.
     * 
     * @return Nome da aplicação
     */
    public String getAppName() {
        return appName;
    }
    
    /**
     * Obtém a versão da aplicação.
     * 
     * @return Versão da aplicação
     */
    public String getAppVersion() {
        return appVersion;
    }
}
