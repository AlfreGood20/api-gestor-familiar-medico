package io.github.alfregood20.apigestorfamiliarmedico.medico;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.especialidad.*;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.medico.dto.MedicoRequest;
import io.github.alfregood20.apigestorfamiliarmedico.medico.dto.MedicoResponse;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicoServ {

    private final MedicoRepo medicoRepo;
    private final UsuarioRepo usuarioRepo;
    private final MedicoEspecialidadRepo medicoEspecialidadRepo;
    private final EspecialidadRepo especialidadRepo;

    @Transactional
    public MedicoResponse crearNuevo(CustomUserDetails usuario, MedicoRequest request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Medico medico = toEntity(request);

        List<Especialidad> especialidades = request.especialidadesIds().stream()
                .map(id -> especialidadRepo.findById(id)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Especialidad con ID:"+id+" no encontrado")))
                .toList();

        medico.setFamilia(autenticado.getFamilia());
        medico = medicoRepo.save(medico);

        for(Especialidad especialidad: especialidades){
            MedicoEspecialidadId id = new MedicoEspecialidadId(medico.getId(), especialidad.getId());
            MedicoEspecialidad medicoEspecialidad = MedicoEspecialidad.builder()
                            .id(id)
                            .especialidad(especialidad)
                            .medico(medico)
                            .build();
            medicoEspecialidadRepo.save(medicoEspecialidad);
        }

        return toResponse(medico);
    }

    @Transactional(readOnly = true)
    public List<MedicoResponse> listado(CustomUserDetails usuario){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        return medicoRepo.findAllByFamilia(autenticado.getFamilia())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicoResponse obtenerPorId(CustomUserDetails usuario ,Long idMedico){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        Medico medico = medicoRepo.findByIdAndFamilia(idMedico,autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico con ID:"+idMedico+" no encontrado"));

        return toResponse(medico);
    }

    @Transactional
    public void eliminarPorId(CustomUserDetails usuario, Long idMedico){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        Medico medico = medicoRepo.findByIdAndFamilia(idMedico, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico con ID:"+idMedico+" no encontrado"));

        medicoRepo.delete(medico);
    }



    // Funciones reutilizables
    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID:"+id+" no encontrado"));
    }

    private Medico toEntity(MedicoRequest request){
        return Medico.builder()
                .nombre(request.nombre())
                .apellidoPaterno(request.apellidoPaterno())
                .apellidoMaterno(request.apellidoMaterno())
                .correo(request.correo())
                .telefono(request.telefono())
                .build();
    }

    private MedicoResponse toResponse(Medico entity){
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

}