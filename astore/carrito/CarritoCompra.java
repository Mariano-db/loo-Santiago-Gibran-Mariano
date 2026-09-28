package astore.carrito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import astore.catalogo.Producto;
import astore.descuentos.Estrategiadescuento;
import astore.seguridad.Cliente;
import astore.ventas.Pedido;

public class CarritoCompra {
    private Long id;
    private Cliente cliente;
    private List<Itemcarrito> items = new ArrayList<>();
    private List<Detallecarrito> detalles = new ArrayList<>();
    private LocalDateTime fechaCreacion;
    private Estadocarrito estado;
    private Estrategiadescuento estrategiaDescuento;

    public CarritoCompra() {
    }

    public void agregarProducto(Producto producto, int cantidad) {
    }

    public void actualizarCantidad(int indice, int cantidad) {
    }

    public void eliminarProducto(int indice) {
    }

    public double totalCondescuento() {
        return 0;
    }

    public double calculartotal() {
        return 0;
    }

    public void validarNovacio() {
    }

    public Pedido convertirApedido() {
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Itemcarrito> getItems() {
        return items;
    }

    public void setItems(List<Itemcarrito> items) {
        this.items = items;
    }

    public List<Detallecarrito> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<Detallecarrito> detalles) {
        this.detalles = detalles;
    }

    public LocalDateTime getFechacreacion() {
        return fechaCreacion;
    }

    public void setFechacreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Estadocarrito getEstado() {
        return estado;
    }

    public void setEstado(Estadocarrito estado) {
        this.estado = estado;
    }

    public Estrategiadescuento getEstrategiadescuento() {
        return estrategiaDescuento;
    }

    public void setEstrategiadescuento(Estrategiadescuento estrategiaDescuento) {
        this.estrategiaDescuento = estrategiaDescuento;
    }
}
