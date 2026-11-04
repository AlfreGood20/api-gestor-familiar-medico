package io.github.alfregood20.apigestorfamiliarmedico.medico;

import io.github.alfregood20.apigestorfamiliarmedico.especialidad.MedicoEspecialidad;
import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "medicos")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medico")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_familia", nullable = false)
    private Familia familia;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false)
    private String apellidoMaterno;

    @Column(nullable = true)
    private String correo;

    @Column(nullable = false, length = 20)
    private String telefono;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDate fechaRegistro;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "medico", orphanRemoval = true)
    private List<MedicoEspecialidad> medicoEspecialidad;
}