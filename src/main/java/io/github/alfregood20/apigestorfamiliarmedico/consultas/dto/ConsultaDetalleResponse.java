package io.github.alfregood20.apigestorfamiliarmedico.consultas.dto;

import io.github.alfregood20.apigestorfamiliarmedico.consultas.enums.EstadoConsultaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteDetalleResponse;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
import io.github.alfregood20.apigestorfamiliarmedico.medico.dto.MedicoResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ConsultaDetalleResponse(
        Long id,
        IntegranteResponse paciente,
        MedicoResponse medico,
        LocalDateTime fechaConsulta,
        String motivo,
        String diagnostico,
        String notas,
        BigDecimal costo,
        EstadoConsultaEnum estado,
        LocalDate proximaConsulta,
        LocalDateTime fechaRegistro
) {
}