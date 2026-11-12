package io.github.alfregood20.apigestorfamiliarmedico.consultas.dto;

import io.github.alfregood20.apigestorfamiliarmedico.consultas.enums.EstadoConsultaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
import io.github.alfregood20.apigestorfamiliarmedico.medico.dto.MedicoResponse;

import java.time.LocalDateTime;

public record ConsultaResponse(
        Long id,
        String nombrePaciente,
        String nombreMedico,
        String motivo,
        LocalDateTime fechaConsulta,
        EstadoConsultaEnum estado
) {
}
