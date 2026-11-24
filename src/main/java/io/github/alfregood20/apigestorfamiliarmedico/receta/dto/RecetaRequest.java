package io.github.alfregood20.apigestorfamiliarmedico.receta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record RecetaRequest(
        @NotNull(message = "El integrante es obligatorio")
        Long idIntegrante,

        @Schema(example = "null")
        Long idConsulta,

        @NotNull(message = "La fecha de emisión es obligatoria")
        LocalDate fechaEmision,

        @Schema(example = "null")
        String indicaciones,

        @NotEmpty(message = "La receta debe contener al menos un medicamento")
        @Valid
        List<DetalleRecetaRequest> detalles
) {
}