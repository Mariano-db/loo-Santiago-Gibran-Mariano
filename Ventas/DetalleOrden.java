public class DetalleOrden {

    private Producto producto;
    private int cantidad;
    private double precioUnitarioVenta;
    private double descuentoLinea;

    public DetalleOrden() {
    }

    public double subtotal() {
        return 0;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return 0;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitarioVenta() {
        return 0;
    }

    public void setPrecioUnitarioVenta(double precioUnitarioVenta) {
        this.precioUnitarioVenta = precioUnitarioVenta;
    }

    public double getDescuentoLinea() {
        return descuentoLinea;
    }

    public void setDescuentoLinea(double descuentoLinea) {
        this.descuentoLinea = descuentoLinea;
    }
}
