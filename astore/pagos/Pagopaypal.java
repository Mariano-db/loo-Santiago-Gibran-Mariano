package astore.pagos;

public class Pagopaypal extends Pago implements Estrategiapago {

    private String cuentaPayPal;

    public Pagopaypal() {
    }

    @Override
    public boolean procesar(double monto) {
        return false;
    }

    @Override
    public String tipo() {
        return "PAYPAL";
    }

    public String getCuentapaypal() {
        return cuentaPayPal;
    }

    public void setCuentapaypal(String cuentaPayPal) {
        this.cuentaPayPal = cuentaPayPal;
    }
}
