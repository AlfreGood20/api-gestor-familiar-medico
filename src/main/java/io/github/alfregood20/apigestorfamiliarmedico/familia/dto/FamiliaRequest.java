package io.github.alfregood20.apigestorfamiliarmedico.familia.dto;

import jakarta.validation.constraints.NotBlank;

public record FamiliaRequest(

        @NotBlank(message = "Es obligatorio")
        String nombre,
        String direccion
) {
}
