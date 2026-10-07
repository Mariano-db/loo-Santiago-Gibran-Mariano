package clasepagos;

public class PagoEfectivo extends Pago implements EstrategiaPago {

    private double montoRecibido;
    private double cambio;

    public PagoEfectivo() {
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
