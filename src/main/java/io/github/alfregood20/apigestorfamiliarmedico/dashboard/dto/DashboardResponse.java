package io.github.alfregood20.apigestorfamiliarmedico.dashboard.dto;

import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
import io.github.alfregood20.apigestorfamiliarmedico.receta.dto.TomaProgramadaAgendaResponse;
import java.util.List;

public record DashboardResponse(
        String nombreFamilia,
        Kpis kpis,
        List<TomaProgramadaAgendaResponse> tomasDeHoy,
        List<ConsultaResponse> proximasConsultas,
        List<IntegranteResponse> integrantes
) {
}