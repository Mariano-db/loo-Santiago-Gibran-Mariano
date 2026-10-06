package Servicios;

import Facturacion.DatosFiscales;
import Facturacion.EstadoFactura;
import Facturacion.Factura;
import Utils.ConfiguracionSistema;
import Ventas.EstadoPedido;
import Ventas.Pedido;
import claseseguridad.Cliente;

import java.util.List;
import java.util.Optional;

public class ServicioFacturacion {

    private final RepositorioFacturaJson repositorio;

    public ServicioFacturacion(RepositorioFacturaJson repositorio) {
        this.repositorio = repositorio;
    }

    public Factura solicitar(Cliente cliente, Pedido pedido, DatosFiscales datos) {
        if (pedido.getCliente() == null || pedido.getCliente().getId() != cliente.getId()) {
            throw new IllegalArgumentException("Ese pedido no es tuyo.");
        }
        if (pedido.getEstado() == EstadoPedido.PENDIENTE || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException("Solo se pueden facturar pedidos pagados.");
        }
        Optional<Factura> existente = buscarVigentePorPedido(pedido.getId());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ese pedido ya tiene la factura " + existente.get().folio() + ".");
        }
        Factura factura = new Factura();
        factura.setPedido(pedido);
        factura.setDatosFiscales(datos);
        factura.validarDatosFiscales();
        factura.calcularMontos(pedido.getTotal(), ConfiguracionSistema.getInstancia().getTasaImpuestoDefault());
        factura.solicitar();
        repositorio.guardar(factura);
        return factura;
    }

    public void emitir(Factura factura) {
        if (factura.getEstado() != EstadoFactura.SOLICITADA) {
            throw new IllegalStateException("Solo se pueden emitir facturas solicitadas.");
        }
        factura.emitir();
        repositorio.guardar(factura);
    }

    public void cancelar(Factura factura) {
        if (factura.getEstado() == EstadoFactura.CANCELADA) {
            throw new IllegalStateException("La factura ya esta cancelada.");
        }
        factura.cancelar();
        repositorio.guardar(factura);
    }

    public Optional<Factura> buscarVigentePorPedido(Long pedidoId) {
        return repositorio.listarTodos().stream()
                .filter(f -> f.getPedido() != null && f.getPedido().getId().equals(pedidoId))
                .filter(f -> f.getEstado() != EstadoFactura.CANCELADA)
                .findFirst();
    }

    public List<Factura> listarPorCliente(Cliente cliente) {
        return repositorio.listarPorCliente(cliente.getId());
    }

    public List<Factura> listarTodas() {
        return repositorio.listarTodos();
    }

    public Optional<Factura> buscarPorId(Long id) {
        return repositorio.buscarPorId(id);
    }
}
