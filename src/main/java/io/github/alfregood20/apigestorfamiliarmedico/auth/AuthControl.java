package io.github.alfregood20.apigestorfamiliarmedico.auth;

import io.github.alfregood20.apigestorfamiliarmedico.auth.dto.*;
import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
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
public class AuthControl {

    private final AuthServic authServic;

    @PostMapping("/registro")
    public ResponseEntity<RegistroResponse> registro (@Valid @RequestBody RegistroRequest request){
        return new ResponseEntity<RegistroResponse>(authServic.registrar(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest req, HttpServletResponse res){
        return ResponseEntity.ok(authServic.login(request, req, res));
    }

    @PostMapping("/logout")
    public void logout() {};

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal CustomUserDetails usuario){
        return ResponseEntity.ok(authServic.autenticado(usuario));
    }
}