package io.github.alfregood20.apigestorfamiliarmedico.integrante;

import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IntegranteRepo extends JpaRepository<Integrante, Long> {

    List<Integrante> findByFamilia (Familia familia);
    Optional<Integrante> findByIdIntegrateAndFamilia(Long idIntegrate, Familia familia);
}