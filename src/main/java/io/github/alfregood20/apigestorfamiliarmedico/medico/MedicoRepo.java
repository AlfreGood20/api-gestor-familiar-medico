package io.github.alfregood20.apigestorfamiliarmedico.medico;

import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicoRepo extends JpaRepository<Medico, Long> {

    Optional<Medico> findByIdAndFamilia(Long id,Familia familia);

    List<Medico> findAllByFamilia(Familia familia);
}