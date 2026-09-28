package astore.ventas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import astore.seguridad.Cliente;

public class Orden {

    private Long id;
    private Cliente cliente;
    private LocalDateTime fecha;
    private EstadoOrden estado;
    private double total;
    private List<DetalleOrden> detalles = new ArrayList<>();

    public Orden() {
    }

    public double calcularTotal() {
        return 0;
    }

    public void anular() {
        this.estado = EstadoOrden.ANULADA;
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<DetalleOrden> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleOrden> detalles) {
        this.detalles = detalles;
    }
}
