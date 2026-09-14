public class Pagopaypal extends Pago {

    private String cuentaPayPal;

    public Pagopaypal() {
    }

    public boolean procesar(double monto) {
        return false;
    }

    public String getCuentapaypal() {
        return cuentaPayPal;
    }

    public void setCuentapaypal(String cuentaPayPal) {
        this.cuentaPayPal = cuentaPayPal;
    }
}
