package io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto;

public record DosisResponse(
        Long id,
        int numeroDosis,
        int edadRecomendadaMeses,
        Integer intervaloMinimoDias,
        boolean esRefuerzo
) {
}