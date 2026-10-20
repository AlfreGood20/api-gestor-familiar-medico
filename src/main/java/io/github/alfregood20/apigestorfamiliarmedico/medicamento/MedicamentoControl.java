package io.github.alfregood20.apigestorfamiliarmedico.medicamento;

import io.github.alfregood20.apigestorfamiliarmedico.medicamento.dto.MedicamentoRequest;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.dto.MedicamentoResponse;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.FormaFarmaceuticaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.ViaAdministracionEnum;
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
public class MedicamentoControl {

    private final MedicamentoServc medicamentoServc;

    @PostMapping
    public ResponseEntity<MedicamentoResponse> crear(@RequestBody MedicamentoRequest request){
        return new ResponseEntity<>(medicamentoServc.crear(request), HttpStatus.CREATED);
    }

    @GetMapping("/{idMedicamento}")
    public ResponseEntity<MedicamentoResponse> obtenerPorId(@PathVariable Long idMedicamento){
        return ResponseEntity.ok(medicamentoServc.obtenerPorId(idMedicamento));
    }

    @GetMapping
    public ResponseEntity<Page<MedicamentoResponse>> listadoPorFiltros(
            @RequestParam(required = false) String nombreComercial,
            @RequestParam(required = false) String sustanciaActiva,
            @RequestParam(required = false) FormaFarmaceuticaEnum formaFarmaceutica,
            @RequestParam(required = false) ViaAdministracionEnum viaAdministracion,
            @RequestParam(defaultValue = "true") boolean activo,
            @ParameterObject @PageableDefault(size = 15, sort = "nombreComercial", direction = Sort.Direction.ASC)  Pageable pageable
    ){
        return ResponseEntity.ok(
                medicamentoServc.obtenerPorFiltros(nombreComercial, sustanciaActiva, formaFarmaceutica, viaAdministracion, activo, pageable)
        );
    }
}