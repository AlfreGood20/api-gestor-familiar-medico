package io.github.alfregood20.apigestorfamiliarmedico.estudio;

import io.github.alfregood20.apigestorfamiliarmedico.consultas.Consulta;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.CategoriaEstudioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.EstadoEstudioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "estudios_laboratorio")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstudioLaboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estudio", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, name = "id_integrante")
    private Integrante integrante;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(nullable = true, name = "id_consulta")
    private Consulta consulta;

    @Column(name = "fecha_estudio", nullable = false)
    private LocalDate fechaEstudio;
    @Column(name = "tipo_estudio", nullable = false, length = 150)
    private String tipoEstudio;
    @Column(name = "categoria", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CategoriaEstudioEnum categoria;
    private String laboratorio;
    private String indicaciones;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEstudioEnum estado = EstadoEstudioEnum.PENDIENTE;

    @Column(columnDefinition = "TEXT")
    private String resultado;
    @Column(name = "valores_referencia", columnDefinition = "TEXT")
    private String valoresReferencia;
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    @Column(name = "archivo_url",length = 500)
    private String archivoUrl;

    @Column(precision = 10, scale = 2)
    private BigDecimal costo;
    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
}
