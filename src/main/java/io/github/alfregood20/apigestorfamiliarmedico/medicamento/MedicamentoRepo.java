package io.github.alfregood20.apigestorfamiliarmedico.medicamento;

import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.FormaFarmaceuticaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.ViaAdministracionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicamentoRepo extends JpaRepository<Medicamento, Long> {

    boolean existsByNombreComercialIgnoreCaseAndConcentracionIgnoreCaseAndFormaFarmaceutica(String nombre, String concentracion, FormaFarmaceuticaEnum formaFarmaceutica);

    @Query(
            """
            SELECT m FROM Medicamento m
                WHERE (:nombreComercial IS NULL OR LOWER(m.nombreComercial) LIKE (CONCAT(:nombreComercial,'%') ) )
                    AND (:sustanciaActiva IS NULL OR LOWER(m.sustanciaActiva) LIKE (CONCAT(:sustanciaActiva,'%') ))
                    AND (:formaFarmaceutica IS NULL OR m.formaFarmaceutica = :formaFarmaceutica)
                    AND (:viaAdministracion IS NULL OR m.viaAdministracion = :viaAdministracion)
                    AND (m.activo = :activo)
            """
    )
    Page<Medicamento> obtenerPorFiltros(
            @Param("nombreComercial") String nombreComercial,
            @Param("sustanciaActiva") String sustanciaActiva,
            @Param("formaFarmaceutica") FormaFarmaceuticaEnum formaFarmaceutica,
            @Param("viaAdministracion") ViaAdministracionEnum viaAdministracion,
            @Param("activo") boolean activo,
            Pageable pageable
    );
}
