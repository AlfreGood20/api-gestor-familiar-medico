package io.github.alfregood20.apigestorfamiliarmedico.alergia;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.enums.TipoAlergiaEnum;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alergia")
@AllArgsConstructor
public class AlergiaControl {

    private final AlergiaServc alergiaServc;

    @GetMapping
    public ResponseEntity<List<Alergia>> listado(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) TipoAlergiaEnum tipo){
        return ResponseEntity.ok(alergiaServc.obtenerPorFiltro(nombre, tipo));
    }

}