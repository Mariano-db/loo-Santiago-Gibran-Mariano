package Ventas;

import Catalogo.Producto;
import Catalogo.VarianteProducto;

import java.time.LocalDateTime;

public class Devolucion {

    private Long id;
    private Pedido pedidoOrigen;
    private Producto producto;
    private VarianteProducto variante;
    private int cantidad;
    private String motivo;
    private LocalDateTime fecha;
    private double montoReembolsado;

    public Devolucion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedidoOrigen() {
        return pedidoOrigen;
    }

    public void setPedidoOrigen(Pedido pedidoOrigen) {
        this.pedidoOrigen = pedidoOrigen;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public VarianteProducto getVariante() {
        return variante;
    }

    public void setVariante(VarianteProducto variante) {
        this.variante = variante;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getMontoReembolsado() {
        return montoReembolsado;
    }

    public void setMontoReembolsado(double montoReembolsado) {
        this.montoReembolsado = montoReembolsado;
    }
}
