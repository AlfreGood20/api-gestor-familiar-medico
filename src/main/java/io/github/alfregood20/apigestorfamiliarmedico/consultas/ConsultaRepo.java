package io.github.alfregood20.apigestorfamiliarmedico.consultas;

import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsultaRepo extends JpaRepository<Consulta, Long> {
    Optional<Consulta> findByIdAndIntegranteFamilia(Long id, Familia integranteFamilia);
}
