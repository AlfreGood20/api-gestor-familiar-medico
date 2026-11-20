package io.github.alfregood20.apigestorfamiliarmedico.integrante.dto;

import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.DosisCartillaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.DosisResponse;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.VacunaResponse;
import java.util.List;

public record CartillaVacunacionResponse(
        VacunaResponse vacuna,
        int dosisAplicadas,
        boolean esquemaCompleto,
        List<DosisCartillaResponse> dosis
) {
}