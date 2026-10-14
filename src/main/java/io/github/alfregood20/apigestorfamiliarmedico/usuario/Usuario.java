package io.github.alfregood20.apigestorfamiliarmedico.usuario;

import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.enums.EstadoUsuarioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.enums.RolEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Usuario {

    @Id
    @Column(name = "id_usuario")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_familia", nullable = false)
    private Familia familia;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(name = "contrasena_hash",nullable = false)
    private String contrasenaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolEnum rol;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuarioEnum estado = EstadoUsuarioEnum.ACTIVO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "fecha_registro")
    private LocalDateTime fechaRegistro;
}
