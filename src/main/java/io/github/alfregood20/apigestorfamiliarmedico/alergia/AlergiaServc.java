package io.github.alfregood20.apigestorfamiliarmedico.alergia;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.enums.TipoAlergiaEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlergiaServc {

    private final AlergiaRepo alergiaRepo;
    private final IntegranteAlergiaRepo integranteAlergiaRepo;

    public List<Alergia> obtenerPorFiltro(String nombre, TipoAlergiaEnum tipo){
        return alergiaRepo.buscarPorFiltro(nombre, tipo);
    }

}
