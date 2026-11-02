package io.github.alfregood20.apigestorfamiliarmedico.stock.dto;

import io.github.alfregood20.apigestorfamiliarmedico.stock.enums.UnidadStockEnum;

import java.time.LocalDate;

public record StockUpdateRequest(
        int cantidad,
        int cantidadMinima,
        UnidadStockEnum unidad,
        LocalDate fechaCaducidad
) {
}
