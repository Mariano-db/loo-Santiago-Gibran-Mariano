package clasereportes;

import Servicios.RepositorioDevolucionJson;
import Servicios.RepositorioPedidoJson;
import Servicios.RepositorioProductoJson;
import Servicios.RepositorioUsuarioJson;
import Servicios.ServiciosInventario;
import claseseguridad.Cliente;
import claseseguridad.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GeneradorReportes {

    private final RepositorioPedidoJson pedidos;
    private final RepositorioProductoJson productos;
    private final RepositorioUsuarioJson usuarios;
    private final RepositorioDevolucionJson devoluciones;
    private final ServiciosInventario inventario;

    public GeneradorReportes(RepositorioPedidoJson pedidos, RepositorioProductoJson productos,
                             RepositorioUsuarioJson usuarios, RepositorioDevolucionJson devoluciones,
                             ServiciosInventario inventario) {
        this.pedidos = pedidos;
        this.productos = productos;
        this.usuarios = usuarios;
        this.devoluciones = devoluciones;
        this.inventario = inventario;
    }

    public Reporte crearReporteVentas(LocalDate inicio, LocalDate fin) {
        return new ReporteVentas(pedidos.listarTodos(), devoluciones.listarTodos(), inicio, fin);
    }

    public Reporte crearReporteInventario() {
        return new ReporteInventario(productos.listarTodos(), inventario.listar());
    }

    public Reporte crearReporteClientes(LocalDate inicio, LocalDate fin) {
        List<Cliente> clientes = new ArrayList<>();
        for (Usuario u : usuarios.listarTodos()) {
            if (u instanceof Cliente) {
                clientes.add((Cliente) u);
            }
        }
        return new ReporteClientes(clientes, pedidos.listarTodos(), inicio, fin);
    }
}
