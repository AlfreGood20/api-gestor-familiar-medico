package io.github.alfregood20.apigestorfamiliarmedico.receta;

import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecetaRepo extends JpaRepository<Receta, Long> {
    Optional<Receta> findByIdAndIntegranteFamilia(Long idReceta, Familia familia);

    @EntityGraph(attributePaths = {"integrante", "consulta", "consulta.medico", "detalles", "detalles.medicamento"})
    @Query("""
        SELECT DISTINCT r FROM Receta r
        LEFT JOIN r.consulta c
        LEFT JOIN r.detalles d
        LEFT JOIN d.medicamento m
        WHERE r.integrante.familia = :familia
          AND (CAST(:idIntegrante AS long) IS NULL OR r.integrante.id = :idIntegrante)
          AND (CAST(:idConsulta AS long) IS NULL OR c.id = :idConsulta)
          AND (CAST(:activa AS boolean) IS NULL OR r.activa = :activa)
          AND (
              LOWER(COALESCE(r.indicaciones, '')) LIKE CONCAT('%', LOWER(:busqueda), '%') OR
              LOWER(COALESCE(m.nombreComercial, '')) LIKE CONCAT('%', LOWER(:busqueda), '%') OR
              LOWER(COALESCE(m.sustanciaActiva, '')) LIKE CONCAT('%', LOWER(:busqueda), '%')
          )
        """)
    Page<Receta> obtenerPorFiltros(
            @Param("familia") Familia familia,
            @Param("idIntegrante") Long idIntegrante,
            @Param("idConsulta") Long idConsulta,
            @Param("activa") Boolean activa,
            @Param("busqueda") String busqueda,
            Pageable pageable
    );

    boolean existsByConsultaId(Long idConsulta);
}
