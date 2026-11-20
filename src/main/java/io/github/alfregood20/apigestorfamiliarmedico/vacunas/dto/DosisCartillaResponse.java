package io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto;

import io.github.alfregood20.apigestorfamiliarmedico.vacunas.enums.EstadoVacunaEnum;

public record DosisCartillaResponse(
        Long id,
        int numeroDosis,
        int edadRecomendadaMeses,
        Integer intervaloMinimoDias,
        boolean esRefuerzo,
        EstadoVacunaEnum estado,
        AplicacionVacunaResponse aplicacion
) {
}