package io.github.alfregood20.apigestorfamiliarmedico.receta.dto;

import io.github.alfregood20.apigestorfamiliarmedico.medicamento.dto.MedicamentoResponse;
import java.time.LocalDateTime;
import java.util.List;

public record DetalleRecetaResponse(
        Long id,
        MedicamentoResponse medicamento,
        String dosis,
        int frecuenciaHoras,
        Integer duracionDias,
        LocalDateTime fechaHoraInicio,
        int totalTomas,
        int tomasCompletadas
) {
}
