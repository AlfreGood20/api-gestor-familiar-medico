package io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AplicacionVacunaUpdate(
        @NotNull(message = "Es obligatorio")
        LocalDate fechaAplicacion,
        String lote,
        String lugarAplicacion
) {
}