public class DetallePedido {

    private Long id;
    private Producto producto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetallePedido() {
    }

    public double calcularSubtotal() {
        return 0;
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
        return 0;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return 0;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return 0;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
