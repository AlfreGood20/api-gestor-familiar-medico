package io.github.alfregood20.apigestorfamiliarmedico.receta;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.receta.dto.TomaProgramadaAgendaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.receta.enums.EstadoTomaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.receta.dto.TomaEstadoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tomas")
@RequiredArgsConstructor
@Tag(
        name = "Tomas Programadas",
        description = "Endpoints para consultar la agenda diaria o por receta de los medicamentos programados y registrar la administración u omisión de cada dosis."
)
public class TomaProgramadaControl {

    private final TomaProgramadaServ tomaProgramadaServ;

    @Operation(
            summary = "Consultar agenda de tomas programadas",
            description = "Obtiene de forma paginada las tomas programadas de la familia ordenadas cronológicamente. Si no se indican fechas, devuelve por defecto las tomas del día de hoy; permite filtrar por rango de fechas, integrante y estado."
    )
    @GetMapping
    public ResponseEntity<Page<TomaProgramadaAgendaResponse>> obtenerTomas(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idIntegrante,
            @RequestParam(required = false) EstadoTomaEnum estado,
            @ParameterObject @PageableDefault(size = 20, sort = "fechaHoraProgramada", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(tomaProgramadaServ.obtenerTomas(usuario, fechaInicio, fechaFin, idIntegrante, estado, pageable));
    }

    @Operation(
            summary = "Actualizar estado de una toma programada",
            description = "Cambia el estado de una toma programada a TOMADA (registrando automáticamente la fecha y hora real si no se envía una manual), OMITIDA, PENDIENTE o CANCELADA."
    )
    @PatchMapping("/{idToma}/estado")
    public ResponseEntity<TomaProgramadaAgendaResponse> actualizarEstado(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idToma,
            @Valid @RequestBody TomaEstadoRequest request
    ) {
        return ResponseEntity.ok(tomaProgramadaServ.actualizarEstado(usuario, idToma, request));
    }

    @Operation(
            summary = "Obtener cronograma de tomas de una receta",
            description = "Devuelve todas las tomas programadas pertenecientes a una receta médica específica ordenadas cronológicamente, permitiendo filtrar opcionalmente por estado."
    )
    @GetMapping("/recetas/{idReceta}")
    public ResponseEntity<List<TomaProgramadaAgendaResponse>> obtenerTomasPorReceta(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idReceta,
            @RequestParam(required = false) EstadoTomaEnum estado
    ) {
        return ResponseEntity.ok(tomaProgramadaServ.obtenerTomasPorReceta(usuario, idReceta, estado));
    }
}