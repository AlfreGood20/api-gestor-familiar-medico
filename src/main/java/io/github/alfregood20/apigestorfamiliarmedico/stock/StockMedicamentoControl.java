package io.github.alfregood20.apigestorfamiliarmedico.stock;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.stock.dto.*;
import io.github.alfregood20.apigestorfamiliarmedico.stock.enums.EstadoStockEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/stock")
@RequiredArgsConstructor
@Tag(
        name = "Gestion de stock medicamentos",
        description = "Endpoints para la gestion de stock de medicamentos familiar, administraras las diferentes operaciones"
)
public class StockMedicamentoControl {

    private final StockMedicamentoServic stockMedicamentoServic;

    @Operation(
            summary = "Añadir nuevo medicamento al stock",
            description = "**Roles:** ADMIN y CUIDADOR"
    )
    @PostMapping
    public ResponseEntity<StockResponse> crearNuevo(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @RequestBody @Valid StockRequest request
    ){
        return new ResponseEntity<StockResponse>(
                stockMedicamentoServic.crear(usuario, request), HttpStatus.CREATED
        );
    }

    @Operation(
            summary = "Obtener detalles de un medicamento del stock",
            description = "**Roles:** ADMIN y CUIDADOR"
    )
    @GetMapping("/{idStock}")
    public ResponseEntity<StockDatalleResponse> obtenerDetallePorId(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idStock
    )
    {
        return ResponseEntity.ok(stockMedicamentoServic.obtenerPorId(usuario, idStock));
    }

    @Operation(
            summary = "Obtener todo el stock, por diferentes filtros",
            description = "**ROLES:** ADMIN y CUIDADOR"
    )
    @GetMapping
    public ResponseEntity<Page<StockResponse>> obtenerListado(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @Parameter(description = "Texto que empieze el nombre comercial o sustancia activa")
            @RequestParam(required = false, defaultValue = "") String nombre,
            @Parameter(description = "Si es `true`, solo los que tienen cantidad igual o menor al mínimo configurado")
            @RequestParam(required = false, defaultValue = "false") boolean bajoMinimo,
            @Parameter(description = "Filtra por estado. Sin este filtro se excluyen los DESECHADOS")
            @RequestParam(required = false) EstadoStockEnum estado,
            @PageableDefault(size = 10, sort = "fechaCaducidad", direction = Sort.Direction.ASC) @ParameterObject Pageable pageable
            )
    {
        return ResponseEntity.ok(stockMedicamentoServic.obtenerPorFiltros(
                usuario,
                nombre,
                bajoMinimo,
                estado,
                pageable
        ));
    }

    @Operation(
            summary = "Eliminar medicamento del stock por id",
            description = "**ROLES:** ADMIN"
    )
    @DeleteMapping("/{idStock}")
    public ResponseEntity<Void> eliminarPorId(@AuthenticationPrincipal CustomUserDetails usuario,@PathVariable Long idStock){
        stockMedicamentoServic.eliminarPorId(usuario, idStock);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Actualizar los datos de un medicamento del stock",
            description = "**Roles:** ADMIN y CUIDADOR"
    )
    @PutMapping("/{idStock}")
    public ResponseEntity<StockResponse> actualizarDatoStock(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idStock,
            @RequestBody @Valid StockUpdateRequest request)
    {
        return ResponseEntity.ok(stockMedicamentoServic.actualizarDatoStock(usuario, idStock, request));
    }

    @Operation(
            summary = "Actualizar datos parciales, ya sea solo la cantidad o el estado del medicamento del stock",
            description = "**ROLES:** ADMIN Y CUIDADOR"
    )
    @PatchMapping("/{idStock}")
    public ResponseEntity<StockResponse> actualizarDatosParciales(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idStock,
            @RequestBody @Valid StockUpdateParcialRequest request
    ){
        return ResponseEntity.ok(stockMedicamentoServic.actualizarDatosParcial(usuario, idStock, request));
    }
}
