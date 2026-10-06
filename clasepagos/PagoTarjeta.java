package clasepagos;

import java.time.YearMonth;
import java.util.UUID;

public class PagoTarjeta extends Pago {

    private String numeroTarjeta;
    private String vencimiento;
    private String cvv;
    private String numeroTarjetaEnmascarado;
    private TipoTarjeta tipoTarjeta;
    private String codigoAutorizacion;

    public PagoTarjeta() {
    }

    public void setDatosTarjeta(String numeroTarjeta, String vencimiento, String cvv) {
        this.numeroTarjeta = numeroTarjeta == null ? null : numeroTarjeta.replaceAll("[\\s-]", "");
        this.vencimiento = vencimiento == null ? null : vencimiento.trim();
        this.cvv = cvv == null ? null : cvv.trim();
    }

    @Override
    public boolean procesar(double monto) {
        try {
            if (numeroTarjeta == null || !numeroTarjeta.matches("\\d{13,19}") || !pasaLuhn(numeroTarjeta)) {
                setMotivoRechazo("El numero de tarjeta no es valido.");
                return false;
            }
            if (!vencimientoVigente()) {
                setMotivoRechazo("La tarjeta esta vencida o la fecha no tiene el formato MM/AA.");
                return false;
            }
            if (cvv == null || !cvv.matches("\\d{3,4}")) {
                setMotivoRechazo("El CVV debe tener 3 o 4 digitos.");
                return false;
            }
            numeroTarjetaEnmascarado = "**** **** **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4);
            codigoAutorizacion = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            return true;
        } finally {
            numeroTarjeta = null;
            cvv = null;
        }
    }

    private boolean vencimientoVigente() {
        if (vencimiento == null || !vencimiento.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return false;
        }
        int mes = Integer.parseInt(vencimiento.substring(0, 2));
        int anio = 2000 + Integer.parseInt(vencimiento.substring(3));
        return !YearMonth.of(anio, mes).isBefore(YearMonth.now());
    }

    static boolean pasaLuhn(String numero) {
        int suma = 0;
        boolean duplicar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = numero.charAt(i) - '0';
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

    @Override
    public String tipo() {
        return "TARJETA";
    }

    @Override
    public String getReferencia() {
        return codigoAutorizacion;
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
