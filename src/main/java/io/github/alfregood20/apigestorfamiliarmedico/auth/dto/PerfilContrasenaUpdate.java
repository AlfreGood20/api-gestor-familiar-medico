package io.github.alfregood20.apigestorfamiliarmedico.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record PerfilContrasenaUpdate(

        @NotBlank(message = "La contraseña actual es obligatoria")
        @Schema(description = "Contraseña actual del usuario autenticado", example = "MiClaveActual123")
        String contrasenaActual,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La nueva contraseña debe tener entre 8 y 72 caracteres")
        @Schema(description = "Nueva contraseña deseada", example = "NuevaClaveSegura2026")
        String nuevaContrasena
) {
}
