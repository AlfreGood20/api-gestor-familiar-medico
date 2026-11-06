package io.github.alfregood20.apigestorfamiliarmedico.especialidad;

import io.github.alfregood20.apigestorfamiliarmedico.especialidad.dto.EspecilidadRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/especialidades")
@RequiredArgsConstructor
@Tag(name = "Catalogo especialidad de medicos", description = "Endpoints para especialidad de los medicos, crear, obtener y eliminar especialidad.")
public class EspecialidadControl {

    private final EspecilidadServ especilidadServ;

    @Operation(
            summary = "Crea nuevo especialidad para medicos",
            description = "Este endpoints solo es para insertar datos, ningun rol podra acceder"
    )
    @PostMapping
    public ResponseEntity<Especialidad> crearNuevo(
            @Valid @RequestBody EspecilidadRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(especilidadServ.crearNuevo(request));
    }

    @Operation(
            summary = "Obtener todo especialidades de los medicos",
            description = "**ROLE:** ADMIN"
    )
    @GetMapping
    public ResponseEntity<List<Especialidad>> listado(){
        return ResponseEntity.ok(especilidadServ.obtenerListado());
    }

    @Operation(
            summary = "Eliminar especialidad de medicos por id",
            description = "Este endpoints solo eliminar datos ingresados por error, ningun rol podra acceder"
    )
    @DeleteMapping("/{idEspecialidad}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Long idEspecialidad){
        especilidadServ.borrarPorId(idEspecialidad);
        return ResponseEntity.noContent().build();
    }
}
