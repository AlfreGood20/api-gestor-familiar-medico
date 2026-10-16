package io.github.alfregood20.apigestorfamiliarmedico.alergia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class IntegranteAlergiaId implements Serializable {

    @Column(name = "id_integrante")
    private Long idIntegrante;

    @Column(name = "id_alergia")
    private Long idAlergia;
}