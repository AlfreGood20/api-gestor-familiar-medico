package io.github.alfregood20.apigestorfamiliarmedico.usuario;

import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.enums.RolEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);
    Boolean existsByCorreo(String correo);

    List<Usuario> findByFamiliaAndRolOrderByFechaRegistroDesc(Familia familia, RolEnum rol);
    Optional<Usuario> findByIdAndFamiliaAndRol(Long id, Familia familia, RolEnum rol);
}