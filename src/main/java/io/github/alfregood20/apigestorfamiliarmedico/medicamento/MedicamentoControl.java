package io.github.alfregood20.apigestorfamiliarmedico.medicamento;

import io.github.alfregood20.apigestorfamiliarmedico.medicamento.dto.MedicamentoRequest;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.dto.MedicamentoResponse;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.FormaFarmaceuticaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.ViaAdministracionEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medicamentos")
@AllArgsConstructor
@Tag(name = "Catalogo de medicamentos", description = "Endpoints para catalogo de medicamentos, crear y obtener")
public class MedicamentoControl {

    private final MedicamentoServc medicamentoServc;

    @Operation(
            summary = "Crea nuevo medicamento",
            description = "Este endpoint nadien podra acceder solo es para insertar datos"
    )
    @PostMapping
    public ResponseEntity<MedicamentoResponse> crear(@Valid @RequestBody MedicamentoRequest request){
        return new ResponseEntity<>(medicamentoServc.crear(request), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Obtener el detalle de un medicamento por id",
            description = "**ROLES:** ADMIN y CUIDADOR"
    )
    @GetMapping("/{idMedicamento}")
    public ResponseEntity<MedicamentoResponse> obtenerPorId(@PathVariable Long idMedicamento){
        return ResponseEntity.ok(medicamentoServc.obtenerPorId(idMedicamento));
    }

    @Operation(
            summary = "Obtener el listado de medicamtos por diferentes filtros",
            description = "**ROLES:** ADMIN y CUIDADOR"
    )
    @GetMapping
    public ResponseEntity<Page<MedicamentoResponse>> listadoPorFiltros(
            @Parameter(description = "Texto que comienze con el nombre comercial del medicamento")
            @RequestParam(required = false, defaultValue = "") String nombreComercial,
            @Parameter(description = "Texto que comienze con la sustancia activa")
            @RequestParam(required = false, defaultValue = "") String sustanciaActiva,
            @Parameter(description = "Opciones para obtener los medicamentos cuyas forma farmaceutica")
            @RequestParam(required = false) FormaFarmaceuticaEnum formaFarmaceutica,
            @Parameter(description = "Opciones para obtener los medicamentos cuyas forma de consumir el medicamento")
            @RequestParam(required = false) ViaAdministracionEnum viaAdministracion,
            @Parameter(description = "Opciones para obtener los medicamentos ya sea activos `true` o inactivos `false`")
            @RequestParam(defaultValue = "true") boolean activo,
            @ParameterObject @PageableDefault(size = 15, sort = "nombreComercial", direction = Sort.Direction.ASC)  Pageable pageable
    ){
        return ResponseEntity.ok(
                medicamentoServc.obtenerPorFiltros(nombreComercial, sustanciaActiva, formaFarmaceutica, viaAdministracion, activo, pageable)
        );
    }
}