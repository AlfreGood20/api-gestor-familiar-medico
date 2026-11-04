package io.github.alfregood20.apigestorfamiliarmedico.especialidad;

import io.github.alfregood20.apigestorfamiliarmedico.medico.Medico;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medico_especialidades")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicoEspecialidad {

    @EmbeddedId
    private MedicoEspecialidadId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_medico")
    @MapsId("idMedico")
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_especialidad")
    @MapsId("idEspecialidad")
    private Especialidad especialidad;
}
