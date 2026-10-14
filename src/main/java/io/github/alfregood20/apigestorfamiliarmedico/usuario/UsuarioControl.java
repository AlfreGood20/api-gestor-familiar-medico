package io.github.alfregood20.apigestorfamiliarmedico.usuario;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.CuidadorRequest;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.CuidadorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioControl {

    private final UsuarioServic usuarioServic;

    @PostMapping("/cuidadores")
    public ResponseEntity<CuidadorResponse> crearNuevoCuidador(
            @AuthenticationPrincipal CustomUserDetails usuario, @Valid @RequestBody CuidadorRequest request){
        return new ResponseEntity<CuidadorResponse>(usuarioServic.crearCuidador(usuario, request), HttpStatus.CREATED);
    }
}
