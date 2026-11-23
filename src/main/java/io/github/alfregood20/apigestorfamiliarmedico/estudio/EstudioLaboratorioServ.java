package io.github.alfregood20.apigestorfamiliarmedico.estudio;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.Consulta;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.ConsultaRepo;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.enums.EstadoConsultaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.dto.EstudioLaboratorioRequest;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.dto.EstudioLaboratorioResponse;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.dto.EstudioLaboratorioUpdate;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.dto.EstudioResultadoRequest;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.CategoriaEstudioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.EstadoEstudioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.ReglaNegocioException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.UsuarioNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.IntegranteRepo;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstudioLaboratorioServ {

    private final EstudioLaboratorioRepo estudioLaboratorioRepo;
    private final UsuarioRepo usuarioRepo;
    private final IntegranteRepo integranteRepo;
    private final ConsultaRepo consultaRepo;

    @Transactional
    public EstudioLaboratorioResponse crearNuevo(CustomUserDetails usuario, EstudioLaboratorioRequest request) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        // 1. Validar que el integrante exista y pertenezca a la familia del usuario
        Integrante integrante = integranteRepo.findByIdAndFamilia(request.idIntegrante(), autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Integrante con ID: " + request.idIntegrante() + " no encontrado"));

        // 2. Validar fecha lógica (no puede hacerse un estudio antes de nacer)
        if (request.fechaEstudio().isBefore(integrante.getFechaNacimiento())) {
            throw new ReglaNegocioException("La fecha del estudio no puede ser anterior a la fecha de nacimiento del integrante");
        }

        // 3. Validar la consulta opcional (si viene, debe ser de la misma familia, del mismo integrante y no estar cancelada)
        Consulta consulta = null;
        if (request.idConsulta() != null) {
            consulta = consultaRepo.findByIdAndIntegranteFamilia(request.idConsulta(), autenticado.getFamilia())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Consulta con ID: "+request.idConsulta() +" no encontrada"));

            if (!consulta.getIntegrante().getId().equals(integrante.getId())) {
                throw new ReglaNegocioException("La consulta seleccionada no pertenece al integrante indicado");
            }

            if (consulta.getEstado() == EstadoConsultaEnum.CANCELADA) {
                throw new ReglaNegocioException("No se puede vincular un estudio de laboratorio a una consulta CANCELADA");
            }
        }

        // 4. Determinar el estado inicial automáticamente según si ya trae resultado o no
        EstadoEstudioEnum estadoInicial = (request.resultado() != null && !request.resultado().isBlank())
                ? EstadoEstudioEnum.REALIZADO
                : EstadoEstudioEnum.PENDIENTE;

        // 5. Construir y guardar la entidad
        EstudioLaboratorio estudio = toEntity(request);
        estudio.setIntegrante(integrante);
        estudio.setConsulta(consulta);
        estudio.setEstado(estadoInicial);

        return toResponse(estudioLaboratorioRepo.save(estudio));
    }

    @Transactional
    public void eliminar(CustomUserDetails usuario, Long idEstudio) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        EstudioLaboratorio estudio = estudioLaboratorioRepo.findByIdAndIntegranteFamilia(idEstudio, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudio de laboratorio con ID: "+idEstudio+" no encontrado"));

        estudioLaboratorioRepo.delete(estudio);
    }

    @Transactional
    public EstudioLaboratorioResponse actualizar(CustomUserDetails usuario, Long idEstudio, EstudioLaboratorioUpdate request) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        // 1. Buscar el estudio validando que pertenezca a un integrante de la familia
        EstudioLaboratorio estudio = estudioLaboratorioRepo.findByIdAndIntegranteFamilia(idEstudio, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudio de laboratorio con ID: "+idEstudio+" no encontrado"));

        // 2. Validar fecha lógica respecto al nacimiento del integrante
        if (request.fechaEstudio().isBefore(estudio.getIntegrante().getFechaNacimiento())) {
            throw new ReglaNegocioException("La fecha del estudio no puede ser anterior a la fecha de nacimiento del integrante");
        }

        // 3. Validar la consulta opcional (si viene, debe ser de la misma familia, del mismo integrante y no estar cancelada)
        Consulta consulta = null;
        if (request.idConsulta() != null) {
            consulta = consultaRepo.findByIdAndIntegranteFamilia(request.idConsulta(), autenticado.getFamilia())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Consulta con ID: "+request.idConsulta()+" no encontrada"));

            if (!consulta.getIntegrante().getId().equals(estudio.getIntegrante().getId())) {
                throw new ReglaNegocioException("La consulta seleccionada no pertenece al integrante del estudio");
            }

            if (consulta.getEstado() == EstadoConsultaEnum.CANCELADA) {
                throw new ReglaNegocioException("No se puede vincular un estudio de laboratorio a una consulta CANCELADA");
            }
        }

        // 4. Actualizar los datos del estudio
        estudio.setConsulta(consulta);
        estudio.setFechaEstudio(request.fechaEstudio());
        estudio.setTipoEstudio(request.tipoEstudio());
        estudio.setCategoria(request.categoria());
        estudio.setLaboratorio(request.laboratorio());
        estudio.setIndicaciones(request.indicaciones());
        estudio.setResultado(request.resultado());
        estudio.setValoresReferencia(request.valoresReferencia());
        estudio.setObservaciones(request.observaciones());
        estudio.setArchivoUrl(request.archivoUrl());
        estudio.setCosto(request.costo());

        // 5. Validar y asignar el estado con coherencia clínica
        boolean tieneResultado = request.resultado() != null && !request.resultado().isBlank();

        if (request.estado() == EstadoEstudioEnum.CANCELADO) {
            if (tieneResultado) {
                throw new ReglaNegocioException("No puedes marcar como CANCELADO un estudio que ya tiene resultados");
            }
            estudio.setEstado(EstadoEstudioEnum.CANCELADO);
        } else {
            estudio.setEstado(tieneResultado ? EstadoEstudioEnum.REALIZADO : EstadoEstudioEnum.PENDIENTE);
        }

        return toResponse(estudioLaboratorioRepo.save(estudio));
    }

    @Transactional
    public EstudioLaboratorioResponse cargarResultado(CustomUserDetails usuario, Long idEstudio, EstudioResultadoRequest request) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        // 1. Buscar el estudio validando que pertenezca a la familia del usuario
        EstudioLaboratorio estudio = estudioLaboratorioRepo
                .findByIdAndIntegranteFamilia(idEstudio, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudio de laboratorio con ID: " + idEstudio + " no encontrado"
                ));

        // 2. Validar regla de negocio: no cargar resultados a un estudio cancelado
        if (estudio.getEstado() == EstadoEstudioEnum.CANCELADO) {
            throw new ReglaNegocioException("No se pueden cargar resultados a un estudio que fue CANCELADO");
        }

        // 3. Cargar los hallazgos y transicionar automáticamente el estado a REALIZADO
        estudio.setResultado(request.resultado());
        estudio.setValoresReferencia(request.valoresReferencia());
        estudio.setObservaciones(request.observaciones());
        estudio.setArchivoUrl(request.archivoUrl());
        estudio.setEstado(EstadoEstudioEnum.REALIZADO);

        return toResponse(estudioLaboratorioRepo.save(estudio));
    }

    @Transactional(readOnly = true)
    public EstudioLaboratorioResponse obtenerPorId(CustomUserDetails usuario, Long idEstudio) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        EstudioLaboratorio estudio = estudioLaboratorioRepo.findByIdAndIntegranteFamilia(idEstudio, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudio de laboratorio con ID: "+idEstudio+ " no encontrado"));

        return toResponse(estudio);
    }

    @Transactional(readOnly = true)
    public Page<EstudioLaboratorioResponse> obtenerPorFiltros(
            CustomUserDetails usuario,
            Long idIntegrante,
            CategoriaEstudioEnum categoria,
            EstadoEstudioEnum estado,
            boolean pendientes,
            String busqueda,
            Pageable pageable
    ) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        Page<EstudioLaboratorio> pagina = estudioLaboratorioRepo.obtenerPorFiltros(
                autenticado.getFamilia(),
                idIntegrante,
                categoria,
                estado,
                pendientes,
                busqueda,
                pageable
        );

        return pagina.map(this::toResponse);
    }

    //============= Funciones reutilizables =================
    private EstudioLaboratorioResponse toResponse(EstudioLaboratorio entity) {
        return new EstudioLaboratorioResponse(
                entity.getId(),
                toIntegranteResponse(entity.getIntegrante()),
                entity.getConsulta() != null ? toConsultaResponse(entity.getConsulta()) : null,
                entity.getFechaEstudio(),
                entity.getTipoEstudio(),
                entity.getCategoria(),
                entity.getLaboratorio(),
                entity.getIndicaciones(),
                entity.getEstado(),
                entity.getResultado(),
                entity.getValoresReferencia(),
                entity.getObservaciones(),
                entity.getArchivoUrl(),
                entity.getCosto(),
                entity.getFechaRegistro()
        );
    }

    private EstudioLaboratorio toEntity(EstudioLaboratorioRequest request) {
        return EstudioLaboratorio.builder()
                .fechaEstudio(request.fechaEstudio())
                .tipoEstudio(request.tipoEstudio())
                .categoria(request.categoria())
                .laboratorio(request.laboratorio())
                .indicaciones(request.indicaciones())
                .resultado(request.resultado())
                .valoresReferencia(request.valoresReferencia())
                .observaciones(request.observaciones())
                .archivoUrl(request.archivoUrl())
                .costo(request.costo())
                .build();
    }

    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
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
}
