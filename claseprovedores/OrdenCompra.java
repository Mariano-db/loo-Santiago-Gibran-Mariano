package claseprovedores;

import Catalogo.Producto;
import Catalogo.VarianteProducto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompra {

    private Long id;
    private Proveedor proveedor;
    private LocalDateTime fecha;
    private LocalDateTime fechaRecepcion;
    private EstadoOrdenCompra estado;
    private List<DetalleOrdenCompra> detalles = new ArrayList<>();

    public OrdenCompra() {
        this.estado = EstadoOrdenCompra.BORRADOR;
        this.fecha = LocalDateTime.now();
    }

    public void agregarDetalle(Producto producto, VarianteProducto variante, int cantidad, double costoUnitario) {
        exigirEstado(EstadoOrdenCompra.BORRADOR, "Solo se pueden editar ordenes en borrador.");
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (costoUnitario < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo.");
        }
        DetalleOrdenCompra d = new DetalleOrdenCompra();
        d.setProducto(producto);
        d.setVariante(variante);
        d.setCantidad(cantidad);
        d.setCostoUnitario(costoUnitario);
        detalles.add(d);
    }

    public double calcularTotal() {
        double total = 0;
        for (DetalleOrdenCompra d : detalles) {
            total += d.calcularSubtotal();
        }
        return total;
    }

    public void enviar() {
        exigirEstado(EstadoOrdenCompra.BORRADOR, "Solo se puede enviar una orden en borrador.");
        if (detalles.isEmpty()) {
            throw new IllegalStateException("La orden no tiene productos.");
        }
        this.estado = EstadoOrdenCompra.ENVIADA;
    }

    public void marcarRecibida() {
        exigirEstado(EstadoOrdenCompra.ENVIADA, "Solo se puede recibir una orden enviada.");
        this.estado = EstadoOrdenCompra.RECIBIDA;
        this.fechaRecepcion = LocalDateTime.now();
    }

    public void cancelar() {
        if (estado == EstadoOrdenCompra.RECIBIDA || estado == EstadoOrdenCompra.CANCELADA) {
            throw new IllegalStateException("La orden ya esta " + estado + ".");
        }
        this.estado = EstadoOrdenCompra.CANCELADA;
    }

    private void exigirEstado(EstadoOrdenCompra esperado, String mensaje) {
        if (estado != esperado) {
            throw new IllegalStateException(mensaje);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(LocalDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public EstadoOrdenCompra getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrdenCompra estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return calcularTotal();
    }

    public List<DetalleOrdenCompra> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleOrdenCompra> detalles) {
        this.detalles = detalles;
    }
}
