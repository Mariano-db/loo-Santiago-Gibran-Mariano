package clasecarrostotal;

import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Exepciones.CarritoVacioException;
import Exepciones.StockInsuficienteException;
import claseseguridad.Cliente;
import clasedescuentos.Cupon;
import clasedescuentos.EstrategiaDescuento;
import Ventas.DetallePedido;
import Ventas.EstadoPedido;
import Ventas.Pedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CarritoCompra {

    private Long id;
    private Cliente cliente;
    private List<ItemCarrito> items = new ArrayList<>();
    private List<DetalleCarrito> detalles = new ArrayList<>();
    private LocalDateTime fechaCreacion;
    private EstadoCarrito estado;
    private EstrategiaDescuento estrategiaDescuento;
    private Cupon cupon;

    public CarritoCompra() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoCarrito.ACTIVO;
    }

    public void agregarProducto(Producto producto, int cantidad) throws StockInsuficienteException {
        agregarProducto(producto, null, cantidad);
    }

    public void agregarProducto(Producto producto, VarianteProducto variante, int cantidad) throws StockInsuficienteException {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        validarStock(producto, cantidadEnCarrito(producto) + cantidad);
        for (ItemCarrito item : items) {
            if (mismoProductoYVariante(item, producto, variante)) {
                item.incrementarCantidad(cantidad);
                return;
            }
        }
        ItemCarrito nuevo = new ItemCarrito();
        nuevo.setProducto(producto);
        nuevo.setVariante(variante);
        nuevo.setCantidad(cantidad);
        nuevo.setPrecioUnitario(variante != null ? variante.getPrecioVenta() : producto.getPrecioUnitario());
        nuevo.setDescuentoLinea(0);
        items.add(nuevo);
    }

    public void actualizarCantidad(int indice, int cantidad) throws StockInsuficienteException {
        validarIndice(indice);
        if (cantidad <= 0) {
            eliminarProducto(indice);
            return;
        }
        Producto producto = items.get(indice).getProducto();
        int otrasLineas = cantidadEnCarrito(producto) - items.get(indice).getCantidad();
        validarStock(producto, otrasLineas + cantidad);
        items.get(indice).cambiarCantidad(cantidad);
    }

    public void eliminarProducto(int indice) {
        validarIndice(indice);
        items.remove(indice);
    }

    public double calcularTotal() {
        double total = 0;
        for (ItemCarrito item : items) {
            total += item.subtotal();
        }
        return total;
    }

    public double totalConDescuento() {
        double total = calcularTotal();
        if (estrategiaDescuento == null) {
            return total;
        }
        double descuento = estrategiaDescuento.calcularDescuento(total);
        return Math.max(total - descuento, 0);
    }

    public int cantidadEnCarrito(Producto producto) {
        int total = 0;
        for (ItemCarrito item : items) {
            if (item.getProducto() != null && item.getProducto().getId().equals(producto.getId())) {
                total += item.getCantidad();
            }
        }
        return total;
    }

    public void aplicarCupon(Cupon cupon) {
        if (cupon == null || !cupon.esValido()) {
            throw new IllegalArgumentException("El cupon no es valido o ya vencio");
        }
        this.cupon = cupon;
        this.estrategiaDescuento = cupon;
    }

    public void quitarCupon() {
        this.cupon = null;
        this.estrategiaDescuento = null;
    }

    public Cupon getCupon() {
        return cupon;
    }

    public void validarNoVacio() throws CarritoVacioException {
        if (items.isEmpty()) {
            throw new CarritoVacioException("El carrito esta vacio");
        }
    }

    public Pedido convertirAPedido() throws CarritoVacioException {
        validarNoVacio();

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);

        List<DetallePedido> detallesPedido = new ArrayList<>();
        for (ItemCarrito item : items) {
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(item.getProducto());
            detalle.setVariante(item.getVariante());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(item.subtotal());
            detallesPedido.add(detalle);
        }
        pedido.setDetalles(detallesPedido);
        pedido.setTotal(totalConDescuento());
        pedido.setDescuento(calcularTotal() - totalConDescuento());
        pedido.setCuponCodigo(cupon == null ? null : cupon.getCodigo());

        this.estado = EstadoCarrito.CONVERTIDO;
        return pedido;
    }

    private boolean mismoProductoYVariante(ItemCarrito item, Producto producto, VarianteProducto variante) {
        boolean mismoProducto = item.getProducto() != null && item.getProducto().getId().equals(producto.getId());
        if (!mismoProducto) {
            return false;
        }
        if (item.getVariante() == null && variante == null) {
            return true;
        }
        if (item.getVariante() == null || variante == null) {
            return false;
        }
        return item.getVariante().getId().equals(variante.getId());
    }

    private void validarStock(Producto producto, int cantidadSolicitada) throws StockInsuficienteException {
        if (cantidadSolicitada > producto.getStock()) {
            throw new StockInsuficienteException("Solo hay " + producto.getStock() + " unidades de "
                    + producto.getNombre() + " (ya tienes " + cantidadEnCarrito(producto) + " en tu carrito)");
        }
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= items.size()) {
            throw new IndexOutOfBoundsException("Indice de item invalido: " + indice);
        }
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
