package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.VacunaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.VacunaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacunaServ {

    private final VacunaRepo vacunaRepo;

    public VacunaResponse crearNuevo(VacunaRequest request){
        Vacuna vacuna = toEntity(request);
        return toResponse(vacunaRepo.save(vacuna));
    }

    public VacunaResponse obtenerPorId(Long idVacuna){
        return toResponse(vacunaRepo.findById(idVacuna)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vacuna con id:"+idVacuna+" no encontrado"))
        );
    }

    public Page<VacunaResponse> obtenerPorFiltro(String busqueda, boolean activo, int dosisTotales, Pageable pageable){
        Page<Vacuna> pageVacunas = vacunaRepo.obtenerPorFiltro(busqueda, activo, dosisTotales, pageable);
        return pageVacunas.map(this::toResponse);
    }

    public void eliminarPorId(Long idVacuna){
        Vacuna vacuna = vacunaRepo.findById(idVacuna)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vacuna con id:"+idVacuna+" no encontrado"));

        vacunaRepo.delete(vacuna);
    }



    // Funciones reutilizables
    private Vacuna toEntity(VacunaRequest request){
        return Vacuna.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .contraindicaciones(request.contraindicaciones())
                .dosisTotales(request.dosisTotales())
                .enfermedadPreviene(request.enfermedadPreviene())
                .build();
    }

    private VacunaResponse toResponse(Vacuna entity){
        return new VacunaResponse(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getContraindicaciones(),
                entity.getEnfermedadPreviene(),
                entity.getDosisTotales()
        );
    }
}
