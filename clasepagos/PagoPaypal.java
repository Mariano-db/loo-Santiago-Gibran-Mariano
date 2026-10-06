package clasepagos;

import java.util.regex.Pattern;

public class PagoPaypal extends Pago {

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private String cuentaPaypal;

    public PagoPaypal() {
    }

    @Override
    public boolean procesar(double monto) {
        if (cuentaPaypal == null || !FORMATO_EMAIL.matcher(cuentaPaypal).matches()) {
            setMotivoRechazo("La cuenta de PayPal debe ser un correo valido.");
            return false;
        }
        return true;
    }

    @Override
    public String tipo() {
        return "PAYPAL";
    }

    @Override
    public String getReferencia() {
        return cuentaPaypal;
    }

    public String getCuentaPaypal() {
        return cuentaPaypal;
    }

    public void setCuentaPaypal(String cuentaPaypal) {
        this.cuentaPaypal = cuentaPaypal;
    }
}
