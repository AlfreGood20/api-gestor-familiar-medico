package io.github.alfregood20.apigestorfamiliarmedico.auth;

import io.github.alfregood20.apigestorfamiliarmedico.auth.dto.*;
import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Gestion de autenticacion", description = "Endpoints para el inicio de sesión, registro de nuevas cuentas familiares y consulta del perfil actual.")
public class AuthControl {

    private final AuthServic authServic;

    @Operation(
            summary = "Registar familia y administrador",
            description = "Este endpoint es **publico** para el resgitro de usuarios nuevo como administrador de la familia"
    )
    @PostMapping("/registro")
    public ResponseEntity<RegistroResponse> registro (@Valid @RequestBody RegistroRequest request){
        return new ResponseEntity<RegistroResponse>(authServic.registrar(request), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Iniciar sesion",
            description = "Este endpoint es **publico** para iniciar sesion al sistema"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest req, HttpServletResponse res){
        return ResponseEntity.ok(authServic.login(request, req, res));
    }

    @Operation(
            summary = "Cerrar sesion",
            description = "Este endpoint es para cerrar sesion del sistema todo aquel que este autenticado podra cerrar sesion"
    )
    @PostMapping("/logout")
    public void logout() {};

    @Operation(
            summary = "Obtener mi perfil",
            description = "Este endpoint es para obtener la informacion del perfil aquel que este autenticado"
    )
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal CustomUserDetails usuario){
        return ResponseEntity.ok(authServic.autenticado(usuario));
    }

    @Operation(
            summary = "Actualizar mi perfil",
            description = "Permite al usuario autenticado actualizar su nombre, apellidos, teléfono y correo electrónico."
    )
    @PutMapping("/me")
    public ResponseEntity<MeResponse> actualizarMe(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @Valid @RequestBody PerfilUpdate request
    ) {
        return ResponseEntity.ok(authServic.actualizarPerfil(usuario, request));
    }

    @Operation(
            summary = "Cambiar mi contraseña",
            description = "Permite al usuario autenticado cambiar su contraseña validando previamente su contraseña actual."
    )
    @PatchMapping("/me/password")
    public ResponseEntity<Void> cambiarContrasena(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @Valid @RequestBody PerfilContrasenaUpdate request
    ) {
        authServic.cambiarContrasena(usuario, request);
        return ResponseEntity.noContent().build();
    }
}