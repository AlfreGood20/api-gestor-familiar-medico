package io.github.alfregood20.apigestorfamiliarmedico.estudio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record EstudioResultadoRequest(

        @NotBlank(message = "El resultado del estudio es obligatorio")
        @Schema(
                description = "Resultados obtenidos en el estudio clínico",
                example = "Hemoglobina: 14.2 g/dL, Leucocitos: 6,800 /µL, Plaquetas: 245,000 /µL"
        )
        String resultado,

        @Schema(
                description = "Rangos normales de referencia indicados por el laboratorio (opcional)",
                example = "Hemoglobina: 13.5 - 17.5 g/dL | Leucocitos: 4,500 - 11,000 /µL"
        )
        String valoresReferencia,

        @Schema(
                description = "Observaciones clínicas o interpretación médica (opcional)",
                example = "Valores dentro de parámetros normales"
        )
        String observaciones,

        @Size(max = 500, message = "La URL del archivo no puede exceder los 500 caracteres")
        @Schema(
                description = "Enlace al PDF o imagen del resultado (opcional)",
                example = "https://drive.google.com/file/d/123456/view"
        )
        String archivoUrl
) {
}
