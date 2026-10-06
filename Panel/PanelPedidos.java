package Panel;

import Servicios.Almacen;
import Ventas.EstadoPedido;
import Ventas.Pedido;

import java.util.List;
import java.util.Optional;

public class PanelPedidos {

    private final Entrada in;
    private final Almacen almacen;

    public PanelPedidos(Entrada in, Almacen almacen) {
        this.in = in;
        this.almacen = almacen;
    }

    public void menu() {
        System.out.println("--- Pedidos ---");
        List<Pedido> lista = almacen.pedidos.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Aun no hay pedidos.");
            return;
        }
        for (Pedido p : lista) {
            String cliente = p.getCliente() == null ? "(cliente desconocido)" : p.getCliente().getNombre();
            System.out.printf("Pedido #%d - %s - %s - %s - Total: $%.2f - %s%n", p.getId(), p.getFecha().toLocalDate(),
                    cliente, p.getEstado(), p.getTotal(), p.getMetodoPago() == null ? "sin pago" : p.getMetodoPago());
        }
        if (in.confirmar("Cambiar el estado de un pedido")) {
            cambiarEstado();
        }
    }

    private void cambiarEstado() {
        Long id = in.id("Id del pedido: ");
        Optional<Pedido> encontrado = id == null ? Optional.empty() : almacen.pedidos.buscarPorId(id);
        if (encontrado.isEmpty()) {
            System.out.println("Pedido no encontrado.");
            return;
        }
        Pedido pedido = encontrado.get();
        EstadoPedido[] estados = EstadoPedido.values();
        for (int i = 0; i < estados.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, estados[i]);
        }
        Integer opcion = in.entero("Nuevo estado: ");
        if (opcion == null || opcion < 1 || opcion > estados.length) {
            System.out.println("Opcion invalida.");
            return;
        }
        EstadoPedido nuevo = estados[opcion - 1];
        if (!puedeCambiar(pedido, nuevo)) {
            return;
        }
        if (nuevo == EstadoPedido.CANCELADO && pedido.getEstado() != EstadoPedido.PENDIENTE) {
            almacen.servicioInventario.reponerPedido(pedido, "Pedido cancelado");
            System.out.println("El stock del pedido se devolvio al inventario.");
        }
        pedido.setEstado(nuevo);
        almacen.pedidos.guardar(pedido);
        System.out.println("Pedido #" + pedido.getId() + " ahora esta " + pedido.getEstado() + ".");
    }

    private boolean puedeCambiar(Pedido pedido, EstadoPedido nuevo) {
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            System.out.println("Un pedido cancelado ya no se puede modificar.");
            return false;
        }
        if (nuevo != EstadoPedido.CANCELADO) {
            return true;
        }
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            System.out.println("Un pedido entregado no se cancela; registra una devolucion.");
            return false;
        }
        if (!almacen.devoluciones.listarPorPedido(pedido.getId()).isEmpty()) {
            System.out.println("Este pedido ya tiene devoluciones; no se puede cancelar.");
            return false;
        }
        return true;
    }
}
