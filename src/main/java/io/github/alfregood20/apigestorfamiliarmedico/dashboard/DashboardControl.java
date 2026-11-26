package io.github.alfregood20.apigestorfamiliarmedico.dashboard;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.dashboard.dto.DashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(
        name = "Dashboard Familiar",
        description = "Endpoint consolidado para obtener los KPIs del hogar, tomas programadas del día, próximas consultas y resumen de integrantes."
)
public class DashboardControl {

    private final DashboardServ dashboardServ;

    @Operation(
            summary = "Obtener resumen general del dashboard",
            description = "Devuelve en una sola petición los indicadores clave (KPIs) de la familia autentificada, las tomas programadas de hoy, las próximas citas médicas y el listado de integrantes."
    )
    @GetMapping
    public ResponseEntity<DashboardResponse> obtenerEstadistica(
            @AuthenticationPrincipal CustomUserDetails usuario
    ) {
        return ResponseEntity.ok(dashboardServ.dashboard(usuario));
    }
}