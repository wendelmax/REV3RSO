package controller;

import io.smallrye.mutiny.Uni;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import util.RedirectUtil;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;

import model.Leilao;
import model.Usuario;
import model.Lance;
import model.FormaPagamento;
import model.Convite;
import service.NotificacaoService;
import service.LeilaoService;
import security.RequiresAuth;
import security.RequiresRole;
import dto.PaginatedResponse;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/leiloes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Leilões", description = "Operações relacionadas a leilões")
public class LeilaoController extends BaseController {
    
    @Inject
    NotificacaoService notificacaoService;
    
    @Inject
    LeilaoService leilaoService;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    
    @CheckedTemplate(basePath = "Leilao", requireTypeSafeExpressions = false)
    public static class Templates {
        public static native TemplateInstance index(List<Leilao> leiloes);
        public static native TemplateInstance criar();
        public static native TemplateInstance editar(Leilao leilao);
        public static native TemplateInstance visualizar(Leilao leilao, List<Lance> lances, boolean podeConvidar, boolean podeParticiplar);
        public static native TemplateInstance meusLeiloes(List<Leilao> leiloes);
        public static native TemplateInstance disponiveis(List<Leilao> leiloes);
        public static native TemplateInstance convidar(Leilao leilao, List<Usuario> fornecedores);
    }
    
    // Página inicial com leilões em destaque
    @Path("")
    public TemplateInstance index() {
        List<Leilao> leiloes = Leilao.listarLeiloesAbertos();
        return Templates.index(leiloes);
    }
    
    // Formulário para criar um novo leilão
    @Path("/criar")
    @RequiresAuth
    @RequiresRole(Usuario.TipoUsuario.COMPRADOR)
    public TemplateInstance criar() {
        return Templates.criar();
    }
    
    // Ação para salvar um novo leilão
    @POST
    @Path("/salvar")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @RequiresAuth
    @RequiresRole(Usuario.TipoUsuario.COMPRADOR)
    public Uni<String> salvar(
            @FormParam("titulo") @NotBlank String titulo,
            @FormParam("descricao") @NotBlank String descricao,
            @FormParam("dataInicio") @NotBlank String dataInicioStr,
            @FormParam("dataFim") @NotBlank String dataFimStr,
            @FormParam("tipoLeilao") @NotBlank String tipoLeilaoStr,
            @FormParam("tipoRequisicao") @NotBlank String tipoRequisicao,
            @FormParam("formaPagamento") @NotNull(message = "A forma de pagamento é obrigatória") Long formaPagamentoId,
            @FormParam("quantidade") Integer quantidade,
            @FormParam("unidadeMedida") String unidadeMedida,
            @FormParam("valorReferencia") String valorReferenciaStr) {
        
        if (validationFailed()) {
            flash("mensagem", "Por favor, corrija os erros no formulário");
            flash("tipo", "danger");
            return Uni.createFrom().item("criar");
        }
        
        Date dataInicio;
        Date dataFim;
        try {
            dataInicio = dateFormat.parse(dataInicioStr);
            dataFim = dateFormat.parse(dataFimStr);
        } catch (ParseException e) {
            flash("mensagem", "Formato de data inválido");
            flash("tipo", "danger");
            return Uni.createFrom().item("criar");
        }
        
        if (dataFim.before(dataInicio)) {
            flash("mensagem", "A data de término deve ser posterior à data de início");
            flash("tipo", "danger");
            return Uni.createFrom().item("criar");
        }
        
        // Criar o leilão
        Leilao leilao = new Leilao();
        leilao.titulo = titulo;
        leilao.descricao = descricao;
        leilao.dataInicio = dataInicio;
        leilao.dataFim = dataFim;
        leilao.tipoLeilao = Leilao.TipoLeilao.valueOf(tipoLeilaoStr);
        leilao.tipoRequisicao = tipoRequisicao;
        FormaPagamento formaPagamento = FormaPagamento.findById(formaPagamentoId);
        if (formaPagamento == null) {
            flash("mensagem", "Forma de pagamento não encontrada");
            flash("tipo", "danger");
            return Uni.createFrom().item("criar");
        }
        leilao.formaPagamento = formaPagamento;
        leilao.quantidade = quantidade;
        leilao.unidadeMedida = unidadeMedida;
        leilao.criador = usuarioLogado();
        
        if (valorReferenciaStr != null && !valorReferenciaStr.isEmpty()) {
            try {
                leilao.valorReferencia = new BigDecimal(valorReferenciaStr.replace(",", "."));
            } catch (NumberFormatException e) {
                // Ignorar se o valor não for válido
            }
        }
        
        leilao.persist();
        
        flash("mensagem", "Leilão criado com sucesso!");
        flash("tipo", "success");
        
        // Se for leilão fechado, redirecionar para página de convite
        if (leilao.tipoLeilao == Leilao.TipoLeilao.FECHADO) {
            return Uni.createFrom().item("convidar/" + leilao.id);
        } else {
            // Se for leilão aberto, já publicar
            leilao.publicar();
            return Uni.createFrom().item("visualizar/" + leilao.id);
        }
    }
    
