package io.github.alfregood20.apigestorfamiliarmedico.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CuidadorRequest(

        @NotBlank(message = "Es obligatorio")
        @Size(max = 200, message = "Máximo 200 caracteres")
        String nombreCompleto,

        @NotBlank(message = "Es obligatorio")
        @Email(message = "Formato inválido")
        String correo,

        @Size(max = 20, message = "Máximo 20 caracteres")
        String telefono,

        @NotBlank(message = "Es obligatorio")
        @Size(min = 8, max = 72, message = "Debe tener de 8 a 72 caracteres")
        String contrasena
) {
}
