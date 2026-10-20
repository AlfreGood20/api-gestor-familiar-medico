package io.github.alfregood20.apigestorfamiliarmedico.medicamento;

import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.FormaFarmaceuticaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.ViaAdministracionEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicamentos")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicamento")
    private Long id;

    @Column(name = "nombre_comercial", nullable = false, length = 255)
    private String nombreComercial;

    @Column(name = "sustancia_activa", nullable = false)
    private String sustanciaActiva;

    @Column(nullable = false)
    private String concentracion;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_farmaceutica",nullable = false)
    private FormaFarmaceuticaEnum formaFarmaceutica;

    @Enumerated(EnumType.STRING)
    @Column(name = "via_administracion",nullable = false)
    private ViaAdministracionEnum viaAdministracion;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;
}