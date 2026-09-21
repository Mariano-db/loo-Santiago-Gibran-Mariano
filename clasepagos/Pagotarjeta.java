public class Pagotarjeta extends Pago implements Estrategiapago {

    private String numeroTarjetaEnmascarado;
    private TipoTarjeta tipoTarjeta;
    private String codigoAutorizacion;

    public Pagotarjeta() {
    }

    @Override
    public boolean procesar(double monto) {
        return false;
    }

    @Override
    public String tipo() {
        return "TARJETA";
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
