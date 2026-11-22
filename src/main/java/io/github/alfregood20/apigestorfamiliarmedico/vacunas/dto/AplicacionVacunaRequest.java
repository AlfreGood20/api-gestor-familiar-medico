package io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record AplicacionVacunaRequest(
        @NotNull(message = "Es obligatorio")
        @Positive(message = "El valor tiene que ser positivo")
        Long idIntegrante,
        @NotNull(message = "Es obligatorio")
        @Positive(message = "El valor tiene que ser positivo")
        Long idVacuna,
        @NotNull(message = "Es obligatorio")
        @Positive(message = "El valor tiene que ser positivo")
        int numeroDosis,
        @NotNull(message = "Es obligatorio")
        LocalDate fechaAplicacion,
        @Schema(example = " null")
        String lote,
        @Schema(example = "null")
        String lugarAplicacion
) {
}
