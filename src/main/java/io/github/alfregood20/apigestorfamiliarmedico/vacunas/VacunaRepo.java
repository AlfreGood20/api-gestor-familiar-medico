package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VacunaRepo extends JpaRepository<Vacuna, Long> {

    @Query("""
        SELECT v FROM Vacuna v
        WHERE (
            LOWER(v.nombre) LIKE CONCAT('%',LOWER(:busqueda),'%') OR
            LOWER(v.descripcion) LIKE CONCAT('%',LOWER(:busqueda),'%') OR
            LOWER(v.contraindicaciones) LIKE CONCAT('%',LOWER(:busqueda),'%') OR
            LOWER(v.enfermedadPreviene) LIKE CONCAT('%', LOWER(:busqueda), '%')
        )
        AND (:dosisTotales <= 0 OR v.dosisTotales = :dosisTotales)
        AND (v.activo = :activo)
    """
    )
    Page<Vacuna> obtenerPorFiltro(
            @Param("busqueda") String busqueda,
            @Param(("activo")) boolean activo,
            @Param("dosisTotales") int dosisTotales,
            Pageable pageable
    );

    @Query("""
        SELECT v FROM Vacuna v
        JOIN EsquemaVacunacion e ON e.vacuna.id = v.id
        WHERE LOWER(v.nombre) LIKE CONCAT(LOWER(:nombre),'%')
        GROUP BY v
        ORDER BY MIN(e.edadRecomendadaMeses) ASC, v.nombre ASC
        """
    )
    Page<Vacuna> buscarVacunasConEsquema(
            @Param("nombre") String nombre,
            Pageable pageable
    );
}