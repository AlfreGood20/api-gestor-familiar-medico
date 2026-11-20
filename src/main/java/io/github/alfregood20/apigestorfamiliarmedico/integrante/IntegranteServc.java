package io.github.alfregood20.apigestorfamiliarmedico.integrante;

import io.github.alfregood20.apigestorfamiliarmedico.alergia.*;
import io.github.alfregood20.apigestorfamiliarmedico.alergia.dto.IntegranteAlergiaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.alergia.dto.IntegranteAlergiaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.Consulta;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.ConsultaRepo;
import io.github.alfregood20.apigestorfamiliarmedico.consultas.dto.ConsultaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.exception.DuplicateException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.ReglaNegocioException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.UsuarioNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.CartillaVacunacionResponse;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteDetalleResponse;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteRequest;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.dto.IntegranteResponse;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.ParentescoEnum;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.enums.TipoSangreEnum;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.*;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.AplicacionVacunaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.DosisCartillaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.VacunaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.enums.EstadoVacunaEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.internal.CustomizerRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IntegranteServc {

    private final IntegranteRepo integranteRepo;
    private final UsuarioRepo usuarioRepo;
    private final IntegranteAlergiaRepo integranteAlergiaRepo;
    private final AlergiaRepo alergiaRepo;
    private final ConsultaRepo consultaRepo;
    private final EsquemaVacunacionRepo esquemaVacunacionRepo;
    private final AplicacionVacunaRepo aplicacionVacunaRepo;
    private final VacunaRepo vacunaRepo;

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
        integrante.setPeso(request.peso());
        integrante.setEstatura(request.estatura());
        integrante.setSexo(request.sexo());
        integrante.setParentesco(request.parentesco());

        if(request.tipoSangre() != null){
            integrante.setTipoSangre(request.tipoSangre());
        }

        integrante = integranteRepo.save(integrante);
        return toDetalleResponse(integrante);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaResponse> obtenerConsultas(
            CustomUserDetails usuario,
            Long idIntegrante,
            Pageable pageable
    ){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Integrante integrante = buscarIntegrantePorIdYFamilia(idIntegrante, autenticado);
        Page<Consulta> pageConsultas = consultaRepo.findByIntegranteFamiliaAndIntegrante(autenticado.getFamilia(), integrante, pageable);

        return pageConsultas.map(this::toConsultaResponse);
    }

    @Transactional(readOnly = true)
    public Page<CartillaVacunacionResponse> obtenerCartillaVacunacion(
            String nombreVacuna,
            EstadoVacunaEnum estado,
            Long idIntegrante,
            CustomUserDetails usuario,
            Pageable pageable
    ) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Integrante integrante = buscarIntegrantePorIdYFamilia(idIntegrante, autenticado);

        // 1. Calcular edad actual del integrante en meses
        long edadEnMeses = ChronoUnit.MONTHS.between(integrante.getFechaNacimiento(), LocalDate.now());
        String filtroNombre = (nombreVacuna != null) ? nombreVacuna.trim() : "";

        // 2. Traer las vacunas con esquema paginadas desde BD
        Page<Vacuna> pageVacunas = vacunaRepo.buscarVacunasConEsquema(filtroNombre, pageable);

        if (pageVacunas.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Vacuna> vacunasPagina = pageVacunas.getContent();

        // 3. Traer en bloque los esquemas y aplicaciones de esas vacunas (cero N+1)
        Map<Long, List<EsquemaVacunacion>> esquemasPorVacuna = esquemaVacunacionRepo
                .findByVacunaInOrderByNumeroDosisAsc(vacunasPagina)
                .stream()
                .collect(Collectors.groupingBy(e -> e.getVacuna().getId()));

        Map<String, AplicacionVacuna> aplicacionesPorClave = aplicacionVacunaRepo
                .findByIntegranteAndVacunaIn(integrante, vacunasPagina)
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getVacuna().getId() + "-" + a.getNumeroDosis(),
                        Function.identity()
                ));

        // 4. Armar cada CartillaVacunacionResponse
        List<CartillaVacunacionResponse> contenido = vacunasPagina.stream()
                .map(vacuna -> {
                    List<EsquemaVacunacion> esquemas = esquemasPorVacuna.getOrDefault(vacuna.getId(), List.of());

                    List<DosisCartillaResponse> dosisDtos = esquemas.stream()
                            .map(esquema -> {
                                String clave = vacuna.getId() + "-" + esquema.getNumeroDosis();
                                AplicacionVacuna app = aplicacionesPorClave.get(clave);

                                EstadoVacunaEnum estadoCalculado = calcularEstadoDosis(
                                        app, edadEnMeses, esquema.getEdadRecomendadaMeses()
                                );

                                AplicacionVacunaResponse appDto = (app != null)
                                        ? new AplicacionVacunaResponse(
                                        app.getId(),
                                        app.getFechaAplicacion(),
                                        app.getLote(),
                                        app.getLugarAplicacion()
                                )
                                        : null;

                                return new DosisCartillaResponse(
                                        esquema.getId(),
                                        esquema.getNumeroDosis(),
                                        esquema.getEdadRecomendadaMeses(),
                                        esquema.getIntervaloMinimoDias(),
                                        esquema.isEsRefuerzo(),
                                        estadoCalculado,
                                        appDto
                                );
                            })
                            .toList();

                    int dosisAplicadas = (int) dosisDtos.stream()
                            .filter(d -> d.estado() == EstadoVacunaEnum.APLICADA)
                            .count();

                    boolean esquemaCompleto = dosisAplicadas >= vacuna.getDosisTotales();

                    return new CartillaVacunacionResponse(
                            toVacunaResponse(vacuna),
                            dosisAplicadas,
                            esquemaCompleto,
                            dosisDtos
                    );
                })
                // Si el usuario filtró por estado (ej. ATRASADA o PENDIENTE), filtramos las vacunas que tengan al menos 1 dosis en ese estado
                .filter(item -> estado == null || item.dosis().stream().anyMatch(d -> d.estado() == estado))
                .toList();

        return new PageImpl<>(contenido, pageable, pageVacunas.getTotalElements());
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

    private EstadoVacunaEnum calcularEstadoDosis(AplicacionVacuna app, long edadEnMeses, int edadRecomendadaMeses) {
        if (app != null) {
            return EstadoVacunaEnum.APLICADA;
        }
        if (edadEnMeses > edadRecomendadaMeses) {
            return EstadoVacunaEnum.ATRASADA;
        }
        if (edadEnMeses >= edadRecomendadaMeses - 1) {
            return EstadoVacunaEnum.PENDIENTE;
        }
        return EstadoVacunaEnum.FUTURA; // Asegúrate de tener este valor en tu EstadoVacunaEnum (o usa PENDIENTE)
    }

    private VacunaResponse toVacunaResponse(Vacuna vacuna) {
        return new VacunaResponse(
                vacuna.getId(),
                vacuna.getNombre(),
                vacuna.getDescripcion(),
                vacuna.getContraindicaciones(),
                vacuna.getEnfermedadPreviene(),
                vacuna.getDosisTotales()
        );
    }
}
