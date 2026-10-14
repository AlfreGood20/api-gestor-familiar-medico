package io.github.alfregood20.apigestorfamiliarmedico.auth.dto;

import io.github.alfregood20.apigestorfamiliarmedico.familia.dto.FamiliaRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record RegistroRequest(

        @NotBlank(message = "Es obligatorio")
        String nombreCompleto,

        @Email(message = "Formato invalido")
        @NotBlank(message = "Es obligatorio")
        String correo,

        String telefono,

        @NotEmpty(message = "Es obligatorio")
        @Size(min = 8, max = 72, message = "Debe tener 8 a 72 caracteres")
        String contrasena,

        @Valid
        @NotNull(message = "Estos datos son obligatorios")
        FamiliaRequest familia
) {
}
