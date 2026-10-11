package io.github.alfregood20.apigestorfamiliarmedico.usuario;

import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Usuario {

    @Id
    @Column(name = "id_usuario")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuarioEnum estado = EstadoUsuarioEnum.ACTIVO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "fecha_registro")
    private LocalDateTime fechaRegistro;
}
