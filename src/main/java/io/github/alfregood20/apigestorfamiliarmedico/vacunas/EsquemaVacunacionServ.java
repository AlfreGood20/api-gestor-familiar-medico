package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EsquemaVacunacionServ {

    private final EsquemaVacunacionRepo esquemaVacunacionRepo;
    private final VacunaRepo vacunaRepo;

    @Transactional
    public EsquemaVacunacionResponse crearNuevo(EsquemaVacunacionRequest request){
        Vacuna vacuna = vacunaRepo.findById(request.idVacuna())
                .orElseThrow(() -> new RecursoNoEncontradoException("Vacuna con id:"+request.idVacuna()+" no encontrado"));

        EsquemaVacunacion esquemaVacunacion = toEntity(request);
        esquemaVacunacion.setVacuna(vacuna);
        return toResponse(esquemaVacunacionRepo.save(esquemaVacunacion));
    }

    @Transactional(readOnly = true)
    public Page<CartillaVacunacionOficialResponse> obtenerCartillaOficial(String nombreVacuna, Pageable pageable) {

        Page<Vacuna> pageVacunas = vacunaRepo.buscarVacunasConEsquema(nombreVacuna, pageable);

        if (pageVacunas.isEmpty()) {
            return Page.empty(pageable);
        }

        List<EsquemaVacunacion> todasLasDosis = esquemaVacunacionRepo
                .findByVacunaInOrderByNumeroDosisAsc(pageVacunas.getContent());

        return pageVacunas.map(v -> {
            List<DosisResponse> dosisDeEstaVacuna = todasLasDosis.stream()
                    .filter(e -> e.getVacuna().getId().equals(v.getId()))
                    .map(e -> new DosisResponse(e.getId(), e.getNumeroDosis(),
                            e.getEdadRecomendadaMeses(),
                            e.getIntervaloMinimoDias(),
                            e.isEsRefuerzo()
                    ))
                    .toList();

            return new CartillaVacunacionOficialResponse(toVacunaResponse(v), dosisDeEstaVacuna);
        });
    }

    @Transactional(readOnly = true)
    public List<DosisResponse> obtenerDosisPorIdVacuna(Long idVacuna){
        Vacuna vacuna = vacunaRepo.findById(idVacuna)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vacuna con id:"+idVacuna+" no encontrado"));

        List<EsquemaVacunacion> dosis = esquemaVacunacionRepo.findByVacunaOrderByNumeroDosisAsc(vacuna);

        return dosis
                .stream()
                .map(e ->
                        new DosisResponse(e.getId(), e.getNumeroDosis(), e.getEdadRecomendadaMeses(), e.getIntervaloMinimoDias(), e.isEsRefuerzo())
                )
                .toList();
    }

    // Funciones reutilizables
    private EsquemaVacunacion toEntity(EsquemaVacunacionRequest request){
        return EsquemaVacunacion.builder()
                .numeroDosis(request.numeroDosis())
                .edadRecomendadaMeses(request.edadRecomendadaMeses())
                .intervaloMinimoDias(request.intervaloMinimoDias())
                .esRefuerzo(request.esRefuerzo())
                .build();
    }

    private EsquemaVacunacionResponse toResponse(EsquemaVacunacion entity){
        return new EsquemaVacunacionResponse(
                entity.getId(),
                toVacunaResponse(entity.getVacuna()),
                entity.getNumeroDosis(),
                entity.getEdadRecomendadaMeses(),
                entity.getIntervaloMinimoDias(),
                entity.isEsRefuerzo()
        );
    }

    private VacunaResponse toVacunaResponse(Vacuna entity){
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
