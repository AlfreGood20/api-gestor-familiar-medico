package io.github.alfregood20.apigestorfamiliarmedico.stock;

import io.github.alfregood20.apigestorfamiliarmedico.configuration.CustomUserDetails;
import io.github.alfregood20.apigestorfamiliarmedico.exception.RecursoNoEncontradoException;
import io.github.alfregood20.apigestorfamiliarmedico.exception.ReglaNegocioException;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.Medicamento;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.MedicamentoRepo;
import io.github.alfregood20.apigestorfamiliarmedico.medicamento.dto.MedicamentoResponse;
import io.github.alfregood20.apigestorfamiliarmedico.stock.dto.*;
import io.github.alfregood20.apigestorfamiliarmedico.stock.enums.EstadoStockEnum;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.Usuario;
import io.github.alfregood20.apigestorfamiliarmedico.usuario.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class StockMedicamentoServic {

    private final StockMedicamentoRepo stockMedicamentoRepo;
    private final MedicamentoRepo medicamentoRepo;
    private final UsuarioRepo usuarioRepo;

    @Transactional
    public StockResponse crear(CustomUserDetails usuario, StockRequest request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Medicamento medicamento = buscarMedicamentoPorId(request.idMedicamento());

        StockMedicamento stock = toEntity(request);

        stock.setFamilia(autenticado.getFamilia());
        stock.setMedicamento(medicamento);

        stockMedicamentoRepo.save(stock);
        return toResponse(stock);
    }

    @Transactional(readOnly = true)
    public StockDatalleResponse obtenerPorId(CustomUserDetails usuario, Long idStock){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        StockMedicamento stock = buscarStockPorId(idStock, autenticado);

        return toDetalleResponse(stock);
    }

    @Transactional(readOnly = true)
    public Page<StockDatalleResponse> obtenerPorFiltros(
            CustomUserDetails usuario, String nombre, boolean bajoMinimo, EstadoStockEnum estado, Pageable pageable
    ){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        Page<StockMedicamento> pagina = stockMedicamentoRepo.obtenerPorFiltros(
                autenticado.getFamilia(),
                nombre,
                bajoMinimo,
                estado,
                pageable
        );

        Page<StockDatalleResponse> paginaResponse = pagina.map(this::toDetalleResponse);
        return paginaResponse;
    }

    @Transactional(readOnly = true)
    public void eliminarPorId(CustomUserDetails usuario, Long idStock){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        StockMedicamento stock = buscarStockPorId(idStock, autenticado);

        stockMedicamentoRepo.delete(stock);
    }

    @Transactional
    public StockResponse actualizarDatoStock(CustomUserDetails usuario, Long idStock, StockUpdateRequest request){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        StockMedicamento stock = buscarStockPorId(idStock, autenticado);

        stock.setCantidad(request.cantidad());
        stock.setCantidadMinima(request.cantidadMinima());
        stock.setUnidad(request.unidad());
        stock.setFechaCaducidad(request.fechaCaducidad());
        stock.setEstado(
                request.cantidad() <= 0 ? EstadoStockEnum.AGOTADO: (
                        request.fechaCaducidad().isBefore(LocalDate.now()) ? EstadoStockEnum.CADUCADO : EstadoStockEnum.DISPONIBLE
                )
        );

        return toResponse(stockMedicamentoRepo.save(stock));
    }

    @Transactional
    public StockResponse actualizarDatosParcial(
            CustomUserDetails usuario, Long idStock, StockUpdateParcialRequest request
    ){
        Usuario autenticado = buscarUsuarioPorId(usuario.getId());
        StockMedicamento stock = buscarStockPorId(idStock, autenticado);

        if (request.cantidad() != null) {
            stock.setCantidad(request.cantidad());
        }

        if(request.estado() != null){

            if(stock.getEstado() == EstadoStockEnum.AGOTADO && request.estado() == EstadoStockEnum.DISPONIBLE){
                throw new ReglaNegocioException("no se puede cambiar a "+request.estado()+
                        " porque la cantidad actual es 0. Debes reabastecer o aumentar la cantidad primero.");
            }

            stock.setEstado(request.estado());

            if (request.estado() == EstadoStockEnum.AGOTADO || request.estado() == EstadoStockEnum.DESECHADO) {
                stock.setCantidad(0);
            }
        }

        if(request.cantidad() != null){
            stock.setCantidad(request.cantidad());
            stock.setEstado(
                    request.cantidad() <= 0 ? EstadoStockEnum.AGOTADO: EstadoStockEnum.DISPONIBLE
            );
        }

        else if (request.cantidad() != null) {
            if (stock.getCantidad() <= 0) {
                stock.setEstado(EstadoStockEnum.AGOTADO);
            } else if (stock.getFechaCaducidad().isBefore(LocalDate.now())) {
                stock.setEstado(EstadoStockEnum.CADUCADO);
            } else {
                stock.setEstado(EstadoStockEnum.DISPONIBLE);
            }
        }

        return toResponse(stock);
    }


    // FUNCIONES REUTILIZABLES
    private Usuario buscarUsuarioPorId(Long id){
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID:"+id+" no encontrado"));
    }

    private Medicamento buscarMedicamentoPorId(Long id){
        return medicamentoRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medicamento con ID:"+id+" no encontrado"));
    }

    private StockMedicamento buscarStockPorId(Long id, Usuario autenticado){
        return stockMedicamentoRepo.findByIdAndFamilia(id, autenticado.getFamilia())
                .orElseThrow(() -> new RecursoNoEncontradoException("Stock con ID:"+id+" no encontrado"));
    }

    private StockMedicamento toEntity(StockRequest request){
        return StockMedicamento.builder()
                .cantidad(request.cantidad())
                .cantidadMinima(request.cantidadMinima())
                .unidad(request.unidad())
                .fechaCaducidad(request.fechaCaducidad())
                .build();
    }

    private StockResponse toResponse(StockMedicamento response){
        return new StockResponse(
                response.getId(),
                toMedicamentoResponse(response.getMedicamento()),
                response.getCantidad(),
                response.getCantidadMinima(),
                response.getUnidad(),
                response.getEstado(),
                response.getFechaCaducidad(),
                response.getFechaRegistro()
        );
    }

    private StockDatalleResponse toDetalleResponse(StockMedicamento response){
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), response.getFechaCaducidad());
        long diasParaRespuesta = diasRestantes <= 0 ? 0 : diasRestantes;

        return new StockDatalleResponse(
          response.getId(),
          toMedicamentoResponse(response.getMedicamento()),
          response.getCantidad(),
          response.getCantidadMinima(),
          response.getUnidad(),
          response.getEstado(),
          response.getFechaCaducidad(),
          diasParaRespuesta,
          response.getFechaRegistro()
        );
    }

    private MedicamentoResponse toMedicamentoResponse(Medicamento response){
        return new MedicamentoResponse(
                response.getId(),
                response.getNombreComercial(),
                response.getSustanciaActiva(),
                response.getConcentracion(),
                response.getFormaFarmaceutica(),
                response.getViaAdministracion()
        );
    }

}
