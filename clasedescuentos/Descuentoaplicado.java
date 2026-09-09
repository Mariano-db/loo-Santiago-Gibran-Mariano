public class Descuentoaplicado {

    private Long id;
    private double monto;
    private String descripcion;
    private Regladescuento regla;

    public Descuentoaplicado() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Regladescuento getRegla() {
        return regla;
    }

    public void setRegla(Regladescuento regla) {
        this.regla = regla;
    }
}
