package io.github.alfregood20.apigestorfamiliarmedico.especialidad;

import io.github.alfregood20.apigestorfamiliarmedico.especialidad.dto.EspecilidadRequest;
import io.github.alfregood20.apigestorfamiliarmedico.exception.DuplicateException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecilidadServ {

    private final EspecialidadRepo especialidadRepo;

    @Transactional
    public Especialidad crearNuevo(EspecilidadRequest request){

        if(especialidadRepo.existsByNombre(request.nombre())){
            throw new DuplicateException("Especialidad con Nombre:"+request.nombre()+" ya se encuentra registrado");
        }

        Especialidad especialidad = Especialidad.builder()
                .nombre(request.nombre())
                .build();

        especialidad = especialidadRepo.save(especialidad);

        return especialidad;
    }

    @Transactional(readOnly = true)
    public List<Especialidad> obtenerListado(){
        return especialidadRepo.findAll();
    }

    @Transactional
    public void borrarPorId(Long id){
        Especialidad especialidad = especialidadRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Especialidad con ID:"+id+" no encontrado"));

        especialidadRepo.delete(especialidad);
    }
}
