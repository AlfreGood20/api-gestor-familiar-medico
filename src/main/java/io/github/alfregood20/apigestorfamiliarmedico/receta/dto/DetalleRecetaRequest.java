package io.github.alfregood20.apigestorfamiliarmedico.receta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record DetalleRecetaRequest(

        @NotNull(message = "El medicamento es obligatorio")
        Long idMedicamento,

        @NotBlank(message = "La dosis es obligatoria")
        @Size(max = 100)
        String dosis,

        @NotNull(message = "La frecuencia en horas es obligatoria")
        @Positive(message = "La frecuencia debe ser mayor a 0 horas")
        Integer frecuenciaHoras,

        @Positive(message = "La duración debe ser mayor a 0 días")
        @Schema(example = "null")
        Integer duracionDias,

        @NotNull(message = "La fecha y hora de inicio son obligatorias")
        @JsonFormat(pattern = "yyyy-MM-dd h:mma", locale = "en")
        @Schema(example = "2027-06-07 11:12AM")
        LocalDateTime fechaHoraInicio
) {
}
