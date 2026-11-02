package io.github.alfregood20.apigestorfamiliarmedico.stock.dto;

import io.github.alfregood20.apigestorfamiliarmedico.medicamento.dto.MedicamentoResponse;
import io.github.alfregood20.apigestorfamiliarmedico.stock.enums.EstadoStockEnum;
import io.github.alfregood20.apigestorfamiliarmedico.stock.enums.UnidadStockEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StockResponse(
        Long id,
        MedicamentoResponse medicamento,
        int cantidad,
        int cantidadMinima,
        UnidadStockEnum unidad,
        EstadoStockEnum estado,
        LocalDate fechaCaducidad,
        LocalDateTime fechaRegistro
) {
}
