package io.github.alfregood20.apigestorfamiliarmedico.configuration;

import io.github.alfregood20.apigestorfamiliarmedico.exception.UsuarioNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServic implements UserDetailsService {

    private final UsuarioRepo usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        final Usuario usuario = usuarioRepo.findByCorreo(username)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario con correo: "+username+" ,no encontrado"));

        return new CustomUserDetails(usuario);
    }
}
