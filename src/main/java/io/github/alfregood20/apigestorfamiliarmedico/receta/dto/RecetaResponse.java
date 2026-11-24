package io.github.alfregood20.apigestorfamiliarmedico.receta.dto;

import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RecetaResponse(
        Long id,
        IntegranteResponse integrante,
        ConsultaResponse consulta, // null si es una receta externa sin cita vinculada
        LocalDate fechaEmision,
        String indicaciones,
        boolean activa,
        LocalDateTime fechaRegistro,
        List<DetalleRecetaResponse> detalles
) {
}
