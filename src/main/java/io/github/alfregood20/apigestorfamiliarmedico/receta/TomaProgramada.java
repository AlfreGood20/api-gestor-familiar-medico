package io.github.alfregood20.apigestorfamiliarmedico.receta;

import io.github.alfregood20.apigestorfamiliarmedico.receta.enums.EstadoTomaEnum;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tomas_programadas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TomaProgramada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_toma")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_detalle", nullable = false)
    private DetalleReceta detalleReceta;

    @Column(name = "fecha_hora_programada", nullable = false)
    private LocalDateTime fechaHoraProgramada;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoTomaEnum estado = EstadoTomaEnum.PENDIENTE;

    @Column(name = "fecha_hora_real", nullable = true)
    private LocalDateTime fechaHoraReal;
}