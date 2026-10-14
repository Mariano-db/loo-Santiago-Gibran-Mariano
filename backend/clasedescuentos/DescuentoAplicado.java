package clasedescuentos;

public class DescuentoAplicado {

    private Long id;
    private double monto;
    private String descripcion;
    private ReglaDescuento regla;

    public DescuentoAplicado() {
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

    public ReglaDescuento getRegla() {
        return regla;
    }

    public void setRegla(ReglaDescuento regla) {
        this.regla = regla;
    }
}
