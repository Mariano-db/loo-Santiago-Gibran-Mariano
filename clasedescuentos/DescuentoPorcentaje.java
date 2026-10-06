package clasedescuentos;

public class DescuentoPorcentaje implements EstrategiaDescuento {

    private double porcentaje;

    public DescuentoPorcentaje() {
    }

    @Override
    public double calcularDescuento(double montoBase) {
        return montoBase * Math.min(Math.max(porcentaje, 0), 100) / 100;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }
}
