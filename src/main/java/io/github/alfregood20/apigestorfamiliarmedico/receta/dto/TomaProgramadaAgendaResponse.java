package io.github.alfregood20.apigestorfamiliarmedico.receta.dto;

import io.github.alfregood20.apigestorfamiliarmedico.receta.enums.EstadoTomaEnum;

import java.time.LocalDateTime;

public record TomaProgramadaAgendaResponse(
        Long id,
        Long idReceta,
        Long idIntegrante,
        String nombreIntegrante,
        String nombreMedicamento,
        String concentracion,
        String dosis,
        LocalDateTime fechaHoraProgramada,
        EstadoTomaEnum estado,
        LocalDateTime fechaHoraReal
) {
}
