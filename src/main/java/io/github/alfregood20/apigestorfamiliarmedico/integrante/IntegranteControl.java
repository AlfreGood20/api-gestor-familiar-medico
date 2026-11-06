package io.github.alfregood20.apigestorfamiliarmedico.integrante;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.dto.IntegranteAlergiaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteDetalleResponse;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteRequest;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/integrantes")
@RequiredArgsConstructor
@Tag(name = "Gestion de integrantes", description = "Endpoints para gestion de los integrantes del usuario aqui podras hacer diferentes operaciones.")
public class IntegranteControl {

    private final IntegranteServc integranteServc;

    @Operation(
            summary = "Crea nuevo integrante de la familia",
            description = "**ROLES:** ADMIN"
    )
    @PostMapping
    public ResponseEntity<IntegranteResponse> crear(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @Valid @RequestBody IntegranteRequest request
    ){
        return new ResponseEntity<IntegranteResponse>(integranteServc.crear(usuario, request), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Obtener todo los integrantes de la familia",
            description = "**ROLES:** ADMIN y CUIDADOR"
    )
    @GetMapping
    public ResponseEntity<List<IntegranteResponse>> obtenerListado(
            @AuthenticationPrincipal CustomUserDetails usuario
    ){
        return ResponseEntity.ok(integranteServc.obtenerListado(usuario));
    }

    @Operation(
            summary = "Eliminar integrante de la familia",
            description = "**ROLE:** ADMIN"
    )
    @DeleteMapping("/{idIntegrante}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal CustomUserDetails usuario, @PathVariable Long idIntegrante
    ){
        integranteServc.eliminar(usuario, idIntegrante);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Añadir alergia a un integrante de la familia",
            description = "**ROLE:** ADMIN"
    )
    @PostMapping("/{idIntegrante}/alergias")
    public ResponseEntity<IntegranteDetalleResponse> asignarAlergia(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idIntegrante,
            @RequestBody @Valid IntegranteAlergiaRequest request
    ){
        return new ResponseEntity<IntegranteDetalleResponse>(integranteServc.asignarleAlergia(usuario, idIntegrante, request), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Obtener detalle de un integrante de la familia",
            description = "**ROLES:** ADMIN y CUIDADOR"
    )
    @GetMapping("/{idIntegrante}")
    public ResponseEntity<IntegranteDetalleResponse> obtenerDetalle(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idIntegrante
    ){
        return ResponseEntity.ok(integranteServc.obtenerDetalle(usuario, idIntegrante));
    }

    @Operation(
            summary = "Actualizar los datos de un integrante de la familia",
            description = "**ROLES:** ADMIN"
    )
    @PutMapping("/{idIntegrante}")
    public ResponseEntity<IntegranteDetalleResponse> actulizarDatos(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idIntegrante,
            @RequestBody @Valid IntegranteRequest request
    ){
        return ResponseEntity.ok(integranteServc.actualizarDatos(usuario, idIntegrante, request));
    }
}