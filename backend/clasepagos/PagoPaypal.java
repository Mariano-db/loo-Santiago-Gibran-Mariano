package clasepagos;

public class PagoPaypal extends Pago implements EstrategiaPago {

    private String cuentaPaypal;

    public PagoPaypal() {
    }

    @Override
    public boolean procesar(double monto) {
        return monto > 0 && cuentaPaypal != null && cuentaPaypal.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    @Override
    public String tipo() {
        return "PAYPAL";
    }

    public String getCuentaPaypal() {
        return cuentaPaypal;
    }

    public void setCuentaPaypal(String cuentaPaypal) {
        this.cuentaPaypal = cuentaPaypal;
    }
}
