package io.github.alfregood20.apigestorfamiliarmedico.receta.dto;

import io.github.alfregood20.apigestorfamiliarmedico.receta.enums.EstadoTomaEnum;
import java.time.LocalDateTime;

public record TomaProgramadaResponse(
        Long id,
        LocalDateTime fechaHoraProgramada,
        EstadoTomaEnum estado,
        LocalDateTime fechaHoraReal
) {
}
