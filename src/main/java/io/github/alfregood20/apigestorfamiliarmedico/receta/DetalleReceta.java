package io.github.alfregood20.apigestorfamiliarmedico.receta;

import io.github.alfregood20.apigestorfamiliarmedico.medicamento.Medicamento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "detalles_receta")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetalleReceta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_receta", nullable = false)
    private Receta receta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_medicamento", nullable = false)
    private Medicamento medicamento;

    @Column(nullable = false, length = 100)
    private String dosis;

    @Column(name = "frecuencia_horas", nullable = false)
    private int frecuenciaHoras;

    @Column(name = "duracion_dias", nullable = true)
    private Integer duracionDias;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Builder.Default
    @OneToMany(mappedBy = "detalleReceta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TomaProgramada> tomas = new ArrayList<>();

    // Método helper para mantener sincronizada la relación bidireccional con TomaProgramada
    public void agregarToma(TomaProgramada toma) {
        tomas.add(toma);
        toma.setDetalleReceta(this);
    }
}