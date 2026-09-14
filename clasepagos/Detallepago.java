public class Detallepago {

    private Long id;
    private double monto;
    private Metodopago metodo;
    private String referencia;

    public Detallepago() {
    }

    public boolean validar() {
        return false;
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

    public Metodopago getMetodo() {
        return metodo;
    }

    public void setMetodo(Metodopago metodo) {
        this.metodo = metodo;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
