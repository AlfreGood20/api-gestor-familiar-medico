package io.github.alfregood20.apigestorfamiliarmedico.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);
    Boolean existsByCorreo(String correo);
}