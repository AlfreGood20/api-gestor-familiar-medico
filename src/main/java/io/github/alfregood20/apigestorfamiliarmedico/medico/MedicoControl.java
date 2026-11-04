package io.github.alfregood20.apigestorfamiliarmedico.medico;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.medico.dto.MedicoRequest;
import io.github.alfregood20.apigestorfamiliarmedico.medico.dto.MedicoResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
@RequiredArgsConstructor
@Tag(name = "Gestion de medicos", description = "Endpoints para gestionar los medicos de la familia, crear, obtener y eliminar")
public class MedicoControl {

    private final MedicoServ medicoServ;

    @PostMapping
    public ResponseEntity<MedicoResponse> crearNuevo(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @Valid @RequestBody MedicoRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(medicoServ.crearNuevo(usuario, request));
    }

    @GetMapping
    public ResponseEntity<List<MedicoResponse>> listado(
            @AuthenticationPrincipal CustomUserDetails usuario
    ){
        return ResponseEntity.ok(medicoServ.listado(usuario));
    }

    @GetMapping("/{idMedico}")
    public ResponseEntity<MedicoResponse> obtenerPorId(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idMedico
    ){
        return ResponseEntity.ok(medicoServ.obtenerPorId(usuario, idMedico));
    }

    @DeleteMapping("/{idMedico}")
    public ResponseEntity<Void> eliminarPorId(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idMedico
    ){
        medicoServ.eliminarPorId(usuario, idMedico);
        return ResponseEntity.noContent().build();
    }

}