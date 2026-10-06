package Servicios;

import clasereportes.GeneradorReportes;

public class Almacen {

    public final RepositorioCategoriaJson categorias;
    public final RepositorioProductoJson productos;
    public final RepositorioUsuarioJson usuarios;
    public final RepositorioPedidoJson pedidos;
    public final RepositorioCuponJson cupones;
    public final RepositorioFacturaJson facturas;
    public final RepositorioRecogidaJson recogidas;
    public final RepositorioImpresionJson impresiones;
    public final RepositorioProveedorJson proveedores;
    public final RepositorioOrdenCompraJson ordenesCompra;
    public final RepositorioDevolucionJson devoluciones;
    public final RepositorioExistenciaJson existencias;
    public final RepositorioMovimientoJson movimientos;

    public final ServicioUsuarios servicioUsuarios;
    public final ServicioPagos servicioPagos;
    public final ServicioFacturacion servicioFacturacion;
    public final ServicioRecogidas servicioRecogidas;
    public final ServiciosInventario servicioInventario;
    public final ServicioCompras servicioCompras;
    public final ServicioDevoluciones servicioDevoluciones;
    public final GeneradorReportes generadorReportes;

    public Almacen(String carpetaData) {
        String d = carpetaData + "/";
        categorias = new RepositorioCategoriaJson(d + "categorias.json");
        productos = new RepositorioProductoJson(d + "productos.json", categorias);
        usuarios = new RepositorioUsuarioJson(d + "usuarios.json");
        pedidos = new RepositorioPedidoJson(d + "pedidos.json", usuarios);
        cupones = new RepositorioCuponJson(d + "cupones.json");
        facturas = new RepositorioFacturaJson(d + "facturas.json", pedidos);
        recogidas = new RepositorioRecogidaJson(d + "recogidas.json", pedidos);
        impresiones = new RepositorioImpresionJson(d + "impresiones.json", usuarios);
        proveedores = new RepositorioProveedorJson(d + "proveedores.json", productos);
        ordenesCompra = new RepositorioOrdenCompraJson(d + "ordenes_compra.json", proveedores, productos);
        devoluciones = new RepositorioDevolucionJson(d + "devoluciones.json", pedidos);
        existencias = new RepositorioExistenciaJson(d + "existencias.json");
        movimientos = new RepositorioMovimientoJson(d + "movimientos_inventario.json");

        servicioUsuarios = new ServicioUsuarios(usuarios);
        servicioPagos = new ServicioPagos();
        servicioFacturacion = new ServicioFacturacion(facturas);
        servicioRecogidas = new ServicioRecogidas(recogidas, pedidos);
        servicioInventario = new ServiciosInventario(existencias, movimientos, productos);
        servicioCompras = new ServicioCompras(ordenesCompra, servicioInventario);
        servicioDevoluciones = new ServicioDevoluciones(devoluciones);
        generadorReportes = new GeneradorReportes(pedidos, productos, usuarios, devoluciones, servicioInventario);
    }
}
