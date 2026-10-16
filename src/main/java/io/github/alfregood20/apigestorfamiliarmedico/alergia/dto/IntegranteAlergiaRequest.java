package io.github.alfregood20.apigestorfamiliarmedico.alergia.dto;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.enums.SeveridadAlergiaEnum;
import jakarta.validation.constraints.*;

import java.util.Set;

public record IntegranteAlergiaRequest(

        @NotNull(message = "Es obligatorio")
        @Positive(message = "El valor tiene que ser positivo")
        Long idAlergia,
        @NotBlank(message = "Es obligatorio")
        SeveridadAlergiaEnum severidad,
        @Size(max = 255, message = "Máximo 255 caracteres")
        String observaciones
) {
}
