import java.time.LocalDateTime;

public class reserva {

    private Long id;
    private varianteproducto varianteProducto;
    private int cantidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaLimite;
    private estadoreserva estado;

    public reserva() {
    }

    public boolean estaVigente() {
        return estado == estadoreserva.VIGENTE;
    }

    public void expirar() {
        this.estado = estadoreserva.EXPIRADA;
    }

    public void confirmarRecogida() {
        this.estado = estadoreserva.CONFIRMADA;
    }

    public void cancelar() {
        this.estado = estadoreserva.CANCELADA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public varianteproducto getVarianteProducto() {
        return varianteProducto;
    }

    public void setVarianteProducto(varianteproducto varianteProducto) {
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

    public estadoreserva getEstado() {
        return estado;
    }

    public void setEstado(estadoreserva estado) {
        this.estado = estado;
    }
}
