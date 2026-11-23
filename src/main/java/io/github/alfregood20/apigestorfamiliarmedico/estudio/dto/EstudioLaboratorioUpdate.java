package io.github.alfregood20.apigestorfamiliarmedico.estudio.dto;

import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.CategoriaEstudioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.EstadoEstudioEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EstudioLaboratorioUpdate(

        @Positive(message = "El ID de la consulta debe ser positivo")
        @Schema(description = "ID de la consulta médica asociada (opcional, puede quedar en null)", example = "15")
        Long idConsulta,

        @NotNull(message = "La fecha del estudio es obligatoria")
        @Schema(description = "Fecha programada o en la que se realizó el estudio", example = "2026-11-25")
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
        @Schema(description = "Preparación previa o indicaciones del estudio (opcional)", example = "Ayuno de 8 horas")
        String indicaciones,

        @Schema(description = "Estado del estudio (opcional; si no se envía, se calcula automáticamente según el resultado)", example = "REALIZADO")
        EstadoEstudioEnum estado,

        @Schema(description = "Resultados obtenidos del laboratorio", example = "Hemoglobina: 14.2 g/dL, Leucocitos: 6,800 /µL")
        String resultado,

        @Schema(description = "Rangos normales de referencia del laboratorio", example = "Hemoglobina: 13.5 - 17.5 g/dL")
        String valoresReferencia,

        @Schema(description = "Observaciones clínicas o notas médicas adicionales", example = "Valores dentro de rangos normales")
        String observaciones,

        @Size(max = 500, message = "La URL del archivo no puede exceder los 500 caracteres")
        @Schema(description = "Enlace al PDF o imagen del resultado", example = "https://drive.google.com/file/...")
        String archivoUrl,

        @PositiveOrZero(message = "El costo no puede ser negativo")
        @Schema(description = "Costo del estudio en moneda local", example = "250.00")
        BigDecimal costo
) {
}