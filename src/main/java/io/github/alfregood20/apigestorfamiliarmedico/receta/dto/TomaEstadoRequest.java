package io.github.alfregood20.apigestorfamiliarmedico.receta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.alfregood20.apigestorfamiliarmedico.receta.enums.EstadoTomaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record TomaEstadoRequest(
        @NotNull(message = "El nuevo estado de la toma es obligatorio")
        @Schema(description = "Nuevo estado: TOMADA, OMITIDA, PENDIENTE o CANCELADA", example = "TOMADA")
        EstadoTomaEnum estado,

        @JsonFormat(pattern = "yyyy-MM-dd h:mma", locale = "en")
        @Schema(description = "Fecha y hora real en que se tomó (opcional; si es null al marcar TOMADA se usa la hora actual)", example = "2026-11-24 04:45PM")
        LocalDateTime fechaHoraReal
) {
}