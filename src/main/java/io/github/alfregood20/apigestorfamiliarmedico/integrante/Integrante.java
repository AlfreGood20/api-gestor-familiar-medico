package io.github.alfregood20.apigestorfamiliarmedico.integrante;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.IntegranteAlergia;
import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.ParentescoEnum;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.SexoEnum;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.TipoSangreEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "integrantes")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Integrante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_integrante")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_familia", nullable = false)
    private Familia familia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = true, unique = true)
    private Usuario usuario;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "apellido_paterno")
    private String apellidoPaterno;

    @Column(name = "apellido_materno")
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "peso_kg", precision = 5, scale = 2, nullable = true)
    private BigDecimal peso;

    @Column(name = "estatura_cm", precision = 5, scale = 2, nullable = true)
    private BigDecimal estatura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private SexoEnum sexo;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, name = "tipo_sangre", length = 20)
    private TipoSangreEnum tipoSangre = TipoSangreEnum.SIN_DEFINIR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParentescoEnum parentesco;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false, nullable = false)
    private LocalDateTime fechaRegistro;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "integrante", orphanRemoval = true)
    public List<IntegranteAlergia> integranteAlergia;
}
