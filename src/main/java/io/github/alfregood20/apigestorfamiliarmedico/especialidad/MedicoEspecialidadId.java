package io.github.alfregood20.apigestorfamiliarmedico.especialidad;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MedicoEspecialidadId implements Serializable {

    @Column(name = "id_medico")
    private Long idMedico;

    @Column(name = "id_especialidad")
    private Long idEspecialidad;
}
