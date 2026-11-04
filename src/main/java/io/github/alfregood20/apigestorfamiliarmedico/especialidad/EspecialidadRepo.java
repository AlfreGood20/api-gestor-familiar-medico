package io.github.alfregood20.apigestorfamiliarmedico.especialidad;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecialidadRepo extends JpaRepository<Especialidad, Long> {

    boolean existsByNombre(String nombre);
}