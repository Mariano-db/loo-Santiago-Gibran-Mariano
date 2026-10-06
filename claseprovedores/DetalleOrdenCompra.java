package claseprovedores;

import Catalogo.Producto;
import Catalogo.VarianteProducto;

public class DetalleOrdenCompra {

    private Producto producto;
    private VarianteProducto variante;
    private int cantidad;
    private double costoUnitario;

    public DetalleOrdenCompra() {
    }

    public double calcularSubtotal() {
        return costoUnitario * cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public VarianteProducto getVariante() {
        return variante;
    }

    public void setVariante(VarianteProducto variante) {
        this.variante = variante;
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
        return calcularSubtotal();
    }
}
