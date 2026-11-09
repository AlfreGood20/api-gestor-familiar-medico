package io.github.alfregood20.apigestorfamiliarmedico.alergia;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.dto.AlergiaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.alergia.enums.TipoAlergiaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.exception.DuplicateException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlergiaServc {

    private final AlergiaRepo alergiaRepo;
    private final IntegranteAlergiaRepo integranteAlergiaRepo;

    @Transactional
    public Alergia crearNuevo(AlergiaRequest request){
        if(alergiaRepo.existsByNombre(request.nombre())){
            throw new DuplicateException("Alergia "+request.nombre()+" ,ya se encuntra registado");
        }

        return alergiaRepo.save(Alergia.builder()
                .nombre(request.nombre())
                .tipo(request.tipo())
                .build());
    }

    @Transactional
    public void eliminarPorId(Long id){
        Alergia alergia = alergiaRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alergia con id:"+id+" no encontrado"));

        alergiaRepo.delete(alergia);
    }

    @Transactional(readOnly = true)
    public List<Alergia> obtenerPorFiltro(String nombre, TipoAlergiaEnum tipo){
        return alergiaRepo.buscarPorFiltro(nombre, tipo);
    }

}
