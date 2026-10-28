package io.github.alfregood20.apigestorfamiliarmedico.integrante;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.*;
import io.github.alfregood20.apigestorfamiliarmedico.alergia.dto.IntegranteAlergiaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.alergia.dto.IntegranteAlergiaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.exception.DuplicateException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.ReglaNegocioException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.UsuarioNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteDetalleResponse;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteRequest;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.ParentescoEnum;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.TipoSangreEnum;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntegranteServc {

    private final IntegranteRepo integranteRepo;
    private final UsuarioRepo usuarioRepo;
    private final IntegranteAlergiaRepo integranteAlergiaRepo;
    private final AlergiaRepo alergiaRepo;

    @Transactional
    public IntegranteResponse crear(CustomUserDetails usuario, IntegranteRequest request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        Integrante integrante = toEntity(request);
        integrante.setFamilia(autenticado.getFamilia());

        return toResponse(integranteRepo.save(integrante));
    }

   @Transactional(readOnly = true)
    public List<IntegranteResponse> obtenerListado(CustomUserDetails usuario){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        return integranteRepo.findByFamilia(autenticado.getFamilia())
                .stream()
                .map(integrante -> toResponse(integrante))
                .toList();
    }

    @Transactional
    public void eliminar(CustomUserDetails usuario, Long idIntegrante){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Integrante integrante = buscarIntegrantePorIdYFamilia(idIntegrante, autenticado);

        integranteRepo.delete(integrante);
    }

    @Transactional
    public IntegranteDetalleResponse asignarleAlergia(CustomUserDetails usuario, Long idIntegrante,IntegranteAlergiaRequest request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Integrante integrante = buscarIntegrantePorIdYFamilia(idIntegrante, autenticado);

        Alergia alergia = alergiaRepo.findById(request.idAlergia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Alergia con ID: "+request.idAlergia()+" no encontrado"));

        IntegranteAlergiaId id = new IntegranteAlergiaId(idIntegrante, request.idAlergia());

        if (integranteAlergiaRepo.existsById(id)) {
            throw new DuplicateException("Ya tiene registrado la alergia: "+alergia.getNombre());
        }

        IntegranteAlergia integranteAlergia = IntegranteAlergia.builder()
                .id(id)
                .integrante(integrante)
                .alergia(alergia)
                .severidad(request.severidad())
                .observaciones(request.observaciones())
                .build();

        integranteAlergia = integranteAlergiaRepo.save(integranteAlergia);

        return toDetalleResponse(integrante);
    }

    @Transactional(readOnly = true)
    public IntegranteDetalleResponse obtenerDetalle(CustomUserDetails usuario, Long idIntegrante){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Integrante integrante = buscarIntegrantePorIdYFamilia(idIntegrante, autenticado);

        return toDetalleResponse(integrante);
    }

    @Transactional
    public IntegranteDetalleResponse actualizarDatos(CustomUserDetails usuario,Long idIntegrante ,IntegranteRequest request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Integrante integrante = buscarIntegrantePorIdYFamilia(idIntegrante, autenticado);

        boolean eraTitular = integrante.getParentesco() == ParentescoEnum.TITULAR;
        boolean seraTitular = request.parentesco() == ParentescoEnum.TITULAR;

        if (eraTitular != seraTitular) {
            throw new ReglaNegocioException("No se puede asignar ni quitar el parentesco de titular");
        }

        integrante.setNombre(request.nombre());
        integrante.setApellidoPaterno(request.apellidoPaterno());
        integrante.setApellidoMaterno(request.apellidoMaterno());
        integrante.setFechaNacimiento(request.fechaNacimiento());
        integrante.setSexo(request.sexo());
        integrante.setParentesco(request.parentesco());

        if(request.tipoSangre() != null){
            integrante.setTipoSangre(request.tipoSangre());
        }

        integrante = integranteRepo.save(integrante);
        return toDetalleResponse(integrante);
    }


    // METODOS REUTILIZABLES
    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    private Integrante toEntity(IntegranteRequest request){
        return Integrante.builder()
                .nombre(request.nombre())
                .apellidoPaterno(request.apellidoPaterno())
                .apellidoMaterno(request.apellidoMaterno())
                .fechaNacimiento(request.fechaNacimiento())
                .peso(request.peso())
                .estatura(request.estatura())
                .sexo(request.sexo())
                .tipoSangre(request.tipoSangre() != null ? request.tipoSangre() : TipoSangreEnum.SIN_DEFINIR)
                .parentesco(request.parentesco())
                .build();
    }

    private IntegranteResponse toResponse(Integrante response){
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

    private Integrante buscarIntegrantePorIdYFamilia(Long idIntegrante, Usuario autenticado){
        return integranteRepo.findByIdAndFamilia(idIntegrante, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Integrante con ID: "+idIntegrante+" no encontrado"));
    }

    private IntegranteDetalleResponse toDetalleResponse(Integrante integrante) {

        List<IntegranteAlergiaResponse> alergias = integranteAlergiaRepo.findByIntegrante(integrante)
                .stream()
                .map(ia -> new IntegranteAlergiaResponse(
                        ia.getAlergia().getId(),
                        ia.getAlergia().getNombre(),
                        ia.getAlergia().getTipo(),
                        ia.getSeveridad(),
                        ia.getObservaciones()
                ))
                .toList();

        return new IntegranteDetalleResponse(
                integrante.getId(),
                integrante.getNombre(),
                integrante.getApellidoPaterno(),
                integrante.getApellidoMaterno(),
                integrante.getFechaNacimiento(),
                integrante.getPeso(),
                integrante.getEstatura(),
                integrante.getSexo(),
                integrante.getTipoSangre(),
                integrante.getParentesco(),
                integrante.getFechaRegistro(),
                alergias
        );
    }

}
