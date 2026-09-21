public class Pagoefectivo extends Pago implements Estrategiapago {

    private double montoRecibido;
    private double cambio;

    public Pagoefectivo() {
    }

    @Override
    public boolean procesar(double monto) {
        this.cambio = montoRecibido - monto;
        return montoRecibido >= monto;
    }

    @Override
    public String tipo() {
        return "EFECTIVO";
    }

    public double getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(double montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public double getCambio() {
        return cambio;
    }

    public void setCambio(double cambio) {
        this.cambio = cambio;
    }
}
