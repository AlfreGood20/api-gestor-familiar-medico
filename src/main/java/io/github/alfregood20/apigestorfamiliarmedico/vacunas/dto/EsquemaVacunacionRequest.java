package io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record EsquemaVacunacionRequest(
        @Positive(message = "El valor tiene que ser positivo")
        @NotNull(message = "Es obligatorio")
        Long idVacuna,
        @Positive(message = "El valor tiene que ser positivo")
        @NotNull(message = "Es obligatorio")
        int numeroDosis,
        @PositiveOrZero(message = "El valor tiene que ser positivo o puede ser 0")
        @NotNull(message = "Es obligatorio")
        int edadRecomendadaMeses,
        @Positive(message = "El valor tiene que ser positivo")
        @Schema(example = "null")
        Integer intervaloMinimoDias,
        @Schema(example = "false")
        boolean esRefuerzo
) {
}