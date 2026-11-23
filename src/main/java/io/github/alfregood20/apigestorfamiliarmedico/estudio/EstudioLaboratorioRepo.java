package io.github.alfregood20.apigestorfamiliarmedico.estudio;

import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.CategoriaEstudioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.EstadoEstudioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EstudioLaboratorioRepo extends JpaRepository<EstudioLaboratorio, Long> {
    Optional<EstudioLaboratorio> findByIdAndIntegranteFamilia(Long idEstudio, Familia familia);

    @EntityGraph(attributePaths = {"integrante", "consulta", "consulta.medico"})
    @Query("""
        SELECT e FROM EstudioLaboratorio e
        LEFT JOIN e.consulta c
        WHERE e.integrante.familia = :familia
          AND (CAST(:idIntegrante AS long) IS NULL OR e.integrante.id = :idIntegrante)
          AND (CAST(:categoria AS string) IS NULL OR e.categoria = :categoria)
          AND (CAST(:estado AS string) IS NULL OR e.estado = :estado)
          AND (:soloPendientes = false OR e.estado = 'PENDIENTE')
          AND (
              LOWER(e.tipoEstudio) LIKE CONCAT('%', LOWER(:busqueda), '%') OR
              LOWER(COALESCE(e.laboratorio, '')) LIKE CONCAT('%', LOWER(:busqueda), '%')
          )
        """)
    Page<EstudioLaboratorio> obtenerPorFiltros(
            @Param("familia") Familia familia,
            @Param("idIntegrante") Long idIntegrante,
            @Param("categoria") CategoriaEstudioEnum categoria,
            @Param("estado") EstadoEstudioEnum estado,
            @Param("soloPendientes") boolean soloPendientes,
            @Param("busqueda") String busqueda,
            Pageable pageable
    );
}