package io.github.alfregood20.apigestorfamiliarmedico.medico.dto;

import io.github.alfregood20.apigestorfamiliarmedico.especialidad.Especialidad;

import java.util.List;

public record MedicoResponse(
        Long id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String correo,
        String telefono,
        List<Especialidad> especialidades
) {
}