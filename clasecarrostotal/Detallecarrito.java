public class Detallecarrito {

    private Long id;
    private Producto producto;
    private int cantidad;
    private double precioUnitario;

    public Detallecarrito() {
    }

    public double subtotal() {
        return precioUnitario * cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
