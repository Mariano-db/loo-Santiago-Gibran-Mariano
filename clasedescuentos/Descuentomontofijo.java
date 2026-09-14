public class Descuentomontofijo implements Estrategiadescuento {

    private double monto;

    public Descuentomontofijo() {
    }

    @Override
    public double calculardescuento(double montoBase) {
        return this.monto;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
