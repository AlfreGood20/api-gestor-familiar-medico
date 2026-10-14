package io.github.alfregood20.apigestorfamiliarmedico.auth.dto;

import io.github.alfregood20.apigestorfamiliarmedico.familia.dto.FamiliaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.SexoEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record RegistroRequest(

        @NotBlank(message = "Es obligatorio")
        String nombre,

        String apellidoPaterno,

        String apellidoMaterno,

        @Past(message = "Debe ser anterior a hoy")
        LocalDate fechaNacimiento,

        @NotNull(message = "Es obligatorio")
        SexoEnum sexo,

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
