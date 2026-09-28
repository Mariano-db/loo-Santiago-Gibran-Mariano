package astore.inventario;

import java.time.LocalDateTime;

import astore.catalogo.VarianteProducto;

public class Reserva {

    private Long id;
    private VarianteProducto varianteProducto;
    private int cantidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaLimite;
    private EstadoReserva estado;

    public Reserva() {
    }

    public boolean estaVigente() {
        return estado == EstadoReserva.VIGENTE;
    }

    public void expirar() {
        this.estado = EstadoReserva.EXPIRADA;
    }

    public void confirmarRecogida() {
        this.estado = EstadoReserva.CONFIRMADA;
    }

    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VarianteProducto getVarianteProducto() {
        return varianteProducto;
    }

    public void setVarianteProducto(VarianteProducto varianteProducto) {
        this.varianteProducto = varianteProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
}
