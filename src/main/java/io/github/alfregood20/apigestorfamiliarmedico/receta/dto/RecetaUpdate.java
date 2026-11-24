package io.github.alfregood20.apigestorfamiliarmedico.receta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RecetaUpdate(
        @Positive(message = "El ID de la consulta debe ser positivo")
        @Schema(description = "ID de la consulta médica asociada (enviar null para desvincular)", example = "4")
        Long idConsulta,

        @NotNull(message = "La fecha de emisión es obligatoria")
        @Schema(description = "Fecha en que se expidió la receta", example = "2026-11-23")
        LocalDate fechaEmision,

        @Schema(description = "Indicaciones generales o cuidados adicionales del médico", example = "Tomar después de los alimentos.")
        String indicaciones
) {
}
