package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "esquemas_vacunacion")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EsquemaVacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_esquema")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vacuna", nullable = false)
    private Vacuna vacuna;

    @Column(name = "numero_dosis",nullable = false)
    private int numeroDosis;

    @Column(name = "edad_recomendada_meses", nullable = false)
    private int edadRecomendadaMeses;

    @Column(name = "intervalo_minimo_dias", nullable = true)
    private Integer intervaloMinimoDias;

    @Column(name = "es_refuerzo", nullable = false)
    private boolean esRefuerzo;
}