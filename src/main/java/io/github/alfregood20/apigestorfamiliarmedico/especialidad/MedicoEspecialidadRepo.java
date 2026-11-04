package io.github.alfregood20.apigestorfamiliarmedico.especialidad;

import io.github.alfregood20.apigestorfamiliarmedico.medico.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MedicoEspecialidadRepo extends JpaRepository<MedicoEspecialidad, MedicoEspecialidadId> {

    @Query("SELECT me.especialidad FROM MedicoEspecialidad me WHERE me.medico = :medico")
    List<Especialidad> findByMedico(Medico medico);
}
