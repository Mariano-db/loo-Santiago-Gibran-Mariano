package claseimpresion;

import java.time.LocalDateTime;

public class Recogida {

    private Long id;
    private LocalDateTime fechaLimite;
    private LocalDateTime fechaEntrega;
    private EstadoRecogida estado;

    public Recogida() {
    }

    public void registrarEntrega() {
        this.estado = EstadoRecogida.ENTREGADA;
    }

    public boolean estaVigente() {
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
