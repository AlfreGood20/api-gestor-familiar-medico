package io.github.alfregood20.apigestorfamiliarmedico.receta;

import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import io.github.alfregood20.apigestorfamiliarmedico.receta.enums.EstadoTomaEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TomaProgramadaRepo extends JpaRepository<TomaProgramada, Long> {
    int countByDetalleRecetaIdAndEstado(Long id, EstadoTomaEnum estadoTomaEnum);

    @Query(
            value = """
                    SELECT t FROM TomaProgramada t
                    JOIN FETCH t.detalleReceta d
                    JOIN FETCH d.medicamento m
                    JOIN FETCH d.receta r
                    JOIN FETCH r.integrante i
                    WHERE i.familia = :familia
                      AND t.fechaHoraProgramada BETWEEN :inicio AND :fin
                      AND (:idIntegrante IS NULL OR i.id = :idIntegrante)
                      AND (:estado IS NULL OR t.estado = :estado)
                    """,
            countQuery = """
                    SELECT COUNT(t) FROM TomaProgramada t
                    JOIN t.detalleReceta d
                    JOIN d.receta r
                    JOIN r.integrante i
                    WHERE i.familia = :familia
                      AND t.fechaHoraProgramada BETWEEN :inicio AND :fin
                      AND (:idIntegrante IS NULL OR i.id = :idIntegrante)
                      AND (:estado IS NULL OR t.estado = :estado)
                    """
    )
    Page<TomaProgramada> buscarPorRangoYFiltros(
            @Param("familia") Familia familia,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("idIntegrante") Long idIntegrante,
            @Param("estado") EstadoTomaEnum estado,
            Pageable pageable
    );

    Optional<TomaProgramada> findByIdAndDetalleRecetaRecetaIntegranteFamilia(Long idToma, Familia familia);

    @Query("""
            SELECT t FROM TomaProgramada t
            JOIN FETCH t.detalleReceta d
            JOIN FETCH d.medicamento m
            JOIN FETCH d.receta r
            JOIN FETCH r.integrante i
            WHERE r.id = :idReceta
              AND i.familia = :familia
              AND (CAST(:estado AS string) IS NULL OR t.estado = :estado)
            ORDER BY t.fechaHoraProgramada ASC
            """)
    List<TomaProgramada> buscarPorRecetaYFamilia(
            @Param("idReceta") Long idReceta,
            @Param("familia") Familia familia,
            @Param("estado") EstadoTomaEnum estado
    );
}