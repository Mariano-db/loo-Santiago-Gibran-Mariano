package astore.descuentos;

public abstract class Regladescuento {

    private Long id;
    private String descripcion;

    public Regladescuento() {
    }

    public boolean cumple() {
        return false;
    }

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
}
