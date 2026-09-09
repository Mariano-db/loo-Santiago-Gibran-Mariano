
public class Carritocompra {
    private Long id;
    private Cliente cliente;
    private List<ItemCarrito> items = new ArrayList<>();
    private List<DetalleCarrito> detalles = new ArrayList<>();
    private LocalDateTime fechaCreacion;
    private EstadoCarrito estado;
    private EstrategiaDescuento estrategiaDescuento;

    public Carritocompra() {
    }

    public void agregarProducto(Producto producto, int cantidad) {
    }

    public void actualizarCantidad(int indice, int cantidad) {
    }

    public void eliminarProducto(int indice) {
    }

    public double totalConDescuento() {
        return 0;
    }

    public double calcularTotal() {
        return 0;
    }

    public void validarNoVacio() {
    }

    public Pedido convertirAPedido() {
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrito> items) {
        this.items = items;
    }

    public List<DetalleCarrito> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCarrito> detalles) {
        this.detalles = detalles;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public EstadoCarrito getEstado() {
        return estado;
    }

    public void setEstado(EstadoCarrito estado) {
        this.estado = estado;
    }

    public EstrategiaDescuento getEstrategiaDescuento() {
        return estrategiaDescuento;
    }

    public void setEstrategiaDescuento(EstrategiaDescuento estrategiaDescuento) {
        this.estrategiaDescuento = estrategiaDescuento;
    }
}