package io.github.alfregood20.apigestorfamiliarmedico.alerta.dto;

import io.github.alfregood20.apigestorfamiliarmedico.alerta.enums.TipoAlertaEnum;
import java.time.LocalDateTime;

public record AlertaResponse(
        Long id,
        Long idIntegrante,
        String nombreIntegrante,
        Long idReferencia,
        TipoAlertaEnum tipo,
        String mensaje,
        boolean leida,
        LocalDateTime fechaGeneracion
) {
}