package io.github.alfregood20.apigestorfamiliarmedico.alerta;

import io.github.alfregood20.apigestorfamiliarmedico.alerta.enums.TipoAlertaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AlertaRepo extends JpaRepository<Alerta, Long> {


    @EntityGraph(attributePaths = {"integrante"})
    @Query(
            """
            SELECT a FROM Alerta a
            LEFT JOIN a.integrante i
            WHERE a.familia = :familia
              AND (CAST(:tipo AS string) IS NULL OR a.tipo = :tipo)
              AND (CAST(:idIntegrante AS long) IS NULL OR i.id = :idIntegrante)
              AND (:soloNoLeidas = false OR a.leida = false)
            """
    )
    Page<Alerta> buscarPorFiltro(
            @Param("familia") Familia familia,
            @Param("idIntegrante") Long idIntegrante,
            @Param("tipo") TipoAlertaEnum tipo,
            @Param("soloNoLeidas") boolean soloNoLeidas,
            Pageable pageable
    );

    long countByFamiliaAndLeidaFalse(Familia familia);

    Optional<Alerta> findByIdAndFamilia(Long id, Familia familia);

    //SE USARA EN LOS SCHEDULER
    boolean existsByFamiliaAndTipoAndReferenciaAndLeidaFalse(Familia familia, TipoAlertaEnum tipo, Long referencia);

    @Modifying
    @Query("UPDATE Alerta a SET a.leida = true WHERE a.familia = :familia AND a.leida = false")
    int marcarTodasComoLeidas(@Param("familia") Familia familia);
}