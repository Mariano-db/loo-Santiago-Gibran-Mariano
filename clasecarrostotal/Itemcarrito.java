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
