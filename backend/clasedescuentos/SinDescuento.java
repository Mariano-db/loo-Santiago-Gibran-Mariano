package clasedescuentos;

public class SinDescuento implements EstrategiaDescuento {

    public SinDescuento() {
    }

    @Override
    public double calcularDescuento(double montoBase) {
        return 0;
    }
}
