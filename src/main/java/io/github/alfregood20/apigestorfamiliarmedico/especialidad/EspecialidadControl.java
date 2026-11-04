package io.github.alfregood20.apigestorfamiliarmedico.especialidad;

import io.github.alfregood20.apigestorfamiliarmedico.especialidad.dto.EspecilidadRequest;
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

    @PostMapping
    public ResponseEntity<Especialidad> crearNuevo(
            @Valid @RequestBody EspecilidadRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(especilidadServ.crearNuevo(request));
    }

    @GetMapping
    public ResponseEntity<List<Especialidad>> listado(){
        return ResponseEntity.ok(especilidadServ.obtenerListado());
    }

    @DeleteMapping("/{idEspecialidad}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Long idEspecialidad){
        especilidadServ.borrarPorId(idEspecialidad);
        return ResponseEntity.noContent().build();
    }
}
