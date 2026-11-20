package io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto;

import java.time.LocalDate;

public record AplicacionVacunaResponse(
        Long id,
        LocalDate fechaAplicacion,
        String lote,
        String lugarAplicacion
) {
}
