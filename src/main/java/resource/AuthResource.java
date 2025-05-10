package resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import model.Usuario;
import service.UsuarioService;

import jakarta.inject.Inject;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UsuarioService usuarioService;

    @GET
    @Path("/me")
    public Usuario getCurrentUser(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        return usuarioService.buscarPorEmail(email);
    }
} 