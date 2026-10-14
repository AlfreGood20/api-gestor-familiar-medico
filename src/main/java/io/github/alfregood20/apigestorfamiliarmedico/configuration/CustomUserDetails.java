package io.github.alfregood20.apigestorfamiliarmedico.configuration;

import io.github.alfregood20.apigestorfamiliarmedico.usuario.enums.EstadoUsuarioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class CustomUserDetails implements UserDetails {

    private Usuario usuario;

    public Long getId (){
        return usuario.getIdUsuario();
    }

    public String getNombreCompleto(){
        return usuario.getNombreCompleto();
    }

    public String getRol (){
        return usuario.getRol().name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+usuario.getRol().name()));
    }

    @Override
    public @Nullable String getPassword() {
        return usuario.getContrasenaHash();
    }

    @Override
    public String getUsername() {
        return usuario.getCorreo();
    }

    @Override
    public boolean isEnabled() {
        return usuario.getEstado() == EstadoUsuarioEnum.ACTIVO;
    }
}