package clasepagos;

public class PagoEfectivo extends Pago {

    private double montoRecibido;
    private double cambio;

    public PagoEfectivo() {
    }

    @Override
    public boolean procesar(double monto) {
        if (montoRecibido < monto) {
            setMotivoRechazo(String.format("El monto recibido ($%.2f) no cubre el total ($%.2f).", montoRecibido, monto));
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
