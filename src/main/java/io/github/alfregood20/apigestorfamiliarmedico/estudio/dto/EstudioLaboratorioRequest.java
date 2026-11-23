package io.github.alfregood20.apigestorfamiliarmedico.estudio.dto;

import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.CategoriaEstudioEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EstudioLaboratorioRequest(

        @NotNull(message = "El ID del integrante es obligatorio")
        @Positive(message = "El ID del integrante debe ser positivo")
        @Schema(description = "Identificador único del integrante de la familia", example = "1")
        Long idIntegrante,

        @Positive(message = "El ID de la consulta debe ser positivo")
        @Schema(description = "ID de la consulta médica donde se solicitó el estudio (opcional)", example = "15")
        Long idConsulta,

        @NotNull(message = "La fecha del estudio es obligatoria")
        @Schema(description = "Fecha programada o de realización del estudio", example = "2026-11-25")
        LocalDate fechaEstudio,

        @NotBlank(message = "El tipo de estudio es obligatorio")
        @Size(max = 150, message = "El tipo de estudio no puede exceder los 150 caracteres")
        @Schema(description = "Nombre específico del análisis o estudio clínico", example = "Biometría Hemática Completa")
        String tipoEstudio,

        @NotNull(message = "La categoría del estudio es obligatoria")
        @Schema(description = "Clasificación general del estudio", example = "SANGRE")
        CategoriaEstudioEnum categoria,

        @Size(max = 200, message = "El nombre del laboratorio no puede exceder los 200 caracteres")
        @Schema(description = "Nombre del laboratorio o clínica (opcional)", example = "Salud Digna")
        String laboratorio,

        @Size(max = 255, message = "Las indicaciones no pueden exceder los 255 caracteres")
        @Schema(description = "Preparación previa requerida para el estudio (opcional)", example = "Ayuno mínimo de 8 horas")
        String indicaciones,

        @PositiveOrZero(message = "El costo no puede ser negativo")
        @Schema(description = "Costo del estudio en moneda local (opcional)", example = "350.00")
        BigDecimal costo,

        // --- Campos opcionales (por si el usuario registra un estudio ya realizado de una vez) ---
        @Schema(description = "Resultados obtenidos (dejar nulo si aún está pendiente de entrega)", example = "null")
        String resultado,

        @Schema(description = "Rangos normales de referencia del laboratorio (opcional)", example = "null")
        String valoresReferencia,

        @Schema(description = "Observaciones clínicas o notas adicionales (opcional)", example = "null")
        String observaciones,

        @Size(max = 500, message = "La URL del archivo no puede exceder los 500 caracteres")
        @Schema(description = "Enlace al PDF o imagen del resultado (opcional)", example = "null")
        String archivoUrl
) {
}
