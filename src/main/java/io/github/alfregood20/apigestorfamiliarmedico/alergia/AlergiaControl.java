package io.github.alfregood20.apigestorfamiliarmedico.alergia;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.dto.AlergiaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.alergia.enums.TipoAlergiaEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alergias")
@AllArgsConstructor
@Tag(name = "Catalogo de alergia", description = "Gestión del catálogo global de alergias y su asociación con los integrantes de la familia")
public class AlergiaControl {

    private final AlergiaServc alergiaServc;

    @Operation(
            summary = "Crea nueva alergia para integrantes",
            description = "Endpoints solo es para insertar datos, nadien podra acceder"
    )
    @PostMapping
    public ResponseEntity<Alergia> crearNuevo(
            @Valid @RequestBody AlergiaRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(alergiaServc.crearNuevo(request));
    }

    @Operation(
            summary = "Eliminar alergia de integrantes",
            description = "Endpoints solo es para eliminar datos, nadien podra acceder"
    )
    @DeleteMapping
    public ResponseEntity<Void> eliminarPorId(@PathVariable Long idAlergia){
        alergiaServc.eliminarPorId(idAlergia);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtener todo las alergias que puede tener un integrante de la familia",
            description = "**ROLES:** ADMIN y CUIDADOR"
    )
    @GetMapping
    public ResponseEntity<List<Alergia>> listado(
            @Parameter(description = "Texto que comienza con el nombre de la alergia")
            @RequestParam(required = false, defaultValue = "") String nombre,
            @Parameter(description = "Opciones para obtener alergias su tipo")
            @RequestParam(required = false) TipoAlergiaEnum tipo){
        return ResponseEntity.ok(alergiaServc.obtenerPorFiltro(nombre, tipo));
    }

}