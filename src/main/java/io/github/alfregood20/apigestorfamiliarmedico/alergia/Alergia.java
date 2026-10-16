package io.github.alfregood20.apigestorfamiliarmedico.alergia;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.enums.TipoAlergiaEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alergias")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Alergia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alergia")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAlergiaEnum tipo = TipoAlergiaEnum.SIN_DEFINIR;
}