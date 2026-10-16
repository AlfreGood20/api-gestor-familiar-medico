package io.github.alfregood20.apigestorfamiliarmedico.alergia.dto;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.enums.SeveridadAlergiaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.alergia.enums.TipoAlergiaEnum;

public record IntegranteAlergiaResponse(
        Long idAlergia,
        String nombre,
        TipoAlergiaEnum tipo,
        SeveridadAlergiaEnum severidad,
        String observaciones
) {
}
