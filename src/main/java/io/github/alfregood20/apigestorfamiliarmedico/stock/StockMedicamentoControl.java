package io.github.alfregood20.apigestorfamiliarmedico.stock;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.stock.dto.*;
import io.github.alfregood20.apigestorfamiliarmedico.stock.enums.EstadoStockEnum;
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
public class StockMedicamentoControl {

    private final StockMedicamentoServic stockMedicamentoServic;

    @PostMapping
    public ResponseEntity<StockResponse> crearNuevo(@AuthenticationPrincipal CustomUserDetails usuario, @RequestBody StockRequest request){
        return new ResponseEntity<StockResponse>(
                stockMedicamentoServic.crear(usuario, request), HttpStatus.CREATED
        );
    }

    @GetMapping("/{idStock}")
    public ResponseEntity<StockDatalleResponse> obtenerDetallePorId(
            @AuthenticationPrincipal CustomUserDetails usuario,@PathVariable Long idStock)
    {
        return ResponseEntity.ok(stockMedicamentoServic.obtenerPorId(usuario, idStock));
    }

    @GetMapping
    public ResponseEntity<Page<StockDatalleResponse>> obtenerListado(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) boolean bajoMinimo,
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

    @DeleteMapping("/{idStock}")
    public ResponseEntity<Void> eliminarPorId(@AuthenticationPrincipal CustomUserDetails usuario,@PathVariable Long idStock){
        stockMedicamentoServic.eliminarPorId(usuario, idStock);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idStock}")
    public ResponseEntity<StockResponse> actualizarDatoStock(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idStock,
            @RequestBody StockUpdateRequest request)
    {
        return ResponseEntity.ok(stockMedicamentoServic.actualizarDatoStock(usuario, idStock, request));
    }

    @PatchMapping("/{idStock}")
    public ResponseEntity<StockResponse> actualizarDatosParciales(
            @AuthenticationPrincipal CustomUserDetails usuario,
            @PathVariable Long idStock,
            @RequestBody StockUpdateParcialRequest request
    ){
        return ResponseEntity.ok(stockMedicamentoServic.actualizarDatosParcial(usuario, idStock, request));
    }
}
