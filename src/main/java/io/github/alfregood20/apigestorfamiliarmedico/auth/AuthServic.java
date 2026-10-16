package io.github.alfregood20.apigestorfamiliarmedico.auth;

import io.github.alfregood20.apigestorfamiliarmedico.auth.dto.*;
import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.exception.DuplicateException;
import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import io.github.alfregood20.apigestorfamiliarmedico.familia.FamiliaRepo;
import io.github.alfregood20.apigestorfamiliarmedico.familia.dto.FamiliaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.IntegranteRepo;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.ParentescoEnum;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.enums.RolEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServic {

    private final FamiliaRepo familiaRepo;
    private final IntegranteRepo integranteRepo;
    private final UsuarioRepo usuarioRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Transactional
    public RegistroResponse registrar(RegistroRequest request){

        if(usuarioRepo.existsByCorreo(request.correo())){
            throw new DuplicateException("Correo: "+request.correo()+" ,ya se encuentra registrado");
        }

        Familia familia = Familia.builder()
                .nombre(request.familia().nombre()).
                direccion(request.familia().direccion())
                .build();

        familia = familiaRepo.save(familia);

        Usuario usuario = Usuario.builder()
                .familia(familia)
                .nombre(request.nombre())
                .apellidoPaterno(request.apellidoPaterno())
                .apellidoMaterno(request.apellidoMaterno())
                .correo(request.correo())
                .telefono(request.telefono())
                .contrasenaHash(encoder.encode(request.contrasena()))
                .rol(RolEnum.ADMIN)
                .build();

        usuario = usuarioRepo.save(usuario);

        Integrante integranteTitular = Integrante.builder()
                .familia(familia)
                .usuario(usuario)
                .nombre(usuario.getNombre())
                .apellidoPaterno(usuario.getApellidoPaterno())
                .apellidoMaterno(usuario.getApellidoMaterno())
                .fechaNacimiento(request.fechaNacimiento())
                .sexo(request.sexo())
                .parentesco(ParentescoEnum.TITULAR)
                .build();

        integranteRepo.save(integranteTitular);

        return new RegistroResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidoPaterno(),
                usuario.getApellidoMaterno(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getEstado().name(),
                new FamiliaResponse(usuario.getFamilia().getId(),
                        usuario.getFamilia().getNombre(),usuario.getFamilia().getDireccion()
                ),
                usuario.getFechaRegistro()
        );
    }

    public LoginResponse login(LoginRequest requestBody, HttpServletRequest request, HttpServletResponse response){

        Authentication auth = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(requestBody.correo(), requestBody.contrasena())
        );

        // evita session fixation si ya existía una sesión
        if (request.getSession(false) != null) {
            request.changeSessionId();
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return new LoginResponse(
                "Login exitoso",
                user.getUsername(),
                user.getRol()
        );
    }

    public MeResponse autenticado(CustomUserDetails usuario){
        Usuario autenticado = usuario.getUsuario();
        return new MeResponse(
          autenticado.getId(),
          autenticado.getNombre(),
          autenticado.getApellidoPaterno(),
          autenticado.getApellidoMaterno(),
          autenticado.getCorreo(),
          autenticado.getTelefono(),
          autenticado.getRol(),
          autenticado.getFechaRegistro()
        );
    }
}
