package io.github.alfregood20.apigestorfamiliarmedico.alerta;

import io.github.alfregood20.apigestorfamiliarmedico.alerta.dto.AlertaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.alerta.enums.TipoAlertaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/alertas")
@RequiredArgsConstructor
@Tag(
        name = "Gestión de Alertas",
        description = "Endpoints para consultar las notificaciones médicas y del botiquín de la familia, obtener el contador de alertas sin leer y marcarlas como leídas o eliminarlas."
)
public class AlertaControl {

    private final AlertaServ alertaServ;

    @Operation(
            summary = "Listar alertas de la familia",
            description = "Obtiene de forma paginada las notificaciones ordenadas de más reciente a más antigua, permitiendo filtrar opcionalmente por integrante, tipo de alerta o únicamente las no leídas."
    )
    @GetMapping
    public ResponseEntity<Page<AlertaResponse>> obtenerPorFiltros(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @RequestParam(required = false) Long idIntegrante,
            @RequestParam(required = false) TipoAlertaEnum tipo,
            @RequestParam(required = false, defaultValue = "false") boolean soloNoLeidas,
            @ParameterObject @PageableDefault(size = 15, sort = "fechaGeneracion", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(alertaServ.obtenerPorFiltros(usuario, idIntegrante, tipo, soloNoLeidas, pageable));
    }

    @Operation(
            summary = "Contar alertas sin leer",
            description = "Devuelve el total de alertas activas que aún no han sido leídas por la familia para alimentar el indicador de notificaciones."
    )
    @GetMapping("/contador")
    public ResponseEntity contarNoLeidas(
            @AuthenticationPrincipal CustomUserDetails usuario
    ) {
        return ResponseEntity.ok(Map.of("noLeidas", alertaServ.contarNoLeidas(usuario)));
    }

    @Operation(
            summary = "Marcar una alerta como leída",
            description = "Cambia el estado de lectura de una alerta específica a leída (true) al ser consultada por el usuario."
    )
    @PatchMapping("/{idAlerta}/leer")
    public ResponseEntity<AlertaResponse> marcarComoLeida(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idAlerta
    ) {
        return ResponseEntity.ok(alertaServ.marcarComoLeida(usuario, idAlerta));
    }

    @Operation(
            summary = "Marcar todas las alertas como leídas",
            description = "Actualiza en una sola operación todas las alertas no leídas de la familia a estado leído (true)."
    )
    @PatchMapping("/leer-todas")
    public ResponseEntity marcarTodasComoLeidas(
            @AuthenticationPrincipal CustomUserDetails usuario
    ) {
        int actualizadas = alertaServ.marcarTodasComoLeidas(usuario);
        return ResponseEntity.ok(Map.of("actualizadas", actualizadas));
    }

    @Operation(
            summary = "Eliminar una alerta",
            description = "Elimina permanentemente una notificación del historial de alertas de la familia."
    )
    @DeleteMapping("/{idAlerta}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idAlerta
    ) {
        alertaServ.eliminar(usuario, idAlerta);
        return ResponseEntity.noContent().build();
    }

}