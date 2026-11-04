package io.github.alfregood20.apigestorfamiliarmedico.especialidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Entity
@Table(name = "especialidades")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidad")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}