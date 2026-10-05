package claseprovedores;

import Catalogo.VarianteProducto;

public class DetalleOrdenCompra {

    private VarianteProducto varianteProducto;
    private int cantidad;
    private double costoUnitario;
    private double subtotal;

    public DetalleOrdenCompra() {
    }

    public double calcularSubtotal() {
        return costoUnitario * cantidad;
    }

    public VarianteProducto getVarianteProducto() {
        return varianteProducto;
    }

    public void setVarianteProducto(VarianteProducto varianteProducto) {
        this.varianteProducto = varianteProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
