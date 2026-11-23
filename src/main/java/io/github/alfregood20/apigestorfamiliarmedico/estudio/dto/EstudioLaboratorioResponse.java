package io.github.alfregood20.apigestorfamiliarmedico.estudio.dto;

import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.CategoriaEstudioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.EstadoEstudioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EstudioLaboratorioResponse(
        Long idEstudio,
        IntegranteResponse integrante,
        ConsultaResponse consulta,
        // Datos principales del estudio
        LocalDate fechaEstudio,
        String tipoEstudio,
        CategoriaEstudioEnum categoria,
        String laboratorio,
        String indicaciones,
        EstadoEstudioEnum estado,
        // Resultados y análisis (null mientras estado == PENDIENTE)
        String resultado,
        String valoresReferencia,
        String observaciones,
        String archivoUrl,
        // Control administrativo y auditoría
        BigDecimal costo,
        LocalDateTime fechaRegistro
) {
}