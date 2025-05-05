package dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import model.Usuario;
import model.Leilao;
import model.AreaAtuacao;

/**
 * DTO (Data Transfer Object) para a entidade Usuario.
 * Utilizado para transferência de dados entre camadas, sem expor a entidade diretamente.
 * Implementado como record para garantir imutabilidade e reduzir código boilerplate.
 */
public record UsuarioDTO(
    Long id,
    String razaoSocial,
    String nomeFantasia,
    String cnpj,
    String endereco,
    String cidade,
    String uf,
    String cep,
    String telefone,
    String email,
    String tipoUsuario,
    String status,
    double pontuacao,
    Date dataCadastro,
    List<AreaAtuacaoResumo> areasAtuacao,
    int leiloesAtivos,
    int propostas
) {
    
    /**
     * Classe interna para resumo de área com estatísticas
     */
    public record AreaAtuacaoResumo(
        Long id,
        String descricao,
        int totalFornecedores,
        boolean popular  // indicador se é uma área popular
    ) {
        public static AreaAtuacaoResumo fromEntity(AreaAtuacao area) {
            return new AreaAtuacaoResumo(
                area.id,
                area.descricao,
                area.fornecedores.size(),
                area.fornecedores.size() > 5  // exemplo de regra de negócio: popular se tiver mais de 5 fornecedores
            );
        }
    }
    
    /**
     * Construtor que converte uma entidade Usuario em DTO.
     * 
     * @param usuario A entidade a ser convertida.
     */
    public static UsuarioDTO fromEntity(Usuario usuario) {
        // Mapear áreas de atuação com estatísticas
        List<AreaAtuacaoResumo> areasAtuacao = null;
        if (usuario.areasAtuacao != null) {
            areasAtuacao = usuario.areasAtuacao.stream()
                .map(AreaAtuacaoResumo::fromEntity)
                .collect(Collectors.toList());
        }
        
        // Contar leilões ativos (para compradores)
        int leiloesAtivos = 0;
        if (usuario.leiloesCriados != null) {
            leiloesAtivos = (int) usuario.leiloesCriados.stream()
                .filter(l -> l.status == Leilao.Status.ABERTO)
                .count();
        }
        
        // Contar propostas (para fornecedores)
        int propostas = 0;
        if (usuario.lances != null) {
            propostas = usuario.lances.size();
        }
        
        return new UsuarioDTO(
            usuario.id,
            usuario.razaoSocial,
            usuario.nomeFantasia,
            usuario.cnpj,
            usuario.endereco,
            usuario.cidade,
            usuario.uf,
            usuario.cep,
            usuario.telefone,
            usuario.email,
            usuario.tipoUsuario != null ? usuario.tipoUsuario.name() : null,
            usuario.status != null ? usuario.status.name() : null,
            usuario.pontuacao,
            usuario.dataCadastro,
            areasAtuacao,
            leiloesAtivos,
            propostas
        );
    }
    
    /**
     * Converte este DTO em uma entidade Usuario para cadastro.
     * Observação: Este método deve ser usado apenas para novos usuários,
     * pois não preenche todos os campos da entidade.
     * 
     * @return Uma nova instância de Usuario com os dados deste DTO.
     */
    public Usuario paraEntidade() {
        Usuario usuario = new Usuario();
        usuario.razaoSocial = this.razaoSocial;
        usuario.nomeFantasia = this.nomeFantasia;
        usuario.cnpj = this.cnpj;
        usuario.endereco = this.endereco;
        usuario.cidade = this.cidade;
        usuario.uf = this.uf;
        usuario.cep = this.cep;
        usuario.telefone = this.telefone;
        usuario.email = this.email;
        
        // Outros campos como senha, tipo, status, devem ser preenchidos pelo serviço
        return usuario;
    }
    
    /**
     * Cria uma lista de DTOs a partir de uma lista de entidades.
     * 
     * @param usuarios Lista de entidades Usuario
     * @return Lista de UsuarioDTO
     */
    public static List<UsuarioDTO> converterLista(List<Usuario> usuarios) {
        return usuarios.stream()
            .map(UsuarioDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Versão simplificada do DTO para listagens.
     */
    public record Resumo(
        Long id,
        String nome,
        String email,
        String tipo,
        String status
    ) {
        /**
         * Cria um DTO resumido a partir de uma entidade Usuario.
         * 
         * @param usuario Entidade a ser convertida
         * @return DTO resumido
         */
        public static Resumo fromEntity(Usuario usuario) {
            return new Resumo(
                usuario.id,
                usuario.nomeFantasia,
                usuario.email,
                usuario.tipoUsuario != null ? usuario.tipoUsuario.name() : null,
                usuario.status != null ? usuario.status.name() : null
            );
        }
        
        /**
         * Converte uma lista de usuários em uma lista de DTOs resumidos.
         * 
         * @param usuarios Lista de entidades Usuario
         * @return Lista de DTOs resumidos
         */
        public static List<Resumo> converterLista(List<Usuario> usuarios) {
            return usuarios.stream()
                .map(Resumo::fromEntity)
                .collect(Collectors.toList());
        }
    }
}
