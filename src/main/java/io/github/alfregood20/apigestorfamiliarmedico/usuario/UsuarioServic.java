package io.github.alfregood20.apigestorfamiliarmedico.usuario;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.exception.DuplicateException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.UsuarioNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.CuidadorRequest;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.CuidadorResponse;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.EstadoRequest;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.enums.EstadoUsuarioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.enums.RolEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServic {

    private final UsuarioRepo usuarioRepo;
    private final PasswordEncoder encoder;

    @Transactional
    public CuidadorResponse crearCuidador(CustomUserDetails usuario, CuidadorRequest request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        if(usuarioRepo.existsByCorreo(request.correo())){
            throw new DuplicateException("Correo: "+request.correo()+" ,ya se encuentra registrado");
        }

        Usuario cuidador = Usuario.builder()
                .familia(autenticado.getFamilia())
                .nombre(request.nombre())
                .apellidoPaterno(request.apellidoPaterno())
                .apellidoMaterno(request.apellidoMaterno())
                .correo(request.correo())
                .telefono(request.telefono())
                .contrasenaHash(encoder.encode(request.contrasena()))
                .rol(RolEnum.CUIDADOR)
                .build();

        cuidador = usuarioRepo.save(cuidador);

        return toCuidadorResponse(cuidador);
    }

    public List<CuidadorResponse> obtenerCuidadores(CustomUserDetails usuario){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        return usuarioRepo.findByFamiliaAndRolOrderByFechaRegistroDesc(autenticado.getFamilia(), RolEnum.CUIDADOR)
                .stream()
                .map(cuidador -> toCuidadorResponse(cuidador))
                .toList();
    }

    @Transactional
    public CuidadorResponse cambiarEstadoCuidador(CustomUserDetails usuario, Long idCuidador, EstadoRequest request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        Usuario cuidador = usuarioRepo.findByIdAndFamiliaAndRol(idCuidador, autenticado.getFamilia(), RolEnum.CUIDADOR)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuidador con ID:" + idCuidador + " no encontrado"));

        cuidador.setEstado(request.estado());
        cuidador = usuarioRepo.save(cuidador);
        return toCuidadorResponse(cuidador);
    }

    @Transactional
    public void eliminarCuidador(CustomUserDetails usuario, Long idCuidador){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        Usuario cuidador = usuarioRepo.findByIdAndFamiliaAndRol(idCuidador, autenticado.getFamilia(), RolEnum.CUIDADOR)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuidador con ID:" + idCuidador + " no encontrado"));

        usuarioRepo.delete(cuidador);
    }

    // METODOS REUTILIZABLES
    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    private CuidadorResponse toCuidadorResponse(Usuario cuidador){
        return new CuidadorResponse(
                cuidador.getId(),
                cuidador.getNombre(),
                cuidador.getApellidoPaterno(),
                cuidador.getApellidoMaterno(),
                cuidador.getCorreo(),
                cuidador.getTelefono(),
                cuidador.getRol(),
                cuidador.getEstado(),
                cuidador.getFechaRegistro()
        );
    }
}
