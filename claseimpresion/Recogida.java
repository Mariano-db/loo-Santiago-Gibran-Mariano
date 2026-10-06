package claseimpresion;

import Ventas.Pedido;

import java.time.LocalDateTime;

public class Recogida {

    private Long id;
    private Pedido pedido;
    private LocalDateTime fechaLimite;
    private LocalDateTime fechaEntrega;
    private EstadoRecogida estado;

    public Recogida() {
    }

    public void marcarLista() {
        if (estado != EstadoRecogida.PENDIENTE) {
            throw new IllegalStateException("Solo una recogida pendiente puede marcarse como lista.");
        }
        this.estado = EstadoRecogida.LISTA;
    }

    public void registrarEntrega() {
        if (estado != EstadoRecogida.LISTA) {
            throw new IllegalStateException("La recogida debe estar lista antes de entregarse.");
        }
        if (!estaVigente()) {
            throw new IllegalStateException("La recogida esta vencida.");
        }
        this.estado = EstadoRecogida.ENTREGADA;
        this.fechaEntrega = LocalDateTime.now();
    }

    public boolean estaVigente() {
        return fechaLimite != null && LocalDateTime.now().isBefore(fechaLimite);
    }

    public EstadoRecogida estadoActual() {
        if ((estado == EstadoRecogida.PENDIENTE || estado == EstadoRecogida.LISTA) && !estaVigente()) {
            return EstadoRecogida.VENCIDA;
        }
        return estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public EstadoRecogida getEstado() {
        return estado;
    }

    public void setEstado(EstadoRecogida estado) {
        this.estado = estado;
    }
}
