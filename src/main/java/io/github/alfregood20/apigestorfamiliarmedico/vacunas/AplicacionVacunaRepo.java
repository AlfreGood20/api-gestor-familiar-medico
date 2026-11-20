package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AplicacionVacunaRepo extends JpaRepository<AplicacionVacuna, Long> {
    List<AplicacionVacuna> findByIntegranteAndVacunaIn(Integrante integrante, List<Vacuna> vacunas);
}
