package io.github.alfregood20.apigestorfamiliarmedico.stock.dto;

import io.github.alfregood20.apigestorfamiliarmedico.stock.enums.UnidadStockEnum;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

public record StockRequest(

        @NotNull(message = "Es obligatorio")
        Long idMedicamento,

        @NotNull(message = "Es obligatorio")
        @Positive(message = "El valor debe ser 0 o mayor")
        int cantidad,

        @PositiveOrZero(message = "Debe ser positivo")
        int cantidadMinima,

        @NotNull(message = "Es obligatorio")
        @FutureOrPresent(message = "El medicamento esta caducado")
        LocalDate fechaCaducidad
) {
}
