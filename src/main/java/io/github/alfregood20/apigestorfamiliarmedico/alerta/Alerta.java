package io.github.alfregood20.apigestorfamiliarmedico.alerta;

import io.github.alfregood20.apigestorfamiliarmedico.alerta.enums.TipoAlertaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_familia", nullable = false)
    private Familia familia;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_integrante", nullable = true)
    private Integrante integrante;

    @Column(name = "id_referencia", nullable = true)
    private Long referencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAlertaEnum tipo;

    @Column(nullable = false, length = 255)
    private String mensaje;

    @Builder.Default
    private boolean leida = false;

    @CreationTimestamp
    @Column(name = "fecha_generacion", nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion;
}
