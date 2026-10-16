package io.github.alfregood20.apigestorfamiliarmedico.alergia;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.enums.SeveridadAlergiaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "integrante_alergias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegranteAlergia {

    @EmbeddedId
    private IntegranteAlergiaId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_integrante", nullable = false)
    @MapsId("idIntegrante")
    private Integrante integrante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_alergia", nullable = false)
    @MapsId("idAlergia")
    private Alergia alergia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeveridadAlergiaEnum severidad;

    @Column(length = 255)
    private String observaciones;
}