    // Visualizar um leilão específico
    @Path("/visualizar/{id}")
    public TemplateInstance visualizar(@PathParam("id") Long id) {
        Leilao leilao = Leilao.findById(id);
        if (leilao == null) {
            flash("mensagem", "Leilão não encontrado");
            flash("tipo", "danger");
            return index();
        }
        
        Usuario usuario = usuarioLogado();
        
        // Verificar se o usuário pode visualizar este leilão
        boolean podeVisualizar = true;
        if (usuario == null) {
            podeVisualizar = false;
        } else if (leilao.tipoLeilao == Leilao.TipoLeilao.FECHADO && 
                  !leilao.criador.equals(usuario) && 
                  !leilao.isConvidado(usuario)) {
            podeVisualizar = false;
        }
        
        if (!podeVisualizar) {
            flash("mensagem", "Você não tem permissão para visualizar este leilão");
            flash("tipo", "danger");
            return index();
        }
        
        List<Lance> lances = Lance.find("leilao", leilao).list();
        
        // Verificar se o comprador logado pode convidar mais fornecedores
        boolean podeConvidar = usuario != null && 
                              usuario.equals(leilao.criador) && 
                              (leilao.status == Leilao.Status.RASCUNHO || leilao.status == Leilao.Status.AGENDADO);
        
        // Verificar se o fornecedor pode participar do leilão
        boolean podeParticipar = usuario != null && 
                                usuario.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR &&
                                leilao.status == Leilao.Status.ABERTO &&
                                (leilao.tipoLeilao == Leilao.TipoLeilao.ABERTO || leilao.isConvidado(usuario));
        
        return Templates.visualizar(leilao, lances, podeConvidar, podeParticipar);
    }
    
    // Página para convidar fornecedores para um leilão fechado
    @Path("/convidar/{id}")
    @RequiresAuth
    @RequiresRole(Usuario.TipoUsuario.COMPRADOR)
    public TemplateInstance convidar(@PathParam("id") Long id) {
        Leilao leilao = Leilao.findById(id);
        if (leilao == null) {
            flash("mensagem", "Leilão não encontrado");
            flash("tipo", "danger");
            return index();
        }
        
        Usuario usuario = usuarioLogado();
        if (!usuario.equals(leilao.criador)) {
            flash("mensagem", "Você não tem permissão para convidar fornecedores");
            flash("tipo", "danger");
            return index();
        }
        
        List<Usuario> fornecedores = Usuario.listarFornecedoresAtivos();
        return Templates.convidar(leilao, fornecedores);
    }
    
