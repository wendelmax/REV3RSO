package security;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.Usuario;
import service.UsuarioService;

@ApplicationScoped
public class UsuarioIdentityProvider implements IdentityProvider<UsernamePasswordAuthenticationRequest> {

    @Inject
    UsuarioService usuarioService;

    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(UsernamePasswordAuthenticationRequest request, 
                                            AuthenticationRequestContext context) {
        return Uni.createFrom().item(() -> {
            Usuario usuario = usuarioService.buscarPorEmail(request.getUsername());
            
            if (usuario != null && usuarioService.verificarSenha(new String(request.getPassword().getPassword()), usuario.senha)) {
                return QuarkusSecurityIdentity.builder()
                    .setPrincipal(() -> usuario.email)
                    .addRole(usuario.tipoUsuario.name())
                    .addAttribute("usuario", usuario)
                    .build();
            }
            
            return null;
        });
    }
} 