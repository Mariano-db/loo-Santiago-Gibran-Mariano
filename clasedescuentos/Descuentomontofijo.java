public class Descuentomontofijo implements EstrategiaDescuento {

    private double monto;

    public Descuentomontofijo() {
    }

    @Override
    public double calcularDescuento(double montoBase) {
        return this.monto;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
