package io.github.alfregood20.apigestorfamiliarmedico.integrante.dto;

import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.ParentescoEnum;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.SexoEnum;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.TipoSangreEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record IntegranteResponse(

        Long id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        LocalDate fechaNacimiento,
        BigDecimal peso,
        BigDecimal estatura,
        SexoEnum sexo,
        TipoSangreEnum tipoSangre,
        ParentescoEnum parentesco,
        LocalDateTime fechaRegistro
) {
}
