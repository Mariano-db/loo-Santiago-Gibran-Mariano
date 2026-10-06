package clasedescuentos;

public class DescuentoMontoFijo implements EstrategiaDescuento {

    private double monto;

    public DescuentoMontoFijo() {
    }

    @Override
    public double calcularDescuento(double montoBase) {
        return Math.max(this.monto, 0);
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
