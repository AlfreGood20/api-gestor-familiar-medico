package io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VacunaRequest(
        @NotBlank(message = "Es obligatorio")
        String nombre,
        String descripcion,
        String contraindicaciones,
        @NotBlank(message = "Es obligatorio")
        String enfermedadPreviene,
        @NotNull(message = "Es obligatorio")
        @Positive(message = "El valor debe ser positivo")
        Integer dosisTotales
) {
}