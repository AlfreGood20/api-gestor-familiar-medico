package io.github.alfregood20.apigestorfamiliarmedico.auth.dto;

import io.github.alfregood20.apigestorfamiliarmedico.usuario.enums.RolEnum;

import java.time.LocalDateTime;

public record MeResponse(
        Long id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String correo,
        String telefono,
        RolEnum rol,
        LocalDateTime fechaRegistro
) {
}
