package clasepagos;

public class PagoEfectivo extends Pago implements EstrategiaPago {

    private double montoRecibido;
    private double cambio;

    public PagoEfectivo() {
    }

    @Override
    public boolean procesar(double monto) {
        if (monto <= 0 || montoRecibido < monto) {
            this.cambio = 0;
            return false;
        }
        this.cambio = montoRecibido - monto;
        return true;
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
