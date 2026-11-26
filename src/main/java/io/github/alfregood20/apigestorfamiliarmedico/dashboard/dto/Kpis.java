package io.github.alfregood20.apigestorfamiliarmedico.dashboard.dto;

public record Kpis(
        int integrantesRegistrados,
        int consultasPendientes,
        int medicamentosEnStock,
        int medicamentosPorCaducar,
        int tomasHoyCompletadas,
        int tomasHoyTotales
) {
}
