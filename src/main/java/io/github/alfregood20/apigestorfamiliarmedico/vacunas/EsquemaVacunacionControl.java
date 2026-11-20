package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.CartillaVacunacionOficialResponse;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.DosisResponse;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.EsquemaVacunacionRequest;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.EsquemaVacunacionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/esquemas-vacunacion")
@RequiredArgsConstructor
@Tag(
        name = "Catálogo de Esquemas de Vacunación",
        description = "Gestión y consulta de la Cartilla Nacional de Vacunación oficial (dosis, edades recomendadas e intervalos)"
)
public class EsquemaVacunacionControl {

    private final EsquemaVacunacionServ esquemaVacunacionServ;

    @Operation(
            summary = "Registrar regla en el esquema oficial",
            description = "Agrega una nueva dosis o refuerzo al catálogo oficial de vacunación. (uso interno para poblar catálogo)"
    )
    @PostMapping
    public ResponseEntity<EsquemaVacunacionResponse> crearNuevo(
            @RequestBody @Valid EsquemaVacunacionRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(esquemaVacunacionServ.crearNuevo(request));
    }

    @Operation(
            summary = "Consultar cartilla oficial de vacunación",
            description = "Devuelve el esquema oficial agrupado por vacuna con sus dosis ordenadas cronológicamente y paginado. **ROLES:** ADMIN y CUIDADOR"
    )
    @GetMapping
    public ResponseEntity<Page<CartillaVacunacionOficialResponse>> obtenerCartillaOficial(
            @Parameter(description = "Texto para obtener por el nombre de la vacuna")
            @RequestParam(required = false, defaultValue = "") String nombreVacuna,
            @ParameterObject @PageableDefault(size = 6) Pageable pageable
    ){
        return ResponseEntity.ok(esquemaVacunacionServ.obtenerCartillaOficial(nombreVacuna, pageable));
    }

    @Operation(
            summary = "Obtener dosis disponibles por vacuna",
            description = "Devuelve la lista ordenada de dosis y refuerzos configurados para una vacuna específica. Ideal para selectores en cascada. **ROLES:** ADMIN y CUIDADOR"
    )
    @GetMapping("/vacuna/{idVacuna}")
    public ResponseEntity<List<DosisResponse>> obtenerDosisPorVacunas(
            @PathVariable @RequestParam Long idVacuna
    ){
        return ResponseEntity.ok(esquemaVacunacionServ.obtenerDosisPorIdVacuna(idVacuna));
    }
}