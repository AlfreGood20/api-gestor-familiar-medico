package io.github.alfregood20.apigestorfamiliarmedico.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(

        @Email(message = "Formato incorrecto")
        @NotBlank(message = "Es obligatorio")
        String correo,

        @NotEmpty(message = "Es obligatorio")
        String contrasena
) {
}