    // Ação para convidar fornecedores para um leilão
    @POST
    @Path("/enviarConvites/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @RequiresAuth
    @RequiresRole(Usuario.TipoUsuario.COMPRADOR)
    public void enviarConvites(
            @PathParam("id") Long leilaoId,
            @FormParam("fornecedores") List<Long> fornecedoresIds,
            @FormParam("publicar") String publicar) {
        
        Leilao leilao = Leilao.findById(leilaoId);
        if (leilao == null) {
            flash("mensagem", "Leilão não encontrado");
            flash("tipo", "danger");
            index();
            return;
        }
        
        Usuario usuario = usuarioLogado();
        if (!usuario.equals(leilao.criador)) {
            flash("mensagem", "Você não tem permissão para convidar fornecedores");
            flash("tipo", "danger");
            index();
            return;
        }
        
        if (fornecedoresIds != null && !fornecedoresIds.isEmpty()) {
            for (Long fornecedorId : fornecedoresIds) {
                Usuario fornecedor = Usuario.findById(fornecedorId);
                if (fornecedor != null && fornecedor.tipoUsuario == Usuario.TipoUsuario.FORNECEDOR) {
                    // Verificar se já existe um convite para este fornecedor
                    if (!Convite.existeConvite(leilao, fornecedor)) {
                        Convite convite = new Convite(leilao, fornecedor);
                        convite.persist();
                        
                        // Enviar email de convite
                        notificacaoService.notificarConvite(convite);
                    }
                }
            }
            
            flash("mensagem", "Convites enviados com sucesso!");
            flash("tipo", "success");
        } else {
            flash("mensagem", "Nenhum fornecedor selecionado");
            flash("tipo", "warning");
        }
        
        // Publicar o leilão se solicitado
        if (publicar != null && !publicar.isEmpty()) {
            leilao.publicar();
        }
        
        visualizar(leilaoId);
    }
    
    // Listar leilões criados pelo usuário logado (para compradores)
    @Path("/meus")
    @RequiresAuth
    @RequiresRole(Usuario.TipoUsuario.COMPRADOR)
    public TemplateInstance meusLeiloes() {
        List<Leilao> leiloes = Leilao.listarLeiloesPorCriador(usuarioLogado());
        return Templates.meusLeiloes(leiloes);
    }
    
    // Listar leilões disponíveis para participação (para fornecedores)
    @Path("/disponiveis")
    @RequiresAuth
    @RequiresRole(Usuario.TipoUsuario.FORNECEDOR)
    public TemplateInstance disponiveis() {
        List<Leilao> leiloes = Leilao.listarLeiloesPorFornecedor(usuarioLogado());
        return Templates.disponiveis(leiloes);
    }
    
    // Cancelar um leilão
    @POST
    @Path("/cancelar/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @RequiresAuth
    @RequiresRole(Usuario.TipoUsuario.COMPRADOR)
    public void cancelar(
            @PathParam("id") Long id,
            @FormParam("motivo") @NotBlank String motivo) {
        
        Leilao leilao = Leilao.findById(id);
        if (leilao == null) {
            flash("mensagem", "Leilão não encontrado");
            flash("tipo", "danger");
            index();
            return;
        }
        
        Usuario usuario = usuarioLogado();
        if (!usuario.equals(leilao.criador)) {
            flash("mensagem", "Você não tem permissão para cancelar este leilão");
            flash("tipo", "danger");
            index();
            return;
        }
        
        if (validationFailed()) {
            flash("mensagem", "É necessário informar o motivo do cancelamento");
            flash("tipo", "danger");
            visualizar(id);
            return;
        }
        
        // Apenas leilões em rascunho, agendados ou abertos podem ser cancelados
        if (leilao.status != Leilao.Status.RASCUNHO && 
            leilao.status != Leilao.Status.AGENDADO && 
            leilao.status != Leilao.Status.ABERTO) {
            
            flash("mensagem", "Este leilão não pode ser cancelado no estado atual");
            flash("tipo", "danger");
            visualizar(id);
            return;
        }
        
        leilao.cancelar(motivo);
        
        // Aplicar penalidade ao comprador
        usuario.atualizarPontuacao(-1.0);
        
        flash("mensagem", "Leilão cancelado com sucesso");
        flash("tipo", "success");
        meusLeiloes();
    }
    
    @GET
    @Operation(
        summary = "Lista leilões paginados",
        description = "Retorna uma lista paginada de leilões"
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de leilões retornada com sucesso",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = PaginatedResponse.class))
    )
    public PaginatedResponse<Leilao> listarLeiloes(
        @Parameter(description = "Número da página (começa em 1)") @QueryParam("page") @DefaultValue("1") int page,
        @Parameter(description = "Tamanho da página") @QueryParam("size") @DefaultValue("10") int size
    ) {
        return leilaoService.listarLeiloesPaginados(page, size);
    }
    
    @GET
    @Path("/{id}")
    @Operation(
        summary = "Busca leilão por ID",
        description = "Retorna os detalhes de um leilão específico"
    )
    @APIResponse(
        responseCode = "200",
        description = "Leilão encontrado",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Leilao.class))
    )
    @APIResponse(
        responseCode = "404",
        description = "Leilão não encontrado"
    )
    public Leilao buscarPorId(
        @Parameter(description = "ID do leilão") @PathParam("id") Long id
    ) {
        return leilaoService.buscarPorId(id);
    }
    
    // Usando o método usuarioLogado() herdado de BaseController
}
