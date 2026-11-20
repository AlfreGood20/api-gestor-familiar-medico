package io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto;

import java.util.List;

public record CartillaVacunacionOficialResponse(
        VacunaResponse vacuna,
        List<DosisResponse> dosis
) {
}
