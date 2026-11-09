package io.github.alfregood20.apigestorfamiliarmedico.alergia.dto;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.enums.TipoAlergiaEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlergiaRequest(
        @NotBlank(message = "Es obligatorio")
        String nombre,

        @NotNull(message = "Es obligatorio")
        TipoAlergiaEnum tipo
) {
}
