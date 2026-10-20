package io.github.alfregood20.apigestorfamiliarmedico.medicamento.dto;

import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.FormaFarmaceuticaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.ViaAdministracionEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicamentoRequest(

        @NotBlank(message = "Es obligatorio")
        String nombreComercial,

        @NotBlank(message = "Es obligatorio")
        String sustanciaActiva,

        @NotBlank(message = "Es obligatorio")
        String concentracion,

        @NotNull(message = "Es obligatorio")
        FormaFarmaceuticaEnum formaFarmaceutica,

        @NotNull(message = "Es obligatorio")
        ViaAdministracionEnum viaAdministracion
) {
}
