package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vacunas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vacuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vacuna")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String contraindicaciones;

    @Column(name = "enfermedad_previene", nullable = false)
    private String enfermedadPreviene;

    @Builder.Default
    @Column(name = "dosis_totales", nullable = false)
    private int dosisTotales = 1;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;
}
