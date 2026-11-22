package io.github.alfregood20.apigestorfamiliarmedico.vacunas;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.exception.DuplicateException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.ReglaNegocioException;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.IntegranteRepo;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.AplicacionVacunaRequest;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.AplicacionVacunaResponse;
import io.github.alfregood20.apigestorfamiliarmedico.vacunas.dto.AplicacionVacunaUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AplicacionVacunaServ {

    private final AplicacionVacunaRepo aplicacionVacunaRepo;
    private final UsuarioRepo usuarioRepo;
    private final IntegranteRepo integranteRepo;
    private final VacunaRepo vacunaRepo;
    private final EsquemaVacunacionRepo esquemaVacunacionRepo;

    @Transactional
    public AplicacionVacunaResponse crearNuevo(CustomUserDetails usuario , AplicacionVacunaRequest request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Integrante integrante = integranteRepo.findByIdAndFamilia(request.idIntegrante(), autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Integrante con id:"+request.idIntegrante()+" no encontrado"));

        Vacuna vacuna = vacunaRepo.findById(request.idVacuna())
                .orElseThrow(() -> new RecursoNoEncontradoException("Vacuna con id:"+request.idVacuna()+" no encontrado"));

        if(request.numeroDosis() > vacuna.getDosisTotales()){
            throw new ReglaNegocioException("La vacuna "+vacuna.getNombre()+" solo se puede aplicar "+vacuna.getDosisTotales()+" dosis en total");
        }

        if(aplicacionVacunaRepo.existsByIntegranteAndVacunaAndNumeroDosis(integrante, vacuna, request.numeroDosis())){
            throw new DuplicateException("La vacuna "+vacuna.getNombre()+" con numero "+request.numeroDosis()+" dosis ya se encuntra registrado");
        }

        if (request.numeroDosis() > 1) {
            aplicacionVacunaRepo.findByIntegranteAndVacunaAndNumeroDosis(integrante, vacuna, request.numeroDosis()-1)
                    .ifPresent(dosisAnterior -> {
                        if (request.fechaAplicacion().isBefore(dosisAnterior.getFechaAplicacion())) {
                            throw new ReglaNegocioException("La fecha de la dosis "+request.numeroDosis()+
                                    " no puede ser anterior a la dosis "+(request.numeroDosis() - 1)+"("+dosisAnterior.getFechaAplicacion()+")");
                        }

                        esquemaVacunacionRepo.findByVacunaAndNumeroDosis(vacuna, request.numeroDosis())
                                .ifPresent(esquema -> {
                                    if (esquema.getIntervaloMinimoDias() != null) {
                                        long diasTranscurridos = ChronoUnit.DAYS.between(dosisAnterior.getFechaAplicacion(), request.fechaAplicacion());
                                        if (diasTranscurridos < esquema.getIntervaloMinimoDias()) {
                                            throw new ReglaNegocioException("Deben transcurrir al menos "+esquema.getIntervaloMinimoDias()+" días desde la dosis anterior");
                                        }
                                    }
                                });
                    });
        }

        AplicacionVacuna aplicacion = AplicacionVacuna.builder()
                .integrante(integrante)
                .vacuna(vacuna)
                .numeroDosis(request.numeroDosis())
                .fechaAplicacion(request.fechaAplicacion())
                .lote(request.lote())
                .lugarAplicacion(request.lugarAplicacion())
                .build();

        return toResponse(aplicacionVacunaRepo.save(aplicacion));
    }

    @Transactional
    public AplicacionVacunaResponse actualizar(CustomUserDetails usuario ,Long idAplicacion, AplicacionVacunaUpdate request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        AplicacionVacuna aplicacion = aplicacionVacunaRepo.findById(idAplicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Aplicacion con id:"+idAplicacion+" no encontrada"));

        if (!aplicacion.getIntegrante().getFamilia().getId().equals(autenticado.getFamilia().getId())) {
            throw new RecursoNoEncontradoException("Aplicación de vacuna con id:"+idAplicacion+" no encontrada");
        }

        if (request.fechaAplicacion().isBefore(aplicacion.getIntegrante().getFechaNacimiento())) {
            throw new ReglaNegocioException("La fecha de aplicación no puede ser anterior al nacimiento del integrante");
        }

        if (aplicacion.getNumeroDosis() > 1) {
            aplicacionVacunaRepo.findByIntegranteAndVacunaAndNumeroDosis(aplicacion.getIntegrante(), aplicacion.getVacuna(), aplicacion.getNumeroDosis() - 1)
                    .ifPresent(dosisAnterior -> {
                if (request.fechaAplicacion().isBefore(dosisAnterior.getFechaAplicacion())) {
                    throw new ReglaNegocioException("La fecha no puede ser anterior a la dosis "+(aplicacion.getNumeroDosis()-1));
                }
            });
        }

        aplicacionVacunaRepo.findByIntegranteAndVacunaAndNumeroDosis(aplicacion.getIntegrante(), aplicacion.getVacuna(), aplicacion.getNumeroDosis() + 1).ifPresent(dosisSiguiente -> {
            if (request.fechaAplicacion().isAfter(dosisSiguiente.getFechaAplicacion())) {
                throw new ReglaNegocioException("La fecha no puede ser posterior a la dosis "+(aplicacion.getNumeroDosis()+1));
            }
        });

        aplicacion.setFechaAplicacion(request.fechaAplicacion());
        aplicacion.setLote(request.lote());
        aplicacion.setLugarAplicacion(request.lugarAplicacion());
        return toResponse(aplicacionVacunaRepo.save(aplicacion));
    }

    @Transactional
    public void eliminar(CustomUserDetails usuario, Long idAplicacion) {
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());

        AplicacionVacuna aplicacion = aplicacionVacunaRepo.findById(idAplicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Aplicación de vacuna con id:"+idAplicacion+" no encontrada"));

        if (!aplicacion.getIntegrante().getFamilia().getId().equals(autenticado.getFamilia().getId())) {
            throw new RecursoNoEncontradoException("Aplicación de vacuna con id:"+idAplicacion+" no encontrada");
        }

        aplicacionVacunaRepo.delete(aplicacion);
    }

    // FUNCIONES REUTILIZABLES
    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID:"+id+" no encontrado"));
    }

    private AplicacionVacunaResponse toResponse(AplicacionVacuna entity){
        return new AplicacionVacunaResponse(
                entity.getId(),
                entity.getFechaAplicacion(),
                entity.getLote(),
                entity.getLugarAplicacion()
        );
    }
}