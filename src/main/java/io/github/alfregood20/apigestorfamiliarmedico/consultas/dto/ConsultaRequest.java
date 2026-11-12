package io.github.alfregood20.apigestorfamiliarmedico.consultas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record ConsultaRequest(
        @NotNull(message = "Es obligatorio")
        @Positive(message = "El valor tiene que ser positivo")
        Long idIntegrante,
        @Schema(example = "null")
        @Positive(message = "El valor tiene que ser positivo")
        Long idMedico,
        @NotNull(message = "Es obligatorio")
        @FutureOrPresent(message = "La fecha de la consulta debe ser presente o futura")
        @JsonFormat(pattern = "yyyy-MM-dd h:mma", locale = "en")
        @Schema(example = "2027-06-07 11:12AM")
        LocalDateTime fechaConsulta,
        @NotBlank(message = "Es obligatorio")
        String motivo
) {
}