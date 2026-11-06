package io.github.alfregood20.apigestorfamiliarmedico.alergia;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.enums.TipoAlergiaEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alergias")
@AllArgsConstructor
@Tag(name = "Catalogo de alergia", description = "Gestión del catálogo global de alergias y su asociación con los integrantes de la familia")
public class AlergiaControl {

    private final AlergiaServc alergiaServc;

    @Operation(
            summary = "Obtener todo las alergias que puede tener un integrante de la familia",
            description = "**ROLES:** ADMIN y CUIDADOR"
    )
    @GetMapping
    public ResponseEntity<List<Alergia>> listado(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) TipoAlergiaEnum tipo){
        return ResponseEntity.ok(alergiaServc.obtenerPorFiltro(nombre, tipo));
    }

}