package io.github.alfregood20.apigestorfamiliarmedico.integrante;


import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteRequest;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/integrantes")
@RequiredArgsConstructor
public class IntegranteControl {

    private final IntegranteServc integranteServc;

    @PostMapping
    public ResponseEntity<IntegranteResponse> crear(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @Valid @RequestBody IntegranteRequest request){
        return new ResponseEntity<IntegranteResponse>(integranteServc.crear(usuario, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<IntegranteResponse>> obtenerListado(@AuthenticationPrincipal CustomUserDetails usuario){
        return ResponseEntity.ok(integranteServc.obtenerListado(usuario));
    }

    @DeleteMapping("/{idIntegrante}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal CustomUserDetails usuario, @PathVariable Long idIntegrante){
        integranteServc.eliminar(usuario, idIntegrante);
        return ResponseEntity.noContent().build();
    }
}
