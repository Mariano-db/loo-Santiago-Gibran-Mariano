public class Devolucion {

    private String id;
    private Orden ordenOrigen;
    private Producto producto;
    private int cantidad;
    private String motivo;
    private LocalDateTime fecha;
    private double montoReembolsado;

    public Devolucion() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Orden getOrdenOrigen() {
        return ordenOrigen;
    }

    public void setOrdenOrigen(Orden ordenOrigen) {
        this.ordenOrigen = ordenOrigen;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getMontoReembolsado() {
        return 0;
    }

    public void setMontoReembolsado(double montoReembolsado) {
        this.montoReembolsado = montoReembolsado;
    }
}
