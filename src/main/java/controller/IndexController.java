package controller;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.Leilao;
import model.Usuario;
import util.RedirectUtil;

import java.util.List;

@Path("/")
@Produces(MediaType.TEXT_HTML)
@PermitAll
public class IndexController extends BaseController {
    
    @CheckedTemplate(basePath = "Index")
    public static class Templates {
        public static native TemplateInstance index(List<Leilao> leiloes, Usuario usuario);
    }
    
    @GET
    public Object index() {
        Usuario usuario = usuarioLogado();
        List<Leilao> leiloes = Leilao.listarLeiloesAbertos();
        return Templates.index(leiloes, usuario);
    }
} 