package resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Usuario;
import service.UsuarioService;
import interceptor.Autenticado;
import interceptor.RequerPermissao;

import java.util.List;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    
    @Inject
    UsuarioService usuarioService;
    
    @GET
    @Autenticado
    @RequerPermissao(RequerPermissao.TipoPermissao.ADMIN)
    public List<Usuario> listar() {
        return usuarioService.listarTodos();
    }
    
    @GET
    @Path("/{id}")
    @Autenticado
    public Usuario buscar(@PathParam("id") Long id) {
        return usuarioService.buscarPorId(id);
    }
    
    @POST
    @Autenticado
    @RequerPermissao({RequerPermissao.TipoPermissao.ADMIN, RequerPermissao.TipoPermissao.COMPRADOR})
    public Response criar(Usuario usuario) {
        usuarioService.salvar(usuario);
        return Response.status(Response.Status.CREATED).build();
    }
    
    @PUT
    @Path("/{id}")
    @Autenticado
    @RequerPermissao({RequerPermissao.TipoPermissao.ADMIN, RequerPermissao.TipoPermissao.COMPRADOR})
    public Response atualizar(@PathParam("id") Long id, Usuario usuario) {
        usuario.setId(id);
        usuarioService.atualizar(usuario);
        return Response.ok().build();
    }
    
    @DELETE
    @Path("/{id}")
    @Autenticado
    @RequerPermissao(RequerPermissao.TipoPermissao.ADMIN)
    public Response excluir(@PathParam("id") Long id) {
        usuarioService.excluir(id);
        return Response.noContent().build();
    }
} 