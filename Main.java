import java.time.LocalDateTime;

import astore.carrito.CarritoCompra;
import astore.carrito.Detallecarrito;
import astore.carrito.Itemcarrito;
import astore.catalogo.Categoria;
import astore.catalogo.EstadoProducto;
import astore.catalogo.Producto;
import astore.descuentos.Cupon;
import astore.descuentos.Descuentomontofijo;
import astore.descuentos.Sindescuento;
import astore.facturacion.DatosFiscales;
import astore.facturacion.Factura;
import astore.impresion.SolicitudImpresion;
import astore.inventario.EstadoReserva;
import astore.inventario.Existencia;
import astore.inventario.Inventario;
import astore.inventario.Reserva;
import astore.pagos.Pagoefectivo;
import astore.pagos.Pagotarjeta;
import astore.pagos.Tipotarjeta;
import astore.proveedores.Ordencompra;
import astore.proveedores.Proveedor;
import astore.reportes.GeneradorReportes;
import astore.reportes.Reporte;
import astore.reportes.ReporteClientes;
import astore.seguridad.Administrador;
import astore.seguridad.Cliente;
import astore.seguridad.Permiso;
import astore.seguridad.Rol;
import astore.seguridad.TipoCliente;
import astore.seguridad.Vendedor;
import astore.utils.ConfiguracionSistema;
import astore.ventas.EstadoOrden;
import astore.ventas.EstadoPedido;
import astore.ventas.Orden;
import astore.ventas.Pedido;
import astore.ventas.Venta;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== ASTORE - Prueba general del sistema ===");

        demoConfiguracion();
        demoSeguridad();
        demoCatalogo();
        demoInventario();
        demoCarrito();
        demoVentas();
        demoPagos();
        demoDescuentos();
        demoProveedores();
        demoFacturacion();
        demoImpresion();
        demoReportes();

        System.out.println("\n=== Fin de la prueba ===");
    }

    private static void demoConfiguracion() {
        System.out.println("\n-- Configuracion del sistema --");
        ConfiguracionSistema config = ConfiguracionSistema.getInstancia();
        config.setNombreTienda("Astore Anahuac");
        config.setMoneda("MXN");
        config.setTasaImpuestoDefault(0.16);
        System.out.println("Tienda: " + config.getNombreTienda() + " (" + config.getMoneda() + ")");
    }

    private static void demoSeguridad() {
        System.out.println("\n-- Seguridad y usuarios --");

        Permiso permisoVentas = new Permiso();
        permisoVentas.setCodigo("REGISTRAR_VENTA");
        permisoVentas.setDescripcion("Permite registrar ventas");

        Rol rolVendedor = new Rol();
        rolVendedor.setNombre("VENDEDOR");
        rolVendedor.agregarPermiso(permisoVentas);
        System.out.println("Rol " + rolVendedor.getNombre() + " tiene permiso: " + rolVendedor.tienePermiso(permisoVentas));

        Administrador admin = new Administrador();
        admin.setNombre("Carlos Montufar");
        admin.setUsername("cmontufar");
        admin.activar();
        System.out.println("Administrador: " + admin.getNombre() + " activo=" + admin.isActivo());

        Vendedor vendedor = new Vendedor();
        vendedor.setNombre("Ana Vendedora");
        vendedor.setRol(rolVendedor);
        vendedor.setTurno("Matutino");
        vendedor.activar();
        System.out.println("Vendedor: " + vendedor.getNombre() + " turno=" + vendedor.getTurno());

        Cliente cliente = new Cliente();
        cliente.setNombre("Luis Cliente");
        cliente.setRfc("LUCL900101ABC");
        cliente.setTipo(TipoCliente.registrado);
        cliente.activar();
        System.out.println("Cliente: " + cliente.getNombre() + " tipo=" + cliente.getTipo());
    }

    private static void demoCatalogo() {
        System.out.println("\n-- Catalogo --");
        Categoria categoria = new Categoria();
        categoria.setNombre("Electronica");

        Producto producto = new Producto();
        producto.setNombre("Laptop");
        producto.setSku("LAP-001");
        producto.setPrecioUnitario(15000);
        producto.setTasaImpuesto(0.16);
        producto.setEstado(EstadoProducto.ACTIVO);
        producto.setCategoria(categoria);
        categoria.agregarProducto(producto);

        System.out.println("Producto: " + producto.getNombre() + " precio con impuesto=" + producto.precioConImpuesto());
        System.out.println("Categoria " + categoria.getNombre() + " productos=" + categoria.getProductos().size());
    }

    private static void demoInventario() {
        System.out.println("\n-- Inventario --");
        Inventario inventario = new Inventario();

        Existencia existencia = new Existencia();
        existencia.setStockFisico(50);
        existencia.setStockReservado(5);
        System.out.println("Stock disponible: " + existencia.calcularDisponible());

        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.VIGENTE);
        System.out.println("Reserva vigente: " + reserva.estaVigente());

        inventario.getExistencias().add(existencia);
        System.out.println("Existencias registradas: " + inventario.getExistencias().size());
    }

    private static void demoCarrito() {
        System.out.println("\n-- Carrito de compra --");
        Producto producto = new Producto();
        producto.setNombre("Mouse");
        producto.setPrecioUnitario(300);

        Itemcarrito item = new Itemcarrito();
        item.setProducto(producto);
        item.setCantidad(2);
        item.setPreciounitario(300);
        System.out.println("Subtotal item: " + item.subtotal());

        Detallecarrito detalle = new Detallecarrito();
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        detalle.setPreciounitario(300);
        System.out.println("Subtotal detalle: " + detalle.subtotal());

        CarritoCompra carrito = new CarritoCompra();
        carrito.getItems().add(item);
        carrito.getDetalles().add(detalle);
        carrito.setFechacreacion(LocalDateTime.now());
        System.out.println("Items en carrito: " + carrito.getItems().size());
    }

    private static void demoVentas() {
        System.out.println("\n-- Ventas --");
        Orden orden = new Orden();
        orden.setFecha(LocalDateTime.now());
        orden.setEstado(EstadoOrden.PAGADA);
        orden.setTotal(1500);
        System.out.println("Orden total: " + orden.getTotal() + " estado=" + orden.getEstado());

        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        System.out.println("Pedido estado: " + pedido.getEstado());

        Venta venta = new Venta();
        venta.setOrden(orden);
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(orden.getTotal());
        System.out.println("Venta registrada por total: " + venta.getTotal());
    }

    private static void demoPagos() {
        System.out.println("\n-- Pagos --");
        Pagoefectivo pagoEfectivo = new Pagoefectivo();
        pagoEfectivo.setMontoRecibido(500);
        boolean exito = pagoEfectivo.procesar(450);
        System.out.println("Pago efectivo exitoso=" + exito + " cambio=" + pagoEfectivo.getCambio());

        Pagotarjeta pagoTarjeta = new Pagotarjeta();
        pagoTarjeta.setTipoTarjeta(Tipotarjeta.CREDITO);
        pagoTarjeta.setNumeroTarjetaEnmascarado("**** 1234");
        System.out.println("Pago con tarjeta tipo=" + pagoTarjeta.getTipoTarjeta() + " => " + pagoTarjeta.tipo());
    }

    private static void demoDescuentos() {
        System.out.println("\n-- Descuentos --");
        Cupon cupon = new Cupon();
        cupon.setCodigo("VERANO10");
        cupon.setActivo(true);
        System.out.println("Cupon " + cupon.getCodigo() + " activo=" + cupon.isActivo());

        Descuentomontofijo descuentoFijo = new Descuentomontofijo();
        descuentoFijo.setMonto(50);
        System.out.println("Descuento monto fijo aplicado: " + descuentoFijo.calculardescuento(500));

        Sindescuento sinDescuento = new Sindescuento();
        System.out.println("Sin descuento aplicado: " + sinDescuento.calculardescuento(500));
    }

    private static void demoProveedores() {
        System.out.println("\n-- Proveedores --");
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Distribuidora XYZ");
        proveedor.activar();

        Ordencompra ordenCompra = new Ordencompra();
        ordenCompra.setFecha(LocalDateTime.now());
        ordenCompra.setTotal(8000);
        proveedor.getOrdenescompra().add(ordenCompra);

        System.out.println("Proveedor " + proveedor.getNombre() + " ordenes=" + proveedor.getOrdenescompra().size());
    }

    private static void demoFacturacion() {
        System.out.println("\n-- Facturacion --");
        DatosFiscales datosFiscales = new DatosFiscales();
        datosFiscales.setRfc("XAXX010101000");
        datosFiscales.setNombreRazonSocial("Publico en general");

        Factura factura = new Factura();
        factura.setDatosFiscales(datosFiscales);
        factura.solicitar();
        System.out.println("Factura estado=" + factura.getEstado() + " RFC=" + factura.getDatosFiscales().getRfc());
    }

    private static void demoImpresion() {
        System.out.println("\n-- Impresion --");
        SolicitudImpresion solicitud = new SolicitudImpresion();
        solicitud.setArchivo("reporte_ventas.pdf");
        solicitud.setCantidad(3);
        System.out.println("Solicitud de impresion: " + solicitud.getArchivo() + " x" + solicitud.getCantidad());
        solicitud.cancelar();
        System.out.println("Estado tras cancelar: " + solicitud.getEstado());
    }

    private static void demoReportes() {
        System.out.println("\n-- Reportes --");
        GeneradorReportes generador = new GeneradorReportes();

        Reporte reporteVentas = generador.crearReporteVentas();
        reporteVentas.setTitulo("Reporte de ventas del mes");
        System.out.println("Reporte generado: " + reporteVentas.getTitulo());

        Reporte reporteClientes = generador.crearReporteClientes();
        reporteClientes.setTitulo("Reporte de clientes");
        ((ReporteClientes) reporteClientes).getClientes().add(new Cliente());
        System.out.println("Clientes en reporte: " + ((ReporteClientes) reporteClientes).getClientes().size());
    }
}
