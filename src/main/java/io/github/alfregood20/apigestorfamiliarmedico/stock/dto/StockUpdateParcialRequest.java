package io.github.alfregood20.apigestorfamiliarmedico.stock.dto;

import io.github.alfregood20.apigestorfamiliarmedico.stock.enums.EstadoStockEnum;
import jakarta.validation.constraints.PositiveOrZero;

public record StockUpdateParcialRequest(
        @PositiveOrZero(message = "El valor tiene que ser positivo")
        Integer cantidad,
        EstadoStockEnum estado
) {
}
