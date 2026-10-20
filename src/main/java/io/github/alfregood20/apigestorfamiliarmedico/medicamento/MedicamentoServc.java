package io.github.alfregood20.apigestorfamiliarmedico.medicamento;

import io.github.alfregood20.apigestorfamiliarmedico.exception.DuplicateException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.dto.MedicamentoRequest;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.dto.MedicamentoResponse;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.FormaFarmaceuticaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.enums.ViaAdministracionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicamentoServc {

    private final MedicamentoRepo medicamentoRepo;

    @Transactional
    public MedicamentoResponse crear(MedicamentoRequest request){

        if(medicamentoRepo.existsByNombreComercialIgnoreCaseAndConcentracionIgnoreCaseAndFormaFarmaceutica(
                request.nombreComercial(),request.concentracion(),request.formaFarmaceutica()
        )){
            throw new DuplicateException(
                    "Medicamento nombre:"+request.nombreComercial()+" con concentracion:"+request.concentracion()+" ya se encuentra registrada"
            );
        }

        Medicamento medicamento = Medicamento.builder()
                .nombreComercial(request.nombreComercial())
                .sustanciaActiva(request.sustanciaActiva())
                .concentracion(request.concentracion())
                .formaFarmaceutica(request.formaFarmaceutica())
                .viaAdministracion(request.viaAdministracion())
                .build();

        return toResponse(medicamentoRepo.save(medicamento));
    }

    public MedicamentoResponse obtenerPorId(Long id){
        Medicamento medicamento = medicamentoRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medicamento con ID:"+id+" no encontrado"));

        return toResponse(medicamento);
    }

    @Transactional(readOnly = true)
    public Page<MedicamentoResponse> obtenerPorFiltros(
            String nombreComercial, String sustanciaActiva, FormaFarmaceuticaEnum formaFarmaceutica,
            ViaAdministracionEnum viaAdministracion, boolean activo, Pageable pageable
    ){
        Page<Medicamento> pageMedicamento = medicamentoRepo.obtenerPorFiltros(
                nombreComercial, sustanciaActiva, formaFarmaceutica, viaAdministracion, activo, pageable
        );

        Page<MedicamentoResponse> pageResponse = pageMedicamento.map(this::toResponse);
        return pageResponse;
    }

    // FUNCIONES REUTILIZABLE
    private MedicamentoResponse toResponse (Medicamento response){
        return new MedicamentoResponse(
          response.getId(),
          response.getNombreComercial(),
          response.getSustanciaActiva(),
          response.getConcentracion(),
          response.getFormaFarmaceutica(),
          response.getViaAdministracion()
        );
    }
}
