package io.github.alfregood20.apigestorfamiliarmedico.alergia;

import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntegranteAlergiaRepo extends JpaRepository<IntegranteAlergia, IntegranteAlergiaId> {
    List<IntegranteAlergia> findByIntegrante(Integrante integrante);
}
