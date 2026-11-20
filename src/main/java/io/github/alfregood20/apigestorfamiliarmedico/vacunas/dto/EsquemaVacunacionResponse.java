package io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto;

public record EsquemaVacunacionResponse(
        Long id,
        VacunaResponse vacuna,
        int numeroDosis,
        int edadRecomendadaMeses,
        Integer intervaloMinimoDias,
        boolean esRefuerzo
) {
}
