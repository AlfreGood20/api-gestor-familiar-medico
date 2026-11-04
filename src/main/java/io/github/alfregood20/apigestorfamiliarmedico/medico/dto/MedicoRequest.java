package io.github.alfregood20.apigestorfamiliarmedico.medico.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.Set;

public record MedicoRequest(

        @NotBlank(message = "Es obligatorio")
        String nombre,
        @NotBlank(message = "Es obligatorio")
        String apellidoPaterno,
        @NotBlank(message = "Es obligatorio")
        String apellidoMaterno,
        @Email(message = "Formato invalido")
        String correo,
        @NotBlank(message = "Es obligatorio")
        String telefono,
        @NotEmpty(message = "Es obligatorio")
        Set<@Positive(message = "Los valores deben ser positivos") Long> especialidadesIds
) {
}