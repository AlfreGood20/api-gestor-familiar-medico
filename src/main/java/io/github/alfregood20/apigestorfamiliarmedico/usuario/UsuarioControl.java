package io.github.alfregood20.apigestorfamiliarmedico.usuario;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.CuidadorRequest;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.CuidadorResponse;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.CuidadorUpdate;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.EstadoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Gestion de usuario",
        description = "Endpoints gestion de usuarios, gestionaras los cuidadores con los diferentes operaciones"
)
public class UsuarioControl {

    private final UsuarioServic usuarioServic;

    @Operation(
            summary = "Crea nuevo cuidador para la familia",
            description = "**ROLES:** ADMIN"
    )
    @PostMapping("/cuidadores")
    public ResponseEntity<CuidadorResponse> crearNuevoCuidador(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @Valid @RequestBody CuidadorRequest request
    ){
        return new ResponseEntity<CuidadorResponse>(usuarioServic.crearCuidador(usuario, request), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Obtener todo los cuidadores de la familia",
            description = "**ROLES:** ADMIN"
    )
    @GetMapping("/cuidadores")
    public ResponseEntity<List<CuidadorResponse>> listadoCuidadores(@AuthenticationPrincipal CustomUserDetails usuario){
        return ResponseEntity.ok(usuarioServic.obtenerCuidadores(usuario));
    }

    @Operation(
            summary = "Actualizar el estado del cuidador por id de la familia",
            description = "**ROLES:** ADMIN"
    )
    @PatchMapping("/cuidadores/{idCuidador}/estado")
    public ResponseEntity<CuidadorResponse> cambiarEstadoCuidador(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idCuidador,
            @RequestBody EstadoRequest request
    ){
        return ResponseEntity.ok(usuarioServic.cambiarEstadoCuidador(usuario, idCuidador, request));
    }

    @Operation(
            summary = "Eliminar cuidador de la familia por id",
            description = "**ROLES:** ADMIN"
    )
    @DeleteMapping("/cuidadores/{idCuidador}")
    public ResponseEntity<Void> eliminarCuidador(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idCuidador
    ){
        usuarioServic.eliminarCuidador(usuario, idCuidador);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtener detalle de un cuidador",
            description = "Devuelve los datos personales, estado y fecha de registro de un cuidador perteneciente a la familia del administrador."
    )
    @GetMapping("/cuidadores/{idCuidador}")
    public ResponseEntity<CuidadorResponse> obtenerDetalleCuidador(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idCuidador
    ) {
        return ResponseEntity.ok(usuarioServic.obtenerDetalleCuidador(usuario, idCuidador));
    }

    @Operation(
            summary = "Actualizar datos de un cuidador",
            description = "Permite modificar el nombre, apellidos, teléfono y correo electrónico de un cuidador de la familia."
    )
    @PutMapping("/cuidadores/{idCuidador}")
    public ResponseEntity<CuidadorResponse> actualizarCuidador(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idCuidador,
            @Valid @RequestBody CuidadorUpdate request
    ) {
        return ResponseEntity.ok(usuarioServic.actualizarCuidador(usuario, idCuidador, request));
    }
}
