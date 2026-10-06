package Servicios;

import Ventas.DetallePedido;
import Ventas.Devolucion;
import Ventas.EstadoPedido;
import Ventas.Pedido;

import java.time.LocalDateTime;
import java.util.Objects;

public class ServicioDevoluciones {

    private final RepositorioDevolucionJson repositorio;

    public ServicioDevoluciones(RepositorioDevolucionJson repositorio) {
        this.repositorio = repositorio;
    }

    public int cantidadYaDevuelta(Pedido pedido, DetallePedido detalle) {
        int total = 0;
        for (Devolucion d : repositorio.listarPorPedido(pedido.getId())) {
            if (mismaLinea(d, detalle)) {
                total += d.getCantidad();
            }
        }
        return total;
    }

    public int cantidadDevolvible(Pedido pedido, DetallePedido detalle) {
        return detalle.getCantidad() - cantidadYaDevuelta(pedido, detalle);
    }

    public Devolucion registrar(Pedido pedido, DetallePedido detalle, int cantidad, String motivo) {
        if (pedido.getEstado() == EstadoPedido.PENDIENTE || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException("Solo se pueden devolver productos de pedidos pagados.");
        }
        if (cantidad < 1 || cantidad > cantidadDevolvible(pedido, detalle)) {
            throw new IllegalArgumentException("Cantidad invalida: se pueden devolver como maximo "
                    + cantidadDevolvible(pedido, detalle) + ".");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio.");
        }
        double sumaLineas = pedido.calcularTotal();
        double factor = sumaLineas > 0 ? pedido.getTotal() / sumaLineas : 1;

        Devolucion d = new Devolucion();
        d.setPedidoOrigen(pedido);
        d.setProducto(detalle.getProducto());
        d.setVariante(detalle.getVariante());
        d.setCantidad(cantidad);
        d.setMotivo(motivo.trim());
        d.setFecha(LocalDateTime.now());
        d.setMontoReembolsado(Math.round(detalle.getPrecioUnitario() * cantidad * factor * 100.0) / 100.0);
        repositorio.guardar(d);
        return d;
    }

    private boolean mismaLinea(Devolucion d, DetallePedido detalle) {
        Long varianteDevolucion = d.getVariante() == null ? null : d.getVariante().getId();
        Long varianteDetalle = detalle.getVariante() == null ? null : detalle.getVariante().getId();
        return Objects.equals(d.getProducto().getId(), detalle.getProducto().getId())
                && Objects.equals(varianteDevolucion, varianteDetalle);
    }
}
