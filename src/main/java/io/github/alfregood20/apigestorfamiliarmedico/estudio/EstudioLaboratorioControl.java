package io.github.alfregood20.apigestorfamiliarmedico.estudio;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.dto.EstudioLaboratorioRequest;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.dto.EstudioLaboratorioResponse;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.dto.EstudioLaboratorioUpdate;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.dto.EstudioResultadoRequest;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.CategoriaEstudioEnum;
import io.github.alfregood20.apigestorfamiliarmedico.estudio.enums.EstadoEstudioEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/estudios-laboratorio")
@Tag(
        name = "Estudios de Laboratorio",
        description = "Endpoints para solicitar, consultar, actualizar, cargar resultados y eliminar estudios clínicos de los integrantes de la familia."
)
public class EstudioLaboratorioControl {

    private final EstudioLaboratorioServ estudioLaboratorioServ;

    @Operation(
            summary = "Registrar o solicitar estudio de laboratorio",
            description = "Crea un nuevo registro de estudio clínico para un integrante de la familia, vinculándolo opcionalmente a una consulta médica e inicializando su estado como PENDIENTE o REALIZADO según si incluye resultados."
    )
    @PostMapping
    public ResponseEntity<EstudioLaboratorioResponse> crearNuevo(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @Valid @RequestBody EstudioLaboratorioRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(estudioLaboratorioServ.crearNuevo(usuario, request));
    }

    @Operation(
            summary = "Listar estudios de laboratorio con filtros",
            description = "Obtiene el historial paginado de estudios clínicos de la familia, permitiendo filtrar por integrante, categoría, estado, estudios pendientes o búsqueda por nombre del estudio y laboratorio."
    )
    @GetMapping
    public ResponseEntity<Page<EstudioLaboratorioResponse>> obtenerPorFiltros(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @RequestParam(required = false) Long idIntegrante,
            @RequestParam(required = false) CategoriaEstudioEnum categoria,
            @RequestParam(required = false) EstadoEstudioEnum estado,
            @RequestParam(required = false, defaultValue = "false") boolean pendientes,
            @RequestParam(required = false, defaultValue = "") String busqueda,
            @ParameterObject @PageableDefault(size = 15, sort = "fechaEstudio", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(estudioLaboratorioServ.obtenerPorFiltros(
                usuario, idIntegrante, categoria, estado, pendientes, busqueda, pageable
        ));
    }

    @Operation(
            summary = "Obtener detalle de un estudio de laboratorio",
            description = "Devuelve la ficha completa de un estudio específico (incluyendo resultado, valores de referencia, observaciones, archivo adjunto, integrante y consulta asociada) validando que pertenezca a la familia."
    )
    @GetMapping("/{idEstudio}")
    public ResponseEntity<EstudioLaboratorioResponse> obtenerPorId(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idEstudio
    ) {
        return ResponseEntity.ok(estudioLaboratorioServ.obtenerPorId(usuario, idEstudio));
    }

    @Operation(
            summary = "Actualizar estudio de laboratorio",
            description = "Modifica los datos generales de un estudio clínico registrado o actualiza sus resultados, valores de referencia, observaciones, costo y estado."
    )
    @PutMapping("/{idEstudio}")
    public ResponseEntity<EstudioLaboratorioResponse> actualizar(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idEstudio,
            @Valid @RequestBody EstudioLaboratorioUpdate request
    ) {
        return ResponseEntity.ok(estudioLaboratorioServ.actualizar(usuario, idEstudio, request));
    }

    @Operation(
            summary = "Cargar resultado de estudio de laboratorio",
            description = "Registra los hallazgos, valores de referencia, observaciones y enlace al archivo de un estudio pendiente, cambiando automáticamente su estado a REALIZADO."
    )
    @PatchMapping("/{idEstudio}/resultado")
    public ResponseEntity<EstudioLaboratorioResponse> cargarResultado(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idEstudio,
            @Valid @RequestBody EstudioResultadoRequest request
    ) {
        return ResponseEntity.ok(estudioLaboratorioServ.cargarResultado(usuario, idEstudio, request));
    }

    @Operation(
            summary = "Eliminar estudio de laboratorio",
            description = "Elimina físicamente un estudio de laboratorio registrado por error, validando que pertenezca a un integrante de la familia del usuario autenticado."
    )
    @DeleteMapping("/{idEstudio}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idEstudio
    ) {
        estudioLaboratorioServ.eliminar(usuario, idEstudio);
        return ResponseEntity.noContent().build();
    }
}