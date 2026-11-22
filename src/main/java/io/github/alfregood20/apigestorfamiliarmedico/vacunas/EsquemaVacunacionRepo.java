package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EsquemaVacunacionRepo extends JpaRepository<EsquemaVacunacion, Long> {

    List<EsquemaVacunacion> findByVacunaInOrderByNumeroDosisAsc(List<Vacuna> vacunas);
    List<EsquemaVacunacion> findByVacunaOrderByNumeroDosisAsc(Vacuna vacuna);
    Optional<EsquemaVacunacion> findByVacunaAndNumeroDosis(Vacuna vacuna, int numeroDosis);
}