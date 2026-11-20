package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.DosisResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EsquemaVacunacionRepo extends JpaRepository<EsquemaVacunacion, Long> {

    List<EsquemaVacunacion> findByVacunaInOrderByNumeroDosisAsc(List<Vacuna> vacunas);
    List<EsquemaVacunacion> findByVacunaOrderByNumeroDosisAsc(Vacuna vacuna);
}