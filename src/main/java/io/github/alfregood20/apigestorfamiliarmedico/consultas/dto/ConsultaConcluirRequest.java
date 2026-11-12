package io.github.alfregood20.apigestorfamiliarmedico.consultas.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConsultaConcluirRequest(
        @NotBlank(message = "Es obligatorio")
        String diagnostico,
        String notas,
        BigDecimal costo,
        @Future(message = "La fecha de la próxima consulta debe ser una fecha futura")
        LocalDate proximaConsulta
) {
}
