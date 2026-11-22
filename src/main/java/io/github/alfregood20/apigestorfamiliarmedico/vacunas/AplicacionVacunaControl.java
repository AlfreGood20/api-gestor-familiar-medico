package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.AplicacionVacunaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.AplicacionVacunaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.AplicacionVacunaUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aplicaciones-vacunas")
@RequiredArgsConstructor
@Tag(
        name = "Aplicaciones de Vacunas",
        description = "Endpoints para registrar, actualizar y eliminar el historial de dosis de vacunas aplicadas a los integrantes de la familia."
)
public class AplicacionVacunaControl {

    private final AplicacionVacunaServ aplicacionVacunaServ;

    @PostMapping
    @Operation(
            summary = "Registrar aplicación de vacuna",
            description = "Registra una nueva dosis aplicada a un integrante de la familia, validando el límite total de dosis, duplicados, fecha de nacimiento e intervalos mínimos entre dosis."
    )
    public ResponseEntity<AplicacionVacunaResponse> aplicarNuevaVacuna(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @RequestBody @Valid AplicacionVacunaRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(aplicacionVacunaServ.crearNuevo(usuario, request));
    }

    @PutMapping("/{idAplicacion}")
    @Operation(
            summary = "Actualizar aplicación de vacuna",
            description = "Actualiza la fecha de aplicación, el número de lote o el lugar donde se aplicó una dosis previamente registrada, manteniendo la coherencia cronológica con dosis anteriores y posteriores."
    )
    public ResponseEntity<AplicacionVacunaResponse> actualizarDatos(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idAplicacion,
            @RequestBody @Valid AplicacionVacunaUpdate request
    ){
        return ResponseEntity.ok(aplicacionVacunaServ.actualizar(usuario, idAplicacion, request));
    }

    @DeleteMapping("/{idAplicacion}")
    @Operation(
            summary = "Eliminar aplicación de vacuna",
            description = "Elimina el registro de una aplicación de vacuna por error de captura, devolviendo automáticamente esa dosis a su estado calculado en la cartilla."
    )
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idAplicacion
    ){
        aplicacionVacunaServ.eliminar(usuario, idAplicacion);
        return ResponseEntity.noContent().build();
    }

}