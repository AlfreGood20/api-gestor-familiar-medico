package io.github.alfregood20.apigestorfamiliarmedico.stock;

import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.Medicamento;
import io.github.alfregood20.apigestorfamiliarmedico.stock.enums.EstadoStockEnum;
import io.github.alfregood20.apigestorfamiliarmedico.stock.enums.UnidadStockEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_medicamentos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stock")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_familia", nullable = false)
    private Familia familia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_medicamento", nullable = false)
    private Medicamento medicamento;

    @Column(nullable = false)
    private int cantidad;

    @Builder.Default
    @Column(name = "cantidad_minima",nullable = false)
    private int cantidadMinima = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private UnidadStockEnum unidad;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoStockEnum estado = EstadoStockEnum.DISPONIBLE;

    @Column(name = "fecha_caducidad", nullable = false)
    private LocalDate fechaCaducidad;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
}