package io.github.alfregood20.apigestorfamiliarmedico.stock;

import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.Medicamento;
import io.github.alfregood20.apigestorfamiliarmedico.stock.enums.EstadoStockEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface StockMedicamentoRepo extends JpaRepository<StockMedicamento, Long> {

    Optional<StockMedicamento> findByIdAndFamilia(Long id, Familia familia);

    @Query("""
        SELECT s FROM StockMedicamento s
        JOIN s.medicamento m
        WHERE s.familia = :familia
          AND (
            LOWER(m.nombreComercial) LIKE LOWER(CONCAT(:nombre, '%')) OR
            LOWER(m.sustanciaActiva) LIKE LOWER(CONCAT(:nombre, '%'))
          )
          AND (:bajoMinimo = false OR s.cantidad <= s.cantidadMinima)
          AND (
            (:estado IS NULL AND s.estado <> EstadoStockEnum.DESECHADO) 
            OR s.estado = :estado
          )
        """)
    Page<StockMedicamento> obtenerPorFiltros(
            @Param("familia") Familia familia,
            @Param("nombre") String nombre,
            @Param("bajoMinimo") boolean bajoMinimo,
            @Param("estado") EstadoStockEnum estado,
            Pageable pageable
    );

    boolean existsByMedicamentoAndFamilia(Medicamento medicamento, Familia familia);

    // PARA EL DASHBOARD
    int countByFamilia(Familia familia);
    int countByFamiliaAndEstadoAndFechaCaducidadBetween(Familia familia, EstadoStockEnum estado, LocalDate inicio, LocalDate fin);
}