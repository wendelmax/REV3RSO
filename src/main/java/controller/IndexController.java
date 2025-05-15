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
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Path("/")
@Produces(MediaType.TEXT_HTML)
@PermitAll
public class IndexController extends BaseController {
    
    @CheckedTemplate(basePath = "Index", requireTypeSafeExpressions = false)
    public static class Templates {
        public static native TemplateInstance index(List<Leilao> leiloes, Usuario usuario);
    }
    
    @GET
    public Object index() {
        List<Leilao> leiloes = Leilao.listarLeiloesAbertos();
        System.out.println("Número de leilões encontrados: " + leiloes.size());
        for (Leilao leilao : leiloes) {
            System.out.println("Leilão: " + leilao.titulo + " (ID: " + leilao.id + ")");
        }
        return Templates.index(leiloes, null);
    }

    @GET
    @Path("/api/leiloes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, Object>> listarLeiloes() {
        List<Leilao> leiloes = Leilao.listarLeiloesAbertos();
        return leiloes.stream().map(leilao -> {
            Map<String, Object> leilaoMap = new HashMap<>();
            leilaoMap.put("id", leilao.id);
            leilaoMap.put("titulo", leilao.titulo);
            leilaoMap.put("descricao", leilao.descricao);
            leilaoMap.put("dataInicio", leilao.dataInicio);
            leilaoMap.put("dataFim", leilao.dataFim);
            leilaoMap.put("tipoLeilao", leilao.tipoLeilao);
            leilaoMap.put("valorReferencia", leilao.valorReferencia);
            leilaoMap.put("valorReferenciaFormatado", leilao.getValorReferenciaFormatado());
            return leilaoMap;
        }).collect(Collectors.toList());
    }
} 