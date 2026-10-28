package io.github.alfregood20.apigestorfamiliarmedico.integrante.dto;

import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.ParentescoEnum;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.SexoEnum;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.TipoSangreEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IntegranteRequest(

        @NotBlank(message = "Es obligatorio")
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        @NotNull(message = "Es obligatorio")
        @Past(message = "Debe ser anterior a hoy")
        LocalDate fechaNacimiento,
        @PositiveOrZero(message = "Debe ser positivo o cero")
        BigDecimal peso,
        @PositiveOrZero(message = "Debe ser positivo o cero")
        BigDecimal estatura,
        @NotNull(message = "Es obligatorio")
        SexoEnum sexo,
        TipoSangreEnum tipoSangre,
        @NotNull(message = "Es obligatorio")
        ParentescoEnum parentesco
) {
}