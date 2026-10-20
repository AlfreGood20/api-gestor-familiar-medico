package io.github.alfregood20.apigestorfamiliarmedico.medicamento.dto;

import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.FormaFarmaceuticaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.ViaAdministracionEnum;

public record MedicamentoResponse(
        Long id,
        String nombreComercial,
        String sustanciaActiva,
        String concentracion,
        FormaFarmaceuticaEnum formaFarmaceutica,
        ViaAdministracionEnum viaAdministracion
) {
}
