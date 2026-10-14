package io.github.alfregood20.apigestorfamiliarmedico.usuario;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.exception.DuplicateException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.UsuarioNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.CuidadorRequest;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.dto.CuidadorResponse;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.enums.RolEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // METODOS REUTILIZABLES
    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }
}
