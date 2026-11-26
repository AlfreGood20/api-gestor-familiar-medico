package io.github.alfregood20.apigestorfamiliarmedico.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record PerfilUpdate(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
        @Schema(description = "Nombre del usuario", example = "José Alfredo")
        String nombre,

        @NotBlank(message = "El apellido paterno es obligatorio")
        @Size(max = 100, message = "El apellido paterno no puede exceder los 100 caracteres")
        @Schema(description = "Apellido paterno del usuario", example = "Lopez")
        String apellidoPaterno,

        @Size(max = 100, message = "El apellido materno no puede exceder los 100 caracteres")
        @Schema(description = "Apellido materno del usuario (opcional)", example = "De La Cruz")
        String apellidoMaterno,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El formato del correo electrónico no es válido")
        @Size(max = 250, message = "El correo no puede exceder los 250 caracteres")
        @Schema(description = "Correo electrónico de acceso", example = "alfredo@gmail.com")
        String correo,

        @Pattern(regexp = "^$|^[0-9]{10}$", message = "El teléfono debe tener exactamente 10 dígitos")
        @Schema(description = "Teléfono a 10 dígitos (opcional)", example = "9931234567")
        String telefono
) {
}