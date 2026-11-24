package io.github.alfregood20.apigestorfamiliarmedico.receta;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.receta.dto.RecetaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.receta.dto.RecetaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.receta.dto.RecetaUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recetas")
@RequiredArgsConstructor
@Tag(
        name = "Gestion de recetas medicas",
        description = "Endpoints para registrar, consultar, actualizar, finalizar y eliminar recetas médicas junto con su calendario de medicamentos."
)
public class RecetaControl {

    private final RecetaServ recetaServ;

    @Operation(
            summary = "Registrar nueva receta médica",
            description = "Crea una receta médica para un integrante de la familia (opcionalmente vinculada a una consulta), guarda sus medicamentos prescritos y genera automáticamente todas las tomas programadas."
    )
    @PostMapping
    public ResponseEntity<RecetaResponse> crearReceta(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @Valid @RequestBody RecetaRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recetaServ.crearReceta(usuario, request));
    }

    @Operation(
            summary = "Listar recetas médicas con filtros",
            description = "Obtiene el historial paginado de recetas médicas de la familia, permitiendo filtrar por integrante, consulta asociada, estado del tratamiento (activa/finalizada) o búsqueda por medicamento e indicaciones."
    )
    @GetMapping
    public ResponseEntity<Page<RecetaResponse>> obtenerPorFiltros(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @RequestParam(required = false) Long idIntegrante,
            @RequestParam(required = false) Long idConsulta,
            @RequestParam(required = false) Boolean activa,
            @RequestParam(required = false, defaultValue = "") String busqueda,
            @ParameterObject @PageableDefault(size = 15, sort = "fechaEmision", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(recetaServ.obtenerPorFiltros(
                usuario, idIntegrante, idConsulta, activa, busqueda, pageable
        ));
    }

    @Operation(
            summary = "Obtener detalle de una receta médica",
            description = "Devuelve la ficha completa de una receta específica (integrante, consulta asociada, indicaciones, medicamentos prescritos y calendario de tomas programadas) validando que pertenezca a la familia."
    )
    @GetMapping("/{idReceta}")
    public ResponseEntity<RecetaResponse> obtenerPorId(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idReceta
    ) {
        return ResponseEntity.ok(recetaServ.obtenerPorId(usuario, idReceta));
    }

    @Operation(
            summary = "Actualizar cabecera de receta médica",
            description = "Modifica la fecha de emisión, las indicaciones generales o la consulta médica vinculada de una receta existente sin alterar su lista de medicamentos ni las tomas programadas."
    )
    @PutMapping("/{idReceta}")
    public ResponseEntity<RecetaResponse> actualizarReceta(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idReceta,
            @Valid @RequestBody RecetaUpdate request
    ) {
        return ResponseEntity.ok(recetaServ.actualizarReceta(usuario, idReceta, request));
    }

    @Operation(
            summary = "Finalizar o suspender tratamiento de una receta",
            description = "Marca la receta como inactiva (activa = false) y cambia a CANCELADA todas sus tomas programadas que aún estén en estado PENDIENTE, conservando el historial de las tomas ya realizadas."
    )
    @PatchMapping("/{idReceta}/finalizar")
    public ResponseEntity<RecetaResponse> finalizarReceta(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idReceta
    ) {
        return ResponseEntity.ok(recetaServ.finalizarReceta(usuario, idReceta));
    }

    @Operation(
            summary = "Eliminar receta médica",
            description = "Elimina físicamente una receta médica registrada por error, borrando en cascada sus medicamentos prescritos y todas sus tomas programadas asociadas."
    )
    @DeleteMapping("/{idReceta}")
    public ResponseEntity<Void> eliminarReceta(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idReceta
    ) {
        recetaServ.eliminarReceta(usuario, idReceta);
        return ResponseEntity.noContent().build();
    }
}