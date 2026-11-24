package io.github.alfregood20.apigestorfamiliarmedico.receta;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.Consulta;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.ConsultaRepo;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.enums.EstadoConsultaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.ReglaNegocioException;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.IntegranteRepo;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.Medicamento;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.MedicamentoRepo;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.dto.MedicamentoResponse;
import io.github.alfregood20.apigestorfamiliarmedico.receta.dto.*;
import io.github.alfregood20.apigestorfamiliarmedico.receta.enums.EstadoTomaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecetaServ {

    private final RecetaRepo recetaRepo;
    private final UsuarioRepo usuarioRepo;
    private final MedicamentoRepo medicamentoRepo;
    private final IntegranteRepo integranteRepo;
    private final ConsultaRepo consultaRepo;
    private final TomaProgramadaRepo tomaProgramadRepo;

    @Transactional
    public RecetaResponse crearReceta(CustomUserDetails usuario, RecetaRequest request) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        // 1. Validar que el integrante pertenezca a la familia
        Integrante integrante = integranteRepo.findByIdAndFamilia(request.idIntegrante(), autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Integrante con ID: "+request.idIntegrante()+" no encontrado"));

        // 2. Validar fecha lógica de emisión
        if (request.fechaEmision().isBefore(integrante.getFechaNacimiento())) {
            throw new ReglaNegocioException("La fecha de emisión no puede ser anterior a la fecha de nacimiento del integrante");
        }

        // 3. Validar consulta opcional y verificar que sea del mismo integrante y no esté cancelada
        Consulta consulta = null;
        if (request.idConsulta() != null) {
            consulta = consultaRepo.findByIdAndIntegranteFamilia(request.idConsulta(), autenticado.getFamilia())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Consulta con ID: "+request.idConsulta()+" no encontrada"));

            if (!consulta.getIntegrante().getId().equals(integrante.getId())) {
                throw new ReglaNegocioException("La consulta seleccionada no pertenece al integrante indicado");
            }

            if (consulta.getEstado() == EstadoConsultaEnum.CANCELADA) {
                throw new ReglaNegocioException("No se puede vincular una receta a una consulta CANCELADA");
            }
        }

        // 4. Construir cabecera de la Receta
        Receta receta = toEntity(request);
        receta.setIntegrante(integrante);
        receta.setConsulta(consulta);

        // 5. Construir cada DetalleReceta y generar automáticamente sus TomasProgramadas
        for (DetalleRecetaRequest item : request.detalles()) {

            Medicamento medicamento = medicamentoRepo.findById(item.idMedicamento())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Medicamento con ID: "+item.idMedicamento()+" no encontrado"));

            DetalleReceta detalle = DetalleReceta.builder()
                    .medicamento(medicamento)
                    .dosis(item.dosis())
                    .frecuenciaHoras(item.frecuenciaHoras())
                    .duracionDias(item.duracionDias())
                    .fechaHoraInicio(item.fechaHoraInicio())
                    .build();

            generarTomasProgramadas(detalle);
            receta.agregarDetalle(detalle);
        }

        return toResponse(recetaRepo.save(receta));
    }

    @Transactional(readOnly = true)
    public RecetaResponse obtenerPorId(CustomUserDetails usuario, Long idReceta) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        Receta receta = recetaRepo.findByIdAndIntegranteFamilia(idReceta, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta médica con ID: "+idReceta+" no encontrada"));

        return toResponse(receta);
    }

    @Transactional
    public RecetaResponse finalizarReceta(CustomUserDetails usuario, Long idReceta) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        // 1. Validar que la receta exista y pertenezca a la familia
        Receta receta = recetaRepo.findByIdAndIntegranteFamilia(idReceta, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta médica con ID: " +idReceta+" no encontrada"));

        // 2. Validar regla de negocio: evitar finalizar una receta que ya fue suspendida/finalizada
        if (!receta.isActiva()) {
            throw new ReglaNegocioException("Esta receta ya se encuentra finalizada o suspendida");
        }

        // 3. Desactivar la receta y cancelar todas las tomas que sigan PENDIENTES
        receta.setActiva(false);

        for (DetalleReceta detalle : receta.getDetalles()) {
            for (TomaProgramada toma : detalle.getTomas()) {
                if (toma.getEstado() == EstadoTomaEnum.PENDIENTE) {
                    toma.setEstado(EstadoTomaEnum.CANCELADA);
                }
            }
        }

        return toResponse(recetaRepo.save(receta));
    }

    @Transactional
    public void eliminarReceta(CustomUserDetails usuario, Long idReceta) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        Receta receta = recetaRepo.findByIdAndIntegranteFamilia(idReceta, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta médica con ID: " + idReceta + " no encontrada"));

        recetaRepo.delete(receta);
    }

    @Transactional(readOnly = true)
    public Page<RecetaResponse> obtenerPorFiltros(CustomUserDetails usuario, Long idIntegrante, Long idConsulta, Boolean activa, String busqueda, Pageable pageable) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        Page<Receta> pagina = recetaRepo.obtenerPorFiltros(autenticado.getFamilia(), idIntegrante, idConsulta, activa, busqueda, pageable);
        return pagina.map(this::toResponse);
    }

    @Transactional
    public RecetaResponse actualizarReceta(CustomUserDetails usuario, Long idReceta, RecetaUpdate request) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        // 1. Buscar la receta validando pertenencia familiar
        Receta receta = recetaRepo.findByIdAndIntegranteFamilia(idReceta, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta médica con ID: "+idReceta+" no encontrada"));

        // 2. Validar fecha lógica de emisión contra nacimiento del paciente
        if (request.fechaEmision().isBefore(receta.getIntegrante().getFechaNacimiento())) {
            throw new ReglaNegocioException("La fecha de emisión no puede ser anterior a la fecha de nacimiento del integrante");
        }

        // 3. Validar consulta opcional (vincular, cambiar o desvincular si viene null)
        Consulta consulta = null;
        if (request.idConsulta() != null) {
            consulta = consultaRepo.findByIdAndIntegranteFamilia(request.idConsulta(), autenticado.getFamilia())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Consulta con ID: "+request.idConsulta()+" no encontrada"));

            if (!consulta.getIntegrante().getId().equals(receta.getIntegrante().getId())) {
                throw new ReglaNegocioException("La consulta seleccionada no pertenece al integrante de esta receta");
            }

            if (consulta.getEstado() == EstadoConsultaEnum.CANCELADA) {
                throw new ReglaNegocioException("No se puede vincular una receta a una consulta CANCELADA");
            }
        }

        // 4. Actualizar únicamente los datos de cabecera
        receta.setConsulta(consulta);
        receta.setFechaEmision(request.fechaEmision());
        receta.setIndicaciones(request.indicaciones());

        return toResponse(recetaRepo.save(receta));
    }

    // ============ Funciones reutilizables ============
    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID:"+id+" no encontrado"));
    }

    private Receta toEntity(RecetaRequest request) {
        return Receta.builder()
                .fechaEmision(request.fechaEmision())
                .indicaciones(request.indicaciones())
                .build();
    }

    private void generarTomasProgramadas(DetalleReceta detalle) {
        int diasAGenerar = (detalle.getDuracionDias() != null) ? detalle.getDuracionDias() : 7;
        int totalTomas = (diasAGenerar * 24) / detalle.getFrecuenciaHoras();

        if (totalTomas <= 0) {
            totalTomas = 1;
        }

        LocalDateTime fechaToma = detalle.getFechaHoraInicio();

        for (int i = 0; i < totalTomas; i++) {
            TomaProgramada toma = TomaProgramada.builder()
                    .fechaHoraProgramada(fechaToma)
                    .estado(EstadoTomaEnum.PENDIENTE)
                    .build();

            detalle.agregarToma(toma);
            fechaToma = fechaToma.plusHours(detalle.getFrecuenciaHoras());
        }
    }

    private RecetaResponse toResponse(Receta entity) {
        return new RecetaResponse(
                entity.getId(),
                toIntegranteResponse(entity.getIntegrante()),
                entity.getConsulta() != null ? toConsultaResponse(entity.getConsulta()) : null,
                entity.getFechaEmision(),
                entity.getIndicaciones(),
                entity.isActiva(),
                entity.getFechaRegistro(),
                entity.getDetalles().stream().map(this::toDetalleResponse).toList()
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

    private MedicamentoResponse toMedicamentoResponse (Medicamento response){
        return new MedicamentoResponse(
                response.getId(),
                response.getNombreComercial(),
                response.getSustanciaActiva(),
                response.getConcentracion(),
                response.getFormaFarmaceutica(),
                response.getViaAdministracion()
        );
    }

    private DetalleRecetaResponse toDetalleResponse(DetalleReceta detalle) {
        int dias = (detalle.getDuracionDias() != null) ? detalle.getDuracionDias() : 7;
        int totalTomas = (dias * 24) / detalle.getFrecuenciaHoras();

        if (totalTomas <= 0) {
            totalTomas = 1;
        }

        int tomasCompletadas = tomaProgramadRepo.countByDetalleRecetaIdAndEstado(detalle.getId(), EstadoTomaEnum.TOMADA);

        return new DetalleRecetaResponse(
                detalle.getId(),
                toMedicamentoResponse(detalle.getMedicamento()),
                detalle.getDosis(),
                detalle.getFrecuenciaHoras(),
                detalle.getDuracionDias(),
                detalle.getFechaHoraInicio(),
                totalTomas,
                tomasCompletadas
        );
    }
}
