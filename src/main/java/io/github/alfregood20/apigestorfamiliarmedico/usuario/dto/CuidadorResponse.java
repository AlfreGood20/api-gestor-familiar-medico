package io.github.alfregood20.apigestorfamiliarmedico.usuario.dto;

import io.github.alfregood20.apigestorfamiliarmedico.usuario.enums.EstadoUsuarioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.enums.RolEnum;

import java.time.LocalDateTime;

public record CuidadorResponse(
        Long id,
        String nombreCompleto,
        String correo,
        String telefono,
        RolEnum rol,
        EstadoUsuarioEnum estado,
        LocalDateTime fechaRegistro
) {
}
