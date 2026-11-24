package io.github.alfregood20.apigestorfamiliarmedico.receta;

import io.github.alfregood20.apigestorfamiliarmedico.receta.enums.EstadoTomaEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TomaProgramadaRepo extends JpaRepository<TomaProgramada, Long> {
    int countByDetalleRecetaIdAndEstado(Long id, EstadoTomaEnum estadoTomaEnum);
}
