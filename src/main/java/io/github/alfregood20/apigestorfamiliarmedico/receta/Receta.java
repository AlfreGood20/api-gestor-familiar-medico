package io.github.alfregood20.apigestorfamiliarmedico.receta;

import io.github.alfregood20.apigestorfamiliarmedico.consultas.Consulta;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recetas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receta")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_integrante", nullable = false)
    private Integrante integrante;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_consulta", nullable = true)
    private Consulta consulta;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String indicaciones;

    @Builder.Default
    @Column(nullable = false)
    private boolean activa = true;

    @Builder.Default
    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleReceta> detalles = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    // Método helper para mantener sincronizada la relación bidireccional en memoria
    public void agregarDetalle(DetalleReceta detalle) {
        detalles.add(detalle);
        detalle.setReceta(this);
    }
}