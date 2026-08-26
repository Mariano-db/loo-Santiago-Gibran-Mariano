package com.astore.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class logicacarro {

    private int id;
    private LocalDateTime fechaCreacion;
    private Estadocarro estado;
    private Cliente cliente;

    private List<Detallescarro> detalles;

    public logicacarro(int id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = Estadocarro.ACTIVO;
        this.detalles = new ArrayList<>();
    }

    public void agregarProducto(Producto producto, int cantidad) {

        validarProducto(producto);
        validarCantidad(cantidad);

        if (cantidad > producto.getStock()) {
            throw new IllegalArgumentException(
                    "No hay suficiente stock disponible."
            );
        }

        Detallescarro detalleExistente = buscarDetalle(producto.getId());

        if (detalleExistente != null) {

            int nuevaCantidad =
                    detalleExistente.getCantidad() + cantidad;

            if (nuevaCantidad > producto.getStock()) {
                throw new IllegalArgumentException(
                        "La cantidad supera el stock disponible."
                );
            }

            detalleExistente.setCantidad(nuevaCantidad);

        } else {

            Detallescarro nuevoDetalle =
                    new Detallescarro(
                            producto,
                            cantidad,
                            producto.getPrecio()
                    );

            detalles.add(nuevoDetalle);
        }
    }

    public void actualizarCantidad(
            int productoId,
            int nuevaCantidad) {

        validarCantidad(nuevaCantidad);

        Detallescarro detalle =
                buscarDetalle(productoId);

        if (detalle == null) {
            throw new IllegalArgumentException(
                    "El producto no se encuentra en el carrito."
            );
        }

        if (nuevaCantidad > detalle.getProducto().getStock()) {
            throw new IllegalArgumentException(
                    "No hay suficiente stock."
            );
        }

        detalle.setCantidad(nuevaCantidad);
    }

    public void eliminarProducto(int productoId) {

        Detallescarro detalle =
                buscarDetalle(productoId);

        if (detalle != null) {
            detalles.remove(detalle);
        }
    }

    public BigDecimal calcularTotal() {

        BigDecimal total = BigDecimal.ZERO;

        for (Detallescarro detalle : detalles) {

            total = total.add(
                    detalle.calcularSubtotal()
            );
        }

        return total;
    }

    public void vaciar() {
        detalles.clear();
    }

    public Pedido convertirAPedido() {

        if (detalles.isEmpty()) {
            throw new IllegalStateException(
                    "No se puede crear un pedido con el carrito vacío."
            );
        }

        Pedido pedido = new Pedido();

        pedido.setCliente(cliente);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setTotal(calcularTotal());

        for (Detallescarro detalle : detalles) {

            DetallePedido detallePedido =
                    new DetallePedido(
                            detalle.getProducto(),
                            detalle.getCantidad(),
                            detalle.getPrecioUnitario()
                    );

            pedido.agregarDetalle(detallePedido);
        }

        estado = Estadocarro.CONVERTIDO;

        return pedido;
    }

    private Detallescarro buscarDetalle(int productoId) {

        for (Detallescarro detalle : detalles) {

            if (detalle.getProducto().getId() == productoId) {
                return detalle;
            }
        }

        return null;
    }

    private void validarProducto(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto no puede ser null."
            );
        }
    }

    private void validarCantidad(int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Estadocarro getEstado() {
        return estado;
    }

    public void setEstado(Estadocarro estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Detallescarro> getDetalles() {
        return detalles;
    }
}