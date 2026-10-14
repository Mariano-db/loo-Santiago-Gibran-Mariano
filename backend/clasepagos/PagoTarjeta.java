package clasepagos;

import java.time.YearMonth;
import java.util.UUID;

public class PagoTarjeta extends Pago implements EstrategiaPago {

    private String numeroTarjetaEnmascarado;
    private TipoTarjeta tipoTarjeta;
    private String codigoAutorizacion;

    private String numeroTarjeta;
    private int mesVencimiento;
    private int anioVencimiento;
    private String cvv;

    public PagoTarjeta() {
    }

    public void setDatosTarjeta(String numeroTarjeta, int mesVencimiento, int anioVencimiento, String cvv) {
        this.numeroTarjeta = numeroTarjeta == null ? "" : numeroTarjeta.replaceAll("[\\s-]", "");
        this.mesVencimiento = mesVencimiento;
        this.anioVencimiento = anioVencimiento;
        this.cvv = cvv == null ? "" : cvv.trim();
    }

    @Override
    public boolean procesar(double monto) {
        boolean valida = monto > 0 && numeroValido() && vigente() && cvvValido();
        if (valida) {
            numeroTarjetaEnmascarado = "**** **** **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4);
            codigoAutorizacion = "AUT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        numeroTarjeta = null;
        cvv = null;
        return valida;
    }

    @Override
    public String tipo() {
        return "TARJETA";
    }

    private boolean numeroValido() {
        if (numeroTarjeta == null || !numeroTarjeta.matches("\\d{13,19}")) {
            return false;
        }
        int suma = 0;
        boolean duplicar = false;
        for (int i = numeroTarjeta.length() - 1; i >= 0; i--) {
            int digito = numeroTarjeta.charAt(i) - '0';
            if (duplicar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
            duplicar = !duplicar;
        }
        return suma % 10 == 0;
    }

    private boolean vigente() {
        if (mesVencimiento < 1 || mesVencimiento > 12) {
            return false;
        }
        int anio = anioVencimiento < 100 ? 2000 + anioVencimiento : anioVencimiento;
        return !YearMonth.of(anio, mesVencimiento).isBefore(YearMonth.now());
    }

    private boolean cvvValido() {
        return cvv != null && cvv.matches("\\d{3,4}");
    }

    public String getNumeroTarjetaEnmascarado() {
        return numeroTarjetaEnmascarado;
    }

    public void setNumeroTarjetaEnmascarado(String numeroTarjetaEnmascarado) {
        this.numeroTarjetaEnmascarado = numeroTarjetaEnmascarado;
    }

    public TipoTarjeta getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(TipoTarjeta tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getCodigoAutorizacion() {
        return codigoAutorizacion;
    }

    public void setCodigoAutorizacion(String codigoAutorizacion) {
        this.codigoAutorizacion = codigoAutorizacion;
    }
}
