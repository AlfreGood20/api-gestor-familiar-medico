package io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto;

public record VacunaResponse(
        Long id,
        String nombre,
        String descripcion,
        String contraindicaciones,
        String enfermedadPreviene,
        Integer dosisTotales
) {
}