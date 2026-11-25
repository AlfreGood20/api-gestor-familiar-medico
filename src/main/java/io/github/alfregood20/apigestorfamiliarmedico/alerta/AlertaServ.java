package io.github.alfregood20.apigestorfamiliarmedico.alerta;

import io.github.alfregood20.apigestorfamiliarmedico.alerta.dto.AlertaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.alerta.enums.TipoAlertaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.UsuarioNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlertaServ {

    private final AlertaRepo alertaRepo;
    private final UsuarioRepo usuarioRepo;

    @Transactional(readOnly = true)
    public Page<AlertaResponse> obtenerPorFiltros(
            CustomUserDetails usuario,
            Long idIntegrante,
            TipoAlertaEnum tipo,
            boolean soloNoLeidas,
            Pageable pageable
    ) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        return alertaRepo
                .buscarPorFiltro(autenticado.getFamilia(), idIntegrante, tipo, soloNoLeidas, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public long contarNoLeidas(CustomUserDetails usuario) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        return alertaRepo.countByFamiliaAndLeidaFalse(autenticado.getFamilia());
    }

    @Transactional
    public AlertaResponse marcarComoLeida(CustomUserDetails usuario, Long idAlerta) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        Alerta alerta = alertaRepo.findByIdAndFamilia(idAlerta, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Alerta con ID: "+idAlerta+" no encontrada"));

        if (!alerta.isLeida()) {
            alerta.setLeida(true);
            alerta = alertaRepo.save(alerta);
        }

        return toResponse(alerta);
    }

    @Transactional
    public int marcarTodasComoLeidas(CustomUserDetails usuario) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        return alertaRepo.marcarTodasComoLeidas(autenticado.getFamilia());
    }

    @Transactional
    public void eliminar(CustomUserDetails usuario, Long idAlerta) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        Alerta alerta = alertaRepo.findByIdAndFamilia(idAlerta, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Alerta con ID: " +idAlerta+" no encontrada"));

        alertaRepo.delete(alerta);
    }


    // ======= FUNCIONES REUTILIZABLES ========
    private Usuario buscarUsuarioPorId(Long idUsuario) {
        return usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario con ID: " + idUsuario + " no encontrado"));
    }

    private AlertaResponse toResponse(Alerta alerta) {
        Long idIntegrante = null;
        String nombreIntegrante = null;

        if (alerta.getIntegrante() != null) {
            idIntegrante = alerta.getIntegrante().getId();
            nombreIntegrante = (alerta.getIntegrante().getNombre() + " "
                    + alerta.getIntegrante().getApellidoPaterno()
                    + (alerta.getIntegrante().getApellidoMaterno() != null
                    ? " " + alerta.getIntegrante().getApellidoMaterno()
                    : "")).trim();
        }

        return new AlertaResponse(
                alerta.getId(),
                idIntegrante,
                nombreIntegrante,
                alerta.getReferencia(),
                alerta.getTipo(),
                alerta.getMensaje(),
                alerta.isLeida(),
                alerta.getFechaGeneracion()
        );
    }
}
