package io.github.alfregood20.apigestorfamiliarmedico.procedural;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.Map;

@RestController
@RequestMapping("/ejemplos-sqlprocedural")
@RequiredArgsConstructor
@Tag(name = "Ejemplos de aplicacion de sqlProcedural")
public class EjemploProcedural {

    private final JdbcTemplate jdbcTemplate;

    @Operation(
            summary = "Calcular edad en meses de un integrante",
            description = "Invoca la función SQL fn_edad_meses_integrante en PostgreSQL a partir de la fecha de nacimiento del integrante y devuelve su edad calculada en meses."
    )
    // 1. FUNCTION: Consulta un valor calculado en PostgreSQL (fn_edad_meses_integrante)
    @GetMapping("/integrantes/{idIntegrante}/edad-meses")
    public ResponseEntity obtenerEdadMeses(@PathVariable Long idIntegrante) {
        Integer edadMeses = jdbcTemplate.queryForObject(
                "SELECT fn_edad_meses_integrante(?)",
                Integer.class,
                idIntegrante
        );

        return ResponseEntity.ok(Map.of(
                "idIntegrante", idIntegrante,
                "edadEnMeses", edadMeses
        ));
    }

    @Operation(
            summary = "Contar consultas de un integrante por estado",
            description = "Ejecuta la función SQL fn_total_consultas_integrante en PostgreSQL para obtener el total de consultas del integrante, filtrando opcionalmente por estado."
    )
    // 2. FUNCTION: Cuenta consultas por integrante y estado opcional (fn_total_consultas_integrante)
    @GetMapping("/integrantes/{idIntegrante}/total-consultas")
    public ResponseEntity contarConsultas(
            @PathVariable Long idIntegrante,
            @RequestParam(required = false) String estado
    ) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT fn_total_consultas_integrante(?, ?)",
                Integer.class,
                idIntegrante,
                estado
        );

        return ResponseEntity.ok(Map.of(
                "idIntegrante", idIntegrante,
                "estado", estado != null ? estado : "TODAS",
                "totalConsultas", total
        ));
    }

    @Operation(
            summary = "Marcar medicamentos caducados en stock",
            description = "Llama al procedimiento almacenado sp_marcar_stock_caducado en PostgreSQL para actualizar a CADUCADO los lotes vencidos disponibles de la familia y retorna cuántos registros cambiaron."
    )
    // 3. PROCEDURE: Ejecuta actualización en BD y devuelve total actualizados (sp_marcar_stock_caducado)
    @PostMapping("/familias/{idFamilia}/stock/marcar-caducados")
    public ResponseEntity marcarStockCaducado(@PathVariable Long idFamilia) {
        Integer actualizados = jdbcTemplate.execute(
                "CALL sp_marcar_stock_caducado(?, ?)",
                (CallableStatement cs) -> {
                    cs.setLong(1, idFamilia);
                    cs.setInt(2, 0);
                    cs.registerOutParameter(2, Types.INTEGER);
                    cs.execute();
                    return cs.getInt(2);
                }
        );

        return ResponseEntity.ok(Map.of(
                "idFamilia", idFamilia,
                "registrosActualizados", actualizados
        ));
    }

    @Operation(
            summary = "Actualizar estado y diagnóstico de una consulta",
            description = "Ejecuta el procedimiento almacenado sp_actualizar_estado_consulta en PostgreSQL para modificar el estado de la consulta y, opcionalmente, actualizar su diagnóstico."
    )
    // 4. PROCEDURE: Actualiza estado/diagnóstico de una consulta (sp_actualizar_estado_consulta)
    @PatchMapping("/consultas/{idConsulta}/estado")
    public ResponseEntity actualizarEstadoConsulta(
            @PathVariable Long idConsulta,
            @RequestParam String estado,
            @RequestParam(required = false) String diagnostico
    ) {
        jdbcTemplate.update(
                "CALL sp_actualizar_estado_consulta(?, ?, ?)",
                idConsulta,
                estado,
                diagnostico
        );

        return ResponseEntity.ok(Map.of(
                "idConsulta", idConsulta,
                "nuevoEstado", estado,
                "mensaje", "Procedimiento ejecutado correctamente"
        ));
    }

    @Operation(
            summary = "Actualizar cantidad en stock para probar trigger",
            description = "Actualiza la cantidad de un registro en stock_medicamentos para disparar automáticamente los triggers configurados en PostgreSQL (como cambio a AGOTADO o alerta de stock bajo) y devuelve la fila resultante."
    )
    // 5. TRIGGER: Endpoint rápido para disparar el trigger de stock sin auth
    // Al poner cantidad = 0 o <= cantidad_minima, el trigger actúa solo en PostgreSQL
    @PatchMapping("/stock/{idStock}/cantidad")
    public ResponseEntity actualizarCantidadParaTrigger(
            @PathVariable Long idStock,
            @RequestParam Integer cantidad
    ) {
        jdbcTemplate.update(
                "UPDATE stock_medicamentos SET cantidad = ? WHERE id_stock = ?",
                cantidad,
                idStock
        );

        Map<String, Object> filaActualizada = jdbcTemplate.queryForMap(
                "SELECT id_stock, cantidad, cantidad_minima, estado FROM stock_medicamentos WHERE id_stock = ?",
                idStock
        );

        return ResponseEntity.ok(filaActualizada);
    }
}
