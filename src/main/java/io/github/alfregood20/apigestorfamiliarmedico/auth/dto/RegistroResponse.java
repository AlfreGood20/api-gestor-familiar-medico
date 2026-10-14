package io.github.alfregood20.apigestorfamiliarmedico.auth.dto;

import io.github.alfregood20.apigestorfamiliarmedico.familia.dto.FamiliaResponse;

import java.time.LocalDateTime;

public record RegistroResponse(

        Long id,
        String nombreCompleto,
        String correo,
        String telefono,
        String estado,
        FamiliaResponse familia,
        LocalDateTime fechaRegistro
) {
}