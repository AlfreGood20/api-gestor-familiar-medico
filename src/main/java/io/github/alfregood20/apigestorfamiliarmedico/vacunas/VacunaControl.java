package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.VacunaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.VacunaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vacunas")
@RequiredArgsConstructor
@Tag(name = "Catalogo vacunas", description = "Endpoints para catalogo de vacunas de integrantes")
public class VacunaControl {

    private final VacunaServ vacunaServ;

    @Operation(summary = "crear nueva vacuna", description = "Este endpoints es para poblar la tabla ningun rol podra acceder")
    @PostMapping
    public ResponseEntity<VacunaResponse> crearNuevo(
            @RequestBody @Valid VacunaRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(vacunaServ.crearNuevo(request));
    }

    @Operation(summary = "Obtener detalle de una vacuna por id", description = "**ROLES:** ADMIN y CUIDADOR")
    @GetMapping("/{idVacuna}")
    public ResponseEntity<VacunaResponse> obtenerPorId(
            @PathVariable Long idVacuna
    ){
        return ResponseEntity.ok(vacunaServ.obtenerPorId(idVacuna));
    }

    @Operation(summary = "Obtener catalogo de vacunas por filtros", description = "**ROLES:** ADMIN y CUIDADOR")
    @GetMapping
    public ResponseEntity<Page<VacunaResponse>> obtenerPorFiltro(
            @Parameter(description = "Texto que busca por nombre, descripcion, contraindicaciones o enfermadad que previene")
            @RequestParam(required = false, defaultValue = "") String busqueda,
            @Parameter(description = "Opcion para obtener aquellos que estan activos por defecto esta `true`")
            @RequestParam(required = false, defaultValue = "true") boolean activo,
            @Parameter(description = "Cantidad de dosis totales que puede tener una vacuna")
            @RequestParam(required = false, defaultValue = "0") int dosisTotales,
            @PageableDefault(size = 15, sort = "nombre", direction = Sort.Direction.ASC) @ParameterObject Pageable pageable
    ){
        return ResponseEntity.ok(vacunaServ.obtenerPorFiltro(busqueda, activo, dosisTotales, pageable));
    }

    @Operation(summary = "Eliminar vacuna por id", description = "Endpoints para administracion de la tabla, nadien podra acceder")
    @DeleteMapping("/{idVacuna}")
    public ResponseEntity<Void> eliminarPorId(
            @PathVariable Long idVacuna
    ){
        vacunaServ.eliminarPorId(idVacuna);
        return ResponseEntity.noContent().build();
    }
}