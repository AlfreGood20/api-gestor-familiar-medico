package io.github.alfregood20.apigestorfamiliarmedico.consultas;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaConcluirRequest;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaDetalleResponse;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.enums.EstadoConsultaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.especialidad.Especialidad;
import io.github.alfregood20.apigestorfamiliarmedico.especialidad.MedicoEspecialidadRepo;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.ReglaNegocioException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.UsuarioNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.IntegranteRepo;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
import io.github.alfregood20.apigestorfamiliarmedico.medico.Medico;
import io.github.alfregood20.apigestorfamiliarmedico.medico.MedicoRepo;
import io.github.alfregood20.apigestorfamiliarmedico.medico.dto.MedicoResponse;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
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
public class ConsultaServ {

    private final ConsultaRepo consultaRepo;
    private final UsuarioRepo usuarioRepo;
    private final IntegranteRepo integranteRepo;
    private final MedicoRepo medicoRepo;
    private final MedicoEspecialidadRepo medicoEspecialidadRepo;

    @Transactional
    public ConsultaResponse crearNuevo(CustomUserDetails usuario, ConsultaRequest request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Integrante integrante = integranteRepo.findByIdAndFamilia(request.idIntegrante(), autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Integrante con id:"+request.idIntegrante()+" no encontrado"));

        Consulta consulta = Consulta.builder()
                .integrante(integrante)
                .fechaConsulta(request.fechaConsulta())
                .motivo(request.motivo())
                .build();

        if (request.idMedico() != null){
             Medico medico= medicoRepo.findByIdAndFamilia(request.idMedico(), autenticado.getFamilia())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Medico con id:"+request.idMedico()+" no encontrado"));

             consulta.setMedico(medico);
        }

        consulta = consultaRepo.save(consulta);
        return toResponse(consulta);
    }

    @Transactional(readOnly = true)
    public ConsultaDetalleResponse obtenerDetallePorId(CustomUserDetails usuario ,Long idConsulta){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Consulta consulta = buscarConsultaPorId(idConsulta, autenticado);

        return toDetalleResponse(consulta);
    }

    @Transactional
    public ConsultaDetalleResponse concluirConsulta(CustomUserDetails usuario, Long idConsulta, ConsultaConcluirRequest request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Consulta consulta = buscarConsultaPorId(idConsulta, autenticado);

        if(consulta.getEstado() == EstadoConsultaEnum.CANCELADA || consulta.getEstado() == EstadoConsultaEnum.REALIZADA){
            throw new ReglaNegocioException("Consulta ya se encuentra "+consulta.getEstado()+" no puedes concluirla");
        }

        consulta.setEstado(EstadoConsultaEnum.REALIZADA);
        consulta.setDiagnostico(request.diagnostico());
        consulta.setNotas(request.notas());
        consulta.setProximaConsulta(request.proximaConsulta());
        consulta.setCosto(request.costo());

        consulta = consultaRepo.save(consulta);
        return toDetalleResponse(consulta);
    }

    @Transactional
    public ConsultaDetalleResponse cancelarConsulta(CustomUserDetails usuario, Long idConsulta){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Consulta consulta = buscarConsultaPorId(idConsulta, autenticado);

        if(consulta.getEstado() == EstadoConsultaEnum.CANCELADA || consulta.getEstado() == EstadoConsultaEnum.REALIZADA){
            throw new ReglaNegocioException("Consulta ya se encuentra "+consulta.getEstado()+" no puedes concluirla");
        }

        consulta.setEstado(EstadoConsultaEnum.CANCELADA);
        consulta = consultaRepo.save(consulta);
        return toDetalleResponse(consulta);
    }

    @Transactional
    public ConsultaResponse actualizarCita(CustomUserDetails usuario, Long idConsulta, ConsultaRequest request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Consulta consulta = buscarConsultaPorId(idConsulta, autenticado);

        Integrante integrante = integranteRepo.findByIdAndFamilia(request.idIntegrante(), autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Integrante con id:"+request.idIntegrante()+" no encontrado"));

        if (request.idMedico() != null){
            Medico medico= medicoRepo.findByIdAndFamilia(request.idMedico(), autenticado.getFamilia())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Medico con id:"+request.idMedico()+" no encontrado"));

            consulta.setMedico(medico);
        }

        consulta.setIntegrante(integrante);
        consulta.setFechaConsulta(request.fechaConsulta());
        consulta.setMotivo(request.motivo());
        consulta = consultaRepo.save(consulta);
        return toResponse(consulta);
    }

    @Transactional
    public void eliminarPorId(CustomUserDetails usuario,Long idConsulta){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Consulta consulta = buscarConsultaPorId(idConsulta, autenticado);

        consultaRepo.delete(consulta);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaResponse> obtenerPorFiltros(
            CustomUserDetails usuario,
            Long idIntegrante,
            Long idMedico,
            EstadoConsultaEnum estado,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String busqueda,
            Pageable pageable
    ){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        LocalDateTime inicio = (fechaDesde != null)
                ? fechaDesde.atStartOfDay()
                : LocalDateTime.of(1900, 1, 1, 0, 0);

        LocalDateTime fin = (fechaHasta != null)
                ? fechaHasta.atTime(LocalTime.MAX)
                : LocalDateTime.of(2999, 12, 31, 23, 59, 59);

        Page<Consulta> pageConsultas = consultaRepo.obtenerPorFiltros(
                autenticado.getFamilia(),
                idIntegrante,
                idMedico,
                estado,
                inicio,
                fin,
                busqueda,
                pageable
        );

        Page<ConsultaResponse> pageResponse = pageConsultas.map(this::toResponse);
        return pageResponse;
    }

    // Funciones reutilizables
    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    private Consulta buscarConsultaPorId(Long idConsulta, Usuario autenticado){
        return consultaRepo.findByIdAndIntegranteFamilia(idConsulta, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Consulta con id:"+idConsulta+" no encontrado"));
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

    private MedicoResponse toMedicoResponse(Medico entity){
        List<Especialidad> especialidades = medicoEspecialidadRepo.findByMedico(entity);

        return new MedicoResponse(
                entity.getId(),
                entity.getNombre(),
                entity.getApellidoPaterno(),
                entity.getApellidoMaterno(),
                entity.getCorreo(),
                entity.getTelefono(),
                especialidades
        );
    }

    private ConsultaDetalleResponse toDetalleResponse(Consulta entity){
        return new ConsultaDetalleResponse(
                entity.getId(),
                toIntegranteResponse(entity.getIntegrante()),
                entity.getMedico() == null ? null: toMedicoResponse(entity.getMedico()),
                entity.getFechaConsulta(),
                entity.getMotivo(),
                entity.getDiagnostico(),
                entity.getNotas(),
                entity.getCosto(),
                entity.getEstado(),
                entity.getProximaConsulta(),
                entity.getFechaRegistro()
        );
    }

    private ConsultaResponse toResponse(Consulta entity){
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
