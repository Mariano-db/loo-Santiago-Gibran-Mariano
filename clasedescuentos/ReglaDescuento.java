package clasedescuentos;

public abstract class ReglaDescuento {

    private Long id;
    private String descripcion;
    private boolean activa;

    public ReglaDescuento() {
    }

    public abstract boolean aplica();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
