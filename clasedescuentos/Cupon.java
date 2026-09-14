import java.time.LocalDate;

public class Cupon {

    private Long id;
    private String codigo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activo;

    public Cupon() {
    }

    public boolean esValido() {
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getFechainicio() {
        return fechaInicio;
    }

    public void setFechainicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechafin() {
        return fechaFin;
    }

    public void setFechafin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
