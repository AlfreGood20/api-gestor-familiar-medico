package io.github.alfregood20.apigestorfamiliarmedico.integrante;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.UsuarioNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteRequest;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
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
        Integrante integrante = integranteRepo.findByIdAndFamilia(idIntegrante, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Integrante con ID: "+idIntegrante+" no encontrado"));

        integranteRepo.delete(integrante);
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
                response.getSexo(),
                response.getTipoSangre(),
                response.getParentesco(),
                response.getFechaRegistro()
        );
    }
}
