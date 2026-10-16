package io.github.alfregood20.apigestorfamiliarmedico.usuario.dto;

import io.github.alfregood20.apigestorfamiliarmedico.usuario.enums.EstadoUsuarioEnum;
import jakarta.validation.constraints.NotNull;

public record EstadoRequest(
        @NotNull(message = "Es obligatorio")
        EstadoUsuarioEnum estado
) {
}
