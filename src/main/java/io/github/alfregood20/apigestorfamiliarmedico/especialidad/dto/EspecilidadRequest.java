package io.github.alfregood20.apigestorfamiliarmedico.especialidad.dto;

import jakarta.validation.constraints.NotBlank;

public record EspecilidadRequest(
        @NotBlank(message = "Es obligatorio")
        String nombre
) {
}