package Api;

import Servicios.RepositorioCategoriaJson;
import Servicios.RepositorioCuponJson;
import Servicios.RepositorioPedidoJson;
import Servicios.RepositorioProductoJson;
import Servicios.RepositorioUsuarioJson;
import Servicios.ServicioClientes;
import Servicios.ServicioPagos;
import Servicios.ServicioPedidos;

public class ContextoApi {

    public final RepositorioCategoriaJson categorias;
    public final RepositorioProductoJson productos;
    public final RepositorioUsuarioJson usuarios;
    public final RepositorioCuponJson cupones;
    public final RepositorioPedidoJson pedidos;
    public final ServicioClientes servicioClientes;
    public final ServicioPedidos servicioPedidos;
    public final GestorSesiones sesiones = new GestorSesiones();

    public ContextoApi(String directorioDatos) {
        String base = directorioDatos.endsWith("/") ? directorioDatos : directorioDatos + "/";
        this.categorias = new RepositorioCategoriaJson(base + "categorias.json");
        this.productos = new RepositorioProductoJson(base + "productos.json", categorias);
        this.usuarios = new RepositorioUsuarioJson(base + "usuarios.json");
        this.cupones = new RepositorioCuponJson(base + "cupones.json");
        this.pedidos = new RepositorioPedidoJson(base + "pedidos.json");
        this.servicioClientes = new ServicioClientes(usuarios);
        this.servicioPedidos = new ServicioPedidos(productos, pedidos, new ServicioPagos());
    }
}
