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

    public double totalCondescuento() {
        return 0;
    }

    public double calculartotal() {
        return 0;
    }

    public void validarNovacio() {
    }

    public Pedido convertirApedido() {
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

    public List<Detallecarrito> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<Detallecarrito> detalles) {
        this.detalles = detalles;
    }

    public LocalDateTime getFechacreacion() {
        return fechaCreacion;
    }

    public void setFechacreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Estadocarrito getEstado() {
        return estado;
    }

    public void setEstado(Estadocarrito estado) {
        this.estado = estado;
    }

    public Estrategiadescuento getEstrategiadescuento() {
        return estrategiaDescuento;
    }

    public void setEstrategiadescuento(Estrategiadescuento estrategiaDescuento) {
        this.estrategiaDescuento = estrategiaDescuento;
    }
}