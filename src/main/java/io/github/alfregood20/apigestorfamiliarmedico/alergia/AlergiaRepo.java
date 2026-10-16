package io.github.alfregood20.apigestorfamiliarmedico.alergia;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.enums.TipoAlergiaEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlergiaRepo extends JpaRepository<Alergia, Long> {

    @Query("""
           SELECT a FROM Alergia a
           WHERE (:nombre IS NULL OR LOWER(a.nombre) LIKE (CONCAT(:nombre,'%')))
           AND(:tipo IS NULL OR a.tipo = :tipo)
           ORDER BY a.nombre ASC
           """
    )
    List<Alergia> buscarPorFiltro(@Param("nombre") String nombre,@Param("tipo") TipoAlergiaEnum tipo);
}