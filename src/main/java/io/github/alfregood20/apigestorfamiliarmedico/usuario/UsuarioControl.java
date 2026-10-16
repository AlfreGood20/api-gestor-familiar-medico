package io.github.alfregood20.apigestorfamiliarmedico.usuario;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.CuidadorRequest;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.CuidadorResponse;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.EstadoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioControl {

    private final UsuarioServic usuarioServic;

    @PostMapping("/cuidadores")
    public ResponseEntity<CuidadorResponse> crearNuevoCuidador(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @Valid @RequestBody CuidadorRequest request
    ){
        return new ResponseEntity<CuidadorResponse>(usuarioServic.crearCuidador(usuario, request), HttpStatus.CREATED);
    }

    @GetMapping("/cuidadores")
    public ResponseEntity<List<CuidadorResponse>> listadoCuidadores(@AuthenticationPrincipal CustomUserDetails usuario){
        return ResponseEntity.ok(usuarioServic.obtenerCuidadores(usuario));
    }

    @PatchMapping("/cuidadores/{idCuidador}/estado")
    public ResponseEntity<CuidadorResponse> cambiarEstadoCuidador(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idCuidador,
            @RequestBody EstadoRequest request
    ){
        return ResponseEntity.ok(usuarioServic.cambiarEstadoCuidador(usuario, idCuidador, request));
    }

    @DeleteMapping("/cuidadores/{idCuidador}")
    public ResponseEntity<Void> eliminarCuidador(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idCuidador
    ){
        usuarioServic.eliminarCuidador(usuario, idCuidador);
        return ResponseEntity.noContent().build();
    }
}
