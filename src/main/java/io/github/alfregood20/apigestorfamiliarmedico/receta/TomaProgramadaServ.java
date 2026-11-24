package io.github.alfregood20.apigestorfamiliarmedico.receta;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.ReglaNegocioException;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import io.github.alfregood20.apigestorfamiliarmedico.receta.dto.TomaProgramadaAgendaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.receta.enums.EstadoTomaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
import io.github.alfregood20.apigestorfamiliarmedico.receta.dto.TomaEstadoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TomaProgramadaServ {

    private final TomaProgramadaRepo tomaProgramadaRepo;
    private final UsuarioRepo usuarioRepo;

    @Transactional(readOnly = true)
    public Page<TomaProgramadaAgendaResponse> obtenerTomas(
            CustomUserDetails usuario,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Long idIntegrante,
            EstadoTomaEnum estado,
            Pageable pageable
    ) {
        LocalDateTime inicioEfectivo = (fechaInicio != null) ? fechaInicio.atStartOfDay() : LocalDateTime.of(1900, 1, 1, 0, 0);

        LocalDateTime finEfectivo = (fechaFin != null) ? fechaFin.atTime(LocalTime.MAX) : LocalDateTime.of(2999, 12, 31, 23, 59, 59);

        if (inicioEfectivo.isAfter(finEfectivo)) {
            throw new ReglaNegocioException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        return tomaProgramadaRepo.buscarPorRangoYFiltros(
                        autenticado.getFamilia(),
                        inicioEfectivo,
                        finEfectivo,
                        idIntegrante,
                        estado,
                        pageable
                )
                .map(this::toAgendaResponse);
    }

    @Transactional
    public TomaProgramadaAgendaResponse actualizarEstado(CustomUserDetails usuario, Long idToma, TomaEstadoRequest request) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        TomaProgramada toma = tomaProgramadaRepo
                .findByIdAndDetalleRecetaRecetaIntegranteFamilia(idToma, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Toma programada con ID: "+idToma+" no encontrada"));

        if (toma.getEstado() == EstadoTomaEnum.CANCELADA && request.estado() != EstadoTomaEnum.CANCELADA) {
            throw new ReglaNegocioException("No se puede modificar el estado de una toma que ya fue CANCELADA");
        }

        if (request.estado() == EstadoTomaEnum.TOMADA) {
            toma.setEstado(EstadoTomaEnum.TOMADA);
            toma.setFechaHoraReal(request.fechaHoraReal() != null ? request.fechaHoraReal() : LocalDateTime.now());
        } else {
            // PENDIENTE, OMITIDA o CANCELADA
            toma.setEstado(request.estado());
            toma.setFechaHoraReal(null);
        }

        return toAgendaResponse(tomaProgramadaRepo.save(toma));
    }

    @Transactional(readOnly = true)
    public List<TomaProgramadaAgendaResponse> obtenerTomasPorReceta(
            CustomUserDetails usuario,
            Long idReceta,
            EstadoTomaEnum estado
    ) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        return tomaProgramadaRepo
                .buscarPorRecetaYFamilia(idReceta, autenticado.getFamilia(), estado)
                .stream()
                .map(this::toAgendaResponse)
                .toList();
    }


    //======= Funciones reutilizables =====

    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID:"+id+" no encontrado"));
    }

    private TomaProgramadaAgendaResponse toAgendaResponse(TomaProgramada toma) {
        DetalleReceta detalle = toma.getDetalleReceta();
        Receta receta = detalle.getReceta();
        Integrante integrante = receta.getIntegrante();

        String nombreCompleto = integrante.getNombre() + " " + integrante.getApellidoPaterno()
                + (integrante.getApellidoMaterno() != null ? " " + integrante.getApellidoMaterno() : "");

        return new TomaProgramadaAgendaResponse(
                toma.getId(),
                receta.getId(),
                integrante.getId(),
                nombreCompleto.trim(),
                detalle.getMedicamento().getNombreComercial(),
                detalle.getMedicamento().getConcentracion(),
                detalle.getDosis(),
                toma.getFechaHoraProgramada(),
                toma.getEstado(),
                toma.getFechaHoraReal()
        );
    }
}
