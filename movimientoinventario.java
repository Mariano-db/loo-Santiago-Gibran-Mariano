import java.time.LocalDateTime;

public class movimientoinventario {

    private Long id;
    private tipomovimiento tipo;
    private int cantidad;
    private LocalDateTime fecha;
    private String motivo;
    private String referencia;

    public movimientoinventario() {
    }

    public void registrar() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public tipomovimiento getTipo() {
        return tipo;
    }

    public void setTipo(tipomovimiento tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
