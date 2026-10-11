package io.github.alfregood20.apigestorfamiliarmedico.familia;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "familias")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Familia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_familia")
    private Long idFamilia;

    @Column(nullable = false, length = 100)
    private String nombre;

    private String direccion;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
}
