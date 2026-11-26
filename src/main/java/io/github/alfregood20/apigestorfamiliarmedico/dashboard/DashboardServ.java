package io.github.alfregood20.apigestorfamiliarmedico.dashboard;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.Consulta;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.ConsultaRepo;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.enums.EstadoConsultaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.dashboard.dto.DashboardResponse;
import io.github.alfregood20.apigestorfamiliarmedico.dashboard.dto.Kpis;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.IntegranteRepo;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
import io.github.alfregood20.apigestorfamiliarmedico.receta.DetalleReceta;
import io.github.alfregood20.apigestorfamiliarmedico.receta.Receta;
import io.github.alfregood20.apigestorfamiliarmedico.receta.TomaProgramada;
import io.github.alfregood20.apigestorfamiliarmedico.receta.TomaProgramadaRepo;
import io.github.alfregood20.apigestorfamiliarmedico.receta.dto.TomaProgramadaAgendaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.receta.dto.TomaProgramadaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.receta.enums.EstadoTomaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.stock.StockMedicamentoRepo;
import io.github.alfregood20.apigestorfamiliarmedico.stock.enums.EstadoStockEnum;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServ {

    private final UsuarioRepo usuarioRepo;
    private final IntegranteRepo integranteRepo;
    private final ConsultaRepo consultaRepo;
    private final StockMedicamentoRepo stockMedicamentoRepo;
    private final TomaProgramadaRepo tomaProgramadaRepo;

    public DashboardResponse dashboard(CustomUserDetails usuario){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(30);
        LocalDateTime inicioDia = hoy.atStartOfDay();
        LocalDateTime finDia = hoy.atTime(LocalTime.MAX);

        Kpis kpis = new Kpis(
                integranteRepo.countByFamilia(autenticado.getFamilia()),
                consultaRepo.countByEstadoAndIntegranteFamilia(EstadoConsultaEnum.PROGRAMADA, autenticado.getFamilia()),
                stockMedicamentoRepo.countByFamilia(autenticado.getFamilia()),
                stockMedicamentoRepo.countByFamiliaAndEstadoAndFechaCaducidadBetween(autenticado.getFamilia(), EstadoStockEnum.DISPONIBLE, hoy, limite),
                tomaProgramadaRepo.contarPorFamiliaRangoYEstado(autenticado.getFamilia(), inicioDia, finDia, EstadoTomaEnum.TOMADA),
                tomaProgramadaRepo.contarTotalesHoyPorFamilia(autenticado.getFamilia(), inicioDia, finDia)
        );

        Pageable limiteHoy = PageRequest.of(0, 5, Sort.by("fechaHoraProgramada").ascending());
        List<TomaProgramadaAgendaResponse> tomasDeHoy = tomaProgramadaRepo
                .buscarPorRangoYFiltros(autenticado.getFamilia(), inicioDia, finDia, null, null, limiteHoy)
                .getContent()
                .stream()
                .map(this::toAgendaResponse)
                .toList();


        Pageable limiteProximas = PageRequest.of(0, 5);
        LocalDateTime ahora = LocalDateTime.now();

        List<ConsultaResponse> proximasConsultas = consultaRepo
                .findByIntegranteFamiliaAndEstadoAndFechaConsultaGreaterThanEqualOrderByFechaConsultaAsc(
                        autenticado.getFamilia(),
                        EstadoConsultaEnum.PROGRAMADA,
                        ahora,
                        limiteProximas
                )
                .stream()
                .map(this::toConsultaResponse)
                .toList();

        List<IntegranteResponse> integrantes = integranteRepo
                .findByFamilia(autenticado.getFamilia())
                .stream()
                .map(this::toIntegranteResponse)
                .toList();

        return new DashboardResponse(
                autenticado.getFamilia().getNombre(),
                kpis,
                tomasDeHoy,
                proximasConsultas,
                integrantes
        );
    }

    // FUNCIONES REUTILIZABLES
    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID:"+id+" no encontrado"));
    }

    private IntegranteResponse toIntegranteResponse(Integrante response){
        return new IntegranteResponse(
                response.getId(),
                response.getNombre(),
                response.getApellidoPaterno(),
                response.getApellidoMaterno(),
                response.getFechaNacimiento(),
                response.getPeso(),
                response.getEstatura(),
                response.getSexo(),
                response.getTipoSangre(),
                response.getParentesco(),
                response.getFechaRegistro()
        );
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

    private ConsultaResponse toConsultaResponse(Consulta entity){
        String nombreIntegrante = entity.getIntegrante().getNombre()+" "+
                entity.getIntegrante().getApellidoPaterno()+" "+
                entity.getIntegrante().getApellidoMaterno();

        String nombreMedico = null;

        if(entity.getMedico() != null){
            nombreMedico = entity.getMedico().getNombre()+" "+
                    entity.getMedico().getApellidoPaterno()+" "+
                    entity.getMedico().getApellidoMaterno();
        }

        return new ConsultaResponse(
                entity.getId(),
                nombreIntegrante,
                nombreMedico,
                entity.getMotivo(),
                entity.getFechaConsulta(),
                entity.getEstado()
        );
    }
}