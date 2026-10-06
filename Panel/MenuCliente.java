package Panel;

import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Servicios.Almacen;
import Ventas.DetallePedido;
import Ventas.Devolucion;
import Ventas.Pedido;
import clasecarrostotal.CarritoCompra;
import clasecarrostotal.ItemCarrito;
import clasedescuentos.Cupon;
import claseseguridad.Cliente;
import claseseguridad.TipoCliente;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MenuCliente {

    private final Entrada in;
    private final Almacen almacen;
    private final Cliente cliente;
    private CarritoCompra carrito;

    public MenuCliente(Entrada in, Almacen almacen, Cliente cliente) {
        this.in = in;
        this.almacen = almacen;
        this.cliente = cliente;
        this.carrito = nuevoCarrito();
    }

    public void ejecutar() {
        boolean cerrar = false;
        while (!cerrar) {
            mostrarOpciones();
            switch (in.texto("Elige una opcion: ")) {
                case "1": verCatalogo(); break;
                case "2": agregarAlCarrito(); break;
                case "3": verCarrito(); break;
                case "4": quitarDelCarrito(); break;
                case "5": finalizarCompra(); break;
                case "6": verMisPedidos(); break;
                case "7": aplicarCupon(); break;
                case "8":
                    if (requiereSesion()) {
                        new PanelFacturacion(in, almacen).menuCliente(cliente);
                    }
                    break;
                case "9":
                    if (requiereSesion()) {
                        new PanelImpresion(in, almacen).menuCliente(cliente);
                    }
                    break;
                case "0":
                    cerrar = true;
                    System.out.println("Sesion cerrada.");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private void mostrarOpciones() {
        System.out.println("1. Ver catalogo");
        System.out.println("2. Agregar producto al carrito");
        System.out.println("3. Ver carrito");
        System.out.println("4. Eliminar producto del carrito");
        System.out.println("5. Finalizar compra");
        System.out.println("6. Ver mis pedidos");
        System.out.println("7. Aplicar cupon de descuento");
        System.out.println("8. Facturas");
        System.out.println("9. Impresiones");
        System.out.println("0. Cerrar sesion");
    }

    private CarritoCompra nuevoCarrito() {
        CarritoCompra nuevo = new CarritoCompra();
        nuevo.setCliente(cliente);
        return nuevo;
    }

    private boolean esInvitado() {
        return cliente.getTipo() == TipoCliente.INVITADO;
    }

    private boolean requiereSesion() {
        if (esInvitado()) {
            System.out.println("Inicia sesion o registrate para usar esta opcion.");
            return false;
        }
        return true;
    }

    private void verCatalogo() {
        List<Producto> productos = almacen.productos.listarActivos();
        if (productos.isEmpty()) {
            System.out.println("El catalogo esta vacio.");
            return;
        }
        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            String categoria = producto.getCategoria() == null ? "Sin categoria" : producto.getCategoria().getNombre();
            int stock = producto.getVariantes().isEmpty()
                    ? almacen.servicioInventario.consultarStock(producto.getId(), null)
                    : producto.getStock();
            System.out.printf("%d. [%s] %s - $%.2f MXN - %s%n", i + 1, categoria, producto.getNombre(),
                    producto.getPrecioUnitario(), stock > 0 ? "Disponibles: " + stock : "AGOTADO");
            for (VarianteProducto variante : producto.getVariantes()) {
                int disponibles = almacen.servicioInventario.consultarStock(producto.getId(), variante.getId());
                System.out.printf("    - %s | color: %s | talla: %s | %s%n", variante.getSku(), variante.getColor(),
                        variante.getTalla(), unidadesODisponibilidad(disponibles));
            }
        }
    }

    private String unidadesODisponibilidad(int unidades) {
        return unidades > 0 ? unidades + " disp." : "agotado";
    }

    private void agregarAlCarrito() {
        Producto producto = elegirProducto();
        if (producto == null) {
            return;
        }
        VarianteProducto variante = null;
        if (!producto.getVariantes().isEmpty()) {
            variante = elegirVariante(producto);
            if (variante == null) {
                return;
            }
        }
        Integer cantidad = in.entero("Cantidad: ");
        if (cantidad == null || cantidad <= 0) {
            System.out.println("Cantidad invalida.");
            return;
        }
        if (!hayStockParaAgregar(producto, variante, cantidad)) {
            return;
        }
        carrito.agregarProducto(producto, variante, cantidad);
        System.out.println("Producto agregado al carrito.");
    }

    private Producto elegirProducto() {
        List<Producto> productos = almacen.productos.listarActivos();
        if (productos.isEmpty()) {
            System.out.println("El catalogo esta vacio.");
            return null;
        }
        for (int i = 0; i < productos.size(); i++) {
            System.out.printf("%d. %s - $%.2f MXN%n", i + 1, productos.get(i).getNombre(), productos.get(i).getPrecioUnitario());
        }
        Integer seleccion = in.entero("Numero de producto a agregar (0 para cancelar): ");
        if (seleccion == null || seleccion == 0) {
            return null;
        }
        if (seleccion < 1 || seleccion > productos.size()) {
            System.out.println("Numero invalido.");
            return null;
        }
        return productos.get(seleccion - 1);
    }

    private VarianteProducto elegirVariante(Producto producto) {
        List<VarianteProducto> variantes = producto.getVariantes();
        for (int i = 0; i < variantes.size(); i++) {
            VarianteProducto v = variantes.get(i);
            int disponibles = almacen.servicioInventario.consultarStock(producto.getId(), v.getId());
            System.out.printf("%d. color: %s - talla: %s (%s)%n", i + 1, v.getColor(), v.getTalla(),
                    unidadesODisponibilidad(disponibles));
        }
        Integer seleccion = in.entero("Elige una variante (color/talla): ");
        if (seleccion == null || seleccion < 1 || seleccion > variantes.size()) {
            System.out.println("Variante invalida, se cancela.");
            return null;
        }
        return variantes.get(seleccion - 1);
    }

    private boolean hayStockParaAgregar(Producto producto, VarianteProducto variante, int cantidad) {
        Long varianteId = variante == null ? null : variante.getId();
        int disponibles = almacen.servicioInventario.consultarStock(producto.getId(), varianteId);
        int enCarrito = 0;
        for (ItemCarrito item : carrito.getItems()) {
            Long varianteItem = item.getVariante() == null ? null : item.getVariante().getId();
            if (item.getProducto().getId().equals(producto.getId()) && Objects.equals(varianteItem, varianteId)) {
                enCarrito += item.getCantidad();
            }
        }
        if (cantidad + enCarrito <= disponibles) {
            return true;
        }
        if (disponibles == 0) {
            System.out.println("Producto agotado.");
        } else {
            System.out.println("Solo hay " + disponibles + " disponibles"
                    + (enCarrito > 0 ? " (ya tienes " + enCarrito + " en el carrito)." : "."));
        }
        return false;
    }

    private void verCarrito() {
        List<ItemCarrito> items = carrito.getItems();
        if (items.isEmpty()) {
            System.out.println("El carrito esta vacio.");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            ItemCarrito item = items.get(i);
            System.out.printf("%d. %s%s x%d - $%.2f c/u - Subtotal: $%.2f%n", i + 1, item.getProducto().getNombre(),
                    Descripciones.variante(item.getVariante()), item.getCantidad(), item.getPrecioUnitario(), item.subtotal());
        }
        System.out.printf("Subtotal: $%.2f MXN%n", carrito.calcularTotal());
        double descuento = carrito.calcularDescuento();
        Cupon cupon = carrito.getCuponAplicado();
        if (descuento > 0) {
            System.out.printf("Descuento%s: -$%.2f MXN%n", cupon == null ? "" : " (cupon " + cupon.getCodigo() + ")", descuento);
        } else if (cupon != null) {
            System.out.println("El cupon " + cupon.getCodigo() + " ya no aplica a este carrito.");
        }
        System.out.printf("Total: $%.2f MXN%n", carrito.totalConDescuento());
    }

    private void quitarDelCarrito() {
        if (carrito.getItems().isEmpty()) {
            System.out.println("El carrito esta vacio.");
            return;
        }
        verCarrito();
        Integer seleccion = in.entero("Numero de item a eliminar (0 para cancelar): ");
        if (seleccion == null || seleccion == 0) {
            return;
        }
        if (seleccion < 1 || seleccion > carrito.getItems().size()) {
            System.out.println("Numero invalido.");
            return;
        }
        carrito.eliminarProducto(seleccion - 1);
        System.out.println("Producto eliminado del carrito.");
    }

    private void aplicarCupon() {
        if (carrito.getItems().isEmpty()) {
            System.out.println("Agrega productos al carrito antes de aplicar un cupon.");
            return;
        }
        String codigo = in.texto("Codigo del cupon (vacio para quitar el actual): ");
        if (codigo.isEmpty()) {
            carrito.quitarCupon();
            System.out.println("Cupon quitado.");
            return;
        }
        Optional<Cupon> cupon = almacen.cupones.buscarPorCodigo(codigo);
        if (cupon.isEmpty()) {
            System.out.println("Ese cupon no existe.");
            return;
        }
        try {
            carrito.aplicarCupon(cupon.get());
            System.out.println("Cupon aplicado: " + cupon.get().descripcion());
            verCarrito();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void finalizarCompra() {
        if (esInvitado()) {
            System.out.println("Para finalizar la compra debes iniciar sesion o registrarte.");
            return;
        }
        if (new ProcesoCompra(in, almacen, carrito).ejecutar()) {
            carrito = nuevoCarrito();
        }
    }

    private void verMisPedidos() {
        if (!requiereSesion()) {
            return;
        }
        List<Pedido> pedidos = almacen.pedidos.listarPorCliente(cliente.getId());
        if (pedidos.isEmpty()) {
            System.out.println("Aun no tienes pedidos.");
            return;
        }
        for (Pedido pedido : pedidos) {
            imprimirPedido(pedido);
        }
    }

    private void imprimirPedido(Pedido pedido) {
        System.out.printf("Pedido #%d - %s - %s - Total: $%.2f MXN%n", pedido.getId(), pedido.getFecha().toLocalDate(),
                pedido.getEstado(), pedido.getTotal());
        if (pedido.getDescuento() > 0) {
            System.out.printf("  Descuento (cupon %s): -$%.2f MXN%n", pedido.getCuponCodigo(), pedido.getDescuento());
        }
        almacen.recogidas.buscarPorPedido(pedido.getId()).ifPresent(r -> System.out.println(
                "  Recogida: " + r.estadoActual() + " (limite " + r.getFechaLimite().toLocalDate() + ")"));
        for (Devolucion devolucion : almacen.devoluciones.listarPorPedido(pedido.getId())) {
            System.out.printf("  Devolucion: %dx %s - reembolso $%.2f%n", devolucion.getCantidad(),
                    devolucion.getProducto().getNombre(), devolucion.getMontoReembolsado());
        }
        if (pedido.getMetodoPago() != null) {
            System.out.println("  Pago: " + pedido.getMetodoPago() + " (ref: " + pedido.getReferenciaPago() + ")");
        }
        for (DetallePedido detalle : pedido.getDetalles()) {
            System.out.printf("  - %s%s x%d - $%.2f%n", detalle.getProducto().getNombre(),
                    Descripciones.variante(detalle.getVariante()), detalle.getCantidad(), detalle.getSubtotal());
        }
    }
}
