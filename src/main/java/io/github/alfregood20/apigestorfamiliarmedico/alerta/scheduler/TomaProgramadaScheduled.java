package io.github.alfregood20.apigestorfamiliarmedico.alerta.scheduler;

import io.github.alfregood20.apigestorfamiliarmedico.alerta.Alerta;
import io.github.alfregood20.apigestorfamiliarmedico.alerta.AlertaRepo;
import io.github.alfregood20.apigestorfamiliarmedico.alerta.enums.TipoAlertaEnum;
import io.github.alfregood20.apigestorfamiliarmedico.familia.Familia;
import io.github.alfregood20.apigestorfamiliarmedico.integrante.Integrante;
import io.github.alfregood20.apigestorfamiliarmedico.receta.DetalleReceta;
import io.github.alfregood20.apigestorfamiliarmedico.receta.Receta;
import io.github.alfregood20.apigestorfamiliarmedico.receta.TomaProgramada;
import io.github.alfregood20.apigestorfamiliarmedico.receta.TomaProgramadaRepo;
import io.github.alfregood20.apigestorfamiliarmedico.receta.enums.EstadoTomaEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TomaProgramadaScheduled {

    private final TomaProgramadaRepo tomaProgramadaRepo;
    private final AlertaRepo alertaRepo;

    //EJECUTA CADA 15 MINUTOS
    @Transactional
    @Scheduled(cron = "0 */15 * * * *", zone = "America/Mexico_City")
    public void generarAlertasDosisPendientes(){
        log.info("GENERANDO ALERTAS PARA TOMAS PENDIENTES.......");
        LocalDateTime ahora = LocalDateTime.now();

        List<TomaProgramada> tomasPendientes = tomaProgramadaRepo.buscarPendientesParaAlerta(
                EstadoTomaEnum.PENDIENTE,
                ahora.minusHours(2),
                ahora.plusMinutes(30)
        );

        if(tomasPendientes.isEmpty()){
            log.info("NO HAY TOMAS PENDIENTES, PARA GENERAR ALERTAS");
        }

        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm a");
        tomasPendientes.forEach(t -> {

            DetalleReceta detalle = t.getDetalleReceta();
            Receta receta = detalle.getReceta();
            Integrante integrante = receta.getIntegrante();
            Familia familia = integrante.getFamilia();

            boolean atrasada = t.getFechaHoraProgramada().isBefore(ahora);
            String prefijo = atrasada ? "Toma atrasada" : "Próxima toma";

            String mensaje = "%s para %s: %s (%s) a las %s".formatted(
                            prefijo, integrante.getNombre(), detalle.getMedicamento().getNombreComercial(),
                            detalle.getDosis() ,t.getFechaHoraProgramada().format(formatoHora)
                    );

            if(!alertaRepo.existsByFamiliaAndTipoAndReferenciaAndLeidaFalse(familia, TipoAlertaEnum.TOMA_PENDIENTE, t.getId())){
                Alerta alerta = Alerta.builder()
                        .familia(familia)
                        .integrante(integrante)
                        .referencia(t.getId())
                        .tipo(TipoAlertaEnum.TOMA_PENDIENTE)
                        .mensaje(mensaje)
                        .build();

                alertaRepo.save(alerta);
            }
        });

        log.info("SE A GENERADO ALERTAS EXITOSAMENTE");
    }
}
