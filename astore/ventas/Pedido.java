package astore.ventas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import astore.seguridad.Cliente;

public class Pedido {

    private Long id;
    private Cliente cliente;
    private LocalDateTime fecha;
    private EstadoPedido estado;
    private double total;
    private List<DetallePedido> detalles = new ArrayList<>();

    public Pedido() {
    }

    public double calcularTotal() {
        return 0;
    }

    public void cancelar() {
        this.estado = EstadoPedido.CANCELADO;
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

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }
}
