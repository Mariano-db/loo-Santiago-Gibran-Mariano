public class Itemcarrito {

    private Producto producto;
    private int cantidad;
    private double precioUnitario;
    private double descuentoLinea;

    public Itemcarrito() {
    }

    public double subtotal() {
        return (precioUnitario * cantidad) - descuentoLinea;
    }

    public void incrementarcantidad(int cantidad) {
        this.cantidad += cantidad;
    }

    public void cambiarcantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPreciounitario() {
        return precioUnitario;
    }

    public void setPreciounitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getDescuentolinea() {
        return descuentoLinea;
    }

    public void setDescuentolinea(double descuentoLinea) {
        this.descuentoLinea = descuentoLinea;
    }
}
