package clasecarrostotal;

import Catalogo.Producto;
import Catalogo.VarianteProducto;

public class ItemCarrito {

    private Producto producto;
    private VarianteProducto variante;
    private int cantidad;
    private double precioUnitario;
    private double descuentoLinea;

    public ItemCarrito() {
    }

    public double subtotal() {
        return (precioUnitario * cantidad) - descuentoLinea;
    }

    public void incrementarCantidad(int cantidad) {
        this.cantidad += cantidad;
    }

    public void cambiarCantidad(int cantidad) {
        this.cantidad = cantidad;
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

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getDescuentoLinea() {
        return descuentoLinea;
    }

    public void setDescuentoLinea(double descuentoLinea) {
        this.descuentoLinea = descuentoLinea;
    }
}
