package io.github.alfregood20.apigestorfamiliarmedico.consultas;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaConcluirRequest;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaDetalleResponse;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.enums.EstadoConsultaEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/consultas")
@RequiredArgsConstructor
@Tag(
        name = "Gestion de consultas medicas",
        description = "Endpoints para la gestion de consultas medicas los integrantes de la familia"
)
public class ConsultaControl {

    private final ConsultaServ consultaServ;

    @Operation(summary = "Agendar nueva cita medica", description = "**ROLES:** ADMIN")
    @PostMapping
    public ResponseEntity<ConsultaResponse> crearNuevo(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @Valid @RequestBody ConsultaRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaServ.crearNuevo(usuario, request));
    }

    @Operation(summary = "Obtener detalles de una consulta", description = "**ROLES:** ADMIN")
    @GetMapping("/{idConsulta}")
    public ResponseEntity<ConsultaDetalleResponse> obtenerDetalles(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idConsulta
    ){
        return ResponseEntity.ok(consultaServ.obtenerDetallePorId(usuario, idConsulta));
    }

    @Operation(summary = "Concluir consulta medica a realizada", description = "**ROLES:** ADMIN")
    @PatchMapping("/{idConsulta}/concluir")
    public ResponseEntity<ConsultaDetalleResponse> concluirConsulta(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idConsulta,
            @RequestBody @Valid ConsultaConcluirRequest request
    ){
        return ResponseEntity.ok(consultaServ.concluirConsulta(usuario, idConsulta, request));
    }

    @Operation(summary = "Cancelar consulta medica", description = "**ROLES:** ADMIN")
    @PatchMapping("/{idConsulta}/cancelar")
    public ResponseEntity<ConsultaDetalleResponse> cancelarConsulta(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idConsulta
    ){
        return ResponseEntity.ok(consultaServ.cancelarConsulta(usuario, idConsulta));
    }

    @Operation(summary = "Actualizar datos o reagendar consulta medica", description = "**ROLES:** ADMIN")
    @PutMapping("/{idConsulta}")
    public ResponseEntity<ConsultaResponse> actualizarDatos(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idConsulta,
            @Valid @RequestBody ConsultaRequest request
    ){
        return ResponseEntity.ok(consultaServ.actualizarCita(usuario, idConsulta, request));
    }

    @Operation(summary = "Obtener Historial completo de las consultas medicas de la familia", description = "**ROLES:** ADMIN")
    @GetMapping
    public ResponseEntity<Page<ConsultaResponse>> obtenerPorFiltros(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @Parameter(description = "Identicador del integrante de la familia")
            @RequestParam(required = false) Long idIntegrante,
            @Parameter(description = "Indentificador del medico de la familia")
            @RequestParam(required = false) Long idMedico,
            @Parameter(description = "Opciones para obtener aquellas consultas por estado que se encuentre")
            @RequestParam(required = false) EstadoConsultaEnum estado,
            @Parameter(description = "Fecha inicial del rango de búsqueda para filtrar consultas (formato ISO: YYYY-MM-DD)")
            @RequestParam(required = false) LocalDate fechaDesde,
            @Parameter(description = "Fecha final del rango de búsqueda para filtrar consultas (formato ISO: YYYY-MM-DD)")
            @RequestParam(required = false) LocalDate fechaHasta,
            @Parameter(description = "Texto libre para buscar coincidencias parciales en el motivo o diagnóstico de la consulta")
            @RequestParam(required = false, defaultValue = "") String busqueda,
            @ParameterObject @PageableDefault(size = 15, sort = "fechaConsulta", direction = Sort.Direction.ASC) Pageable pageable

    ){
        return ResponseEntity.ok(consultaServ.obtenerPorFiltros(
                usuario,idIntegrante,idMedico,estado, fechaDesde,
                fechaHasta, busqueda, pageable
        ));
    }

    @Operation(summary = "Eliminar consulta medica", description = "**ROLES:** ADMIN")
    @DeleteMapping("/{idConsulta}")
    public ResponseEntity<Void> eliminarPorId(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idConsulta
    ){
        consultaServ.eliminarPorId(usuario, idConsulta);
        return ResponseEntity.noContent().build();
    }
}