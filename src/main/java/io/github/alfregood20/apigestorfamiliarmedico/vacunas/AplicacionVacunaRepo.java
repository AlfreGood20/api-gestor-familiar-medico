package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AplicacionVacunaRepo extends JpaRepository<AplicacionVacuna, Long> {

    List<AplicacionVacuna> findByIntegranteAndVacunaIn(Integrante integrante, List<Vacuna> vacunas);
    boolean existsByIntegranteAndVacunaAndNumeroDosis(Integrante integrante, Vacuna vacuna, int numeroDosis);
    Optional<AplicacionVacuna> findByIntegranteAndVacunaAndNumeroDosis(Integrante integrante, Vacuna vacuna, int numeroDosis);
}
