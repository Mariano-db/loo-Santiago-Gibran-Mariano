package Servicios;

import Catalogo.Producto;
import Exepciones.CarritoVacioException;
import Exepciones.PagoInvalidoException;
import Exepciones.StockInsuficienteException;
import Ventas.EstadoPedido;
import Ventas.Pedido;
import clasecarrostotal.CarritoCompra;
import clasecarrostotal.ItemCarrito;
import clasepagos.EstrategiaPago;
import clasepagos.PagoPaypal;
import clasepagos.PagoTarjeta;
import clasepagos.PagoTransferencia;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServicioPedidos {

    private final RepositorioProductoJson repositorioProductos;
    private final RepositorioPedidoJson repositorioPedidos;
    private final IServiciosPagos servicioPagos;

    public ServicioPedidos(RepositorioProductoJson repositorioProductos,
                           RepositorioPedidoJson repositorioPedidos,
                           IServiciosPagos servicioPagos) {
        this.repositorioProductos = repositorioProductos;
        this.repositorioPedidos = repositorioPedidos;
        this.servicioPagos = servicioPagos;
    }

    /**
     * Valida stock y cupon, cobra, descuenta el inventario y guarda el pedido.
     * Si algo falla antes del cobro, no cambia nada.
     */
    public Pedido finalizarCompra(CarritoCompra carrito, EstrategiaPago estrategiaPago)
            throws CarritoVacioException, StockInsuficienteException, PagoInvalidoException {
        carrito.validarNoVacio();

        if (carrito.getCupon() != null && !carrito.getCupon().esValido()) {
            carrito.quitarCupon();
            throw new IllegalArgumentException("El cupon ya no es valido y se quito del carrito");
        }

        Map<Long, Integer> cantidadPorProducto = cantidadesPorProducto(carrito);
        validarStockActual(cantidadPorProducto);

        servicioPagos.procesarPago(estrategiaPago, carrito.totalConDescuento());

        Pedido pedido = carrito.convertirAPedido();
        pedido.setEstado(EstadoPedido.PAGADO);
        pedido.setMetodoPago(estrategiaPago.tipo());
        pedido.setReferenciaPago(referenciaDePago(estrategiaPago));

        descontarStock(cantidadPorProducto);
        repositorioPedidos.guardar(pedido);
        return pedido;
    }

    public List<Pedido> pedidosDeCliente(long clienteId) {
        return repositorioPedidos.listarPorCliente(clienteId);
    }

    private Map<Long, Integer> cantidadesPorProducto(CarritoCompra carrito) {
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (ItemCarrito item : carrito.getItems()) {
            cantidades.merge(item.getProducto().getId(), item.getCantidad(), Integer::sum);
        }
        return cantidades;
    }

    private void validarStockActual(Map<Long, Integer> cantidadPorProducto) throws StockInsuficienteException {
        for (Map.Entry<Long, Integer> entrada : cantidadPorProducto.entrySet()) {
            Producto actual = repositorioProductos.buscarPorId(entrada.getKey())
                    .orElseThrow(() -> new StockInsuficienteException("Un producto del carrito ya no existe en el catalogo"));
            if (!actual.isActivo()) {
                throw new StockInsuficienteException(actual.getNombre() + " ya no esta disponible");
            }
            if (entrada.getValue() > actual.getStock()) {
                throw new StockInsuficienteException("Solo hay " + actual.getStock() + " unidades de " + actual.getNombre());
            }
        }
    }

    private void descontarStock(Map<Long, Integer> cantidadPorProducto) {
        for (Map.Entry<Long, Integer> entrada : cantidadPorProducto.entrySet()) {
            repositorioProductos.buscarPorId(entrada.getKey()).ifPresent(producto -> {
                producto.actualizarStock(producto.getStock() - entrada.getValue());
                repositorioProductos.guardar(producto);
            });
        }
    }

    private String referenciaDePago(EstrategiaPago estrategia) {
        if (estrategia instanceof PagoTarjeta) {
            PagoTarjeta tarjeta = (PagoTarjeta) estrategia;
            return tarjeta.getCodigoAutorizacion() + " (" + tarjeta.getNumeroTarjetaEnmascarado() + ")";
        }
        if (estrategia instanceof PagoTransferencia) {
            return ((PagoTransferencia) estrategia).getReferenciaBancaria();
        }
        if (estrategia instanceof PagoPaypal) {
            return ((PagoPaypal) estrategia).getCuentaPaypal();
        }
        return null;
    }
}
