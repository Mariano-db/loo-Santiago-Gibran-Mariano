import astore.catalogo.VarianteProducto;

public class Suministro {

    private VarianteProducto varianteProducto;
    private double costo;
    private int tiempoRestockDias;

    public Suministro() {
    }

    public void actualizarCosto() {
    }

    public void actualizarTiempoRestock() {
    }

    public VarianteProducto getVarianteProducto() {
        return varianteProducto;
    }

    public void setVarianteProducto(VarianteProducto varianteProducto) {
        this.varianteProducto = varianteProducto;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public int getTiempoRestockDias() {
        return tiempoRestockDias;
    }

    public void setTiempoRestockDias(int tiempoRestockDias) {
        this.tiempoRestockDias = tiempoRestockDias;
    }
}
