package io.github.alfregood20.apigestorfamiliarmedico.consultas;

import io.github.alfregood20.apigestorfamiliarmedico.consultas.enums.EstadoConsultaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ConsultaRepo extends JpaRepository<Consulta, Long> {

    Optional<Consulta> findByIdAndIntegranteFamilia(Long id, Familia integranteFamilia);


    @EntityGraph(attributePaths = {"integrante", "medico"})
    Page<Consulta> findByIntegranteFamiliaAndIntegrante(Familia integranteFamilia, Integrante integrante, Pageable pageable);

    @EntityGraph(attributePaths = {"integrante", "medico"})
    @Query(""" 
        SELECT c FROM Consulta c
        LEFT JOIN c.medico m
        WHERE c.integrante.familia = :familia 
            AND (:estado IS NULL OR c.estado = :estado)
            AND (:idMedico IS NULL OR m.id = :idMedico)
            AND (:idIntegrante IS NULL OR c.integrante.id = :idIntegrante)
            AND c.fechaConsulta >= :fechaDesde
            AND c.fechaConsulta <= :fechaHasta
            AND (
                LOWER(c.motivo) LIKE CONCAT('%',LOWER(:busqueda), '%') OR
                LOWER(c.diagnostico) LIKE CONCAT('%',LOWER(:busqueda),'%')     
            )
        """)
    Page<Consulta> obtenerPorFiltros(
            @Param("familia") Familia familia ,
            @Param("idIntegrante") Long idIntegrante,
            @Param("idMedico") Long idMedico,
            @Param("estado") EstadoConsultaEnum estado,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta,
            @Param("busqueda") String busqueda,
            Pageable pageable
    );
}
