package Panel;

import Servicios.Almacen;
import Ventas.DetallePedido;
import Ventas.Devolucion;
import Ventas.EstadoPedido;
import Ventas.Pedido;

import java.util.List;
import java.util.Optional;

public class PanelDevoluciones {

    private final Entrada in;
    private final Almacen almacen;

    public PanelDevoluciones(Entrada in, Almacen almacen) {
        this.in = in;
        this.almacen = almacen;
    }

    public void menu() {
        boolean volver = false;
        while (!volver) {
            System.out.println("--- Devoluciones ---");
            System.out.println("1. Registrar una devolucion");
            System.out.println("2. Ver devoluciones");
            System.out.println("0. Volver");
            switch (in.texto("Elige una opcion: ")) {
                case "1": registrar(); break;
                case "2": listar(); break;
                case "0": volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private void listar() {
        List<Devolucion> lista = almacen.devoluciones.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay devoluciones.");
            return;
        }
        for (Devolucion d : lista) {
            System.out.printf("[%d] %s - Pedido #%d - %dx %s - reembolso $%.2f - motivo: %s%n", d.getId(),
                    d.getFecha().toLocalDate(), d.getPedidoOrigen().getId(), d.getCantidad(),
                    d.getProducto().getNombre(), d.getMontoReembolsado(), d.getMotivo());
        }
    }

    private void registrar() {
        for (Pedido p : almacen.pedidos.listarTodos()) {
            if (p.getEstado() != EstadoPedido.PENDIENTE && p.getEstado() != EstadoPedido.CANCELADO) {
                System.out.printf("  Pedido #%d - %s - %s - $%.2f%n", p.getId(), p.getFecha().toLocalDate(),
                        p.getCliente() == null ? "?" : p.getCliente().getNombre(), p.getTotal());
            }
        }
        Long id = in.id("Numero de pedido (vacio para cancelar): ");
        Optional<Pedido> encontrado = id == null ? Optional.empty() : almacen.pedidos.buscarPorId(id);
        if (encontrado.isEmpty()) {
            System.out.println("Pedido no encontrado.");
            return;
        }
        Pedido pedido = encontrado.get();
        List<DetallePedido> detalles = pedido.getDetalles();
        for (int i = 0; i < detalles.size(); i++) {
            DetallePedido d = detalles.get(i);
            String variante = d.getVariante() == null ? ""
                    : " (" + d.getVariante().getColor() + ", " + d.getVariante().getTalla() + ")";
            System.out.printf("  %d. %s%s - comprados: %d, ya devueltos: %d%n", i + 1, d.getProducto().getNombre(),
                    variante, d.getCantidad(), almacen.servicioDevoluciones.cantidadYaDevuelta(pedido, d));
        }
        Integer linea = in.entero("Linea a devolver: ");
        if (linea == null || linea < 1 || linea > detalles.size()) {
            System.out.println("Linea invalida.");
            return;
        }
        DetallePedido detalle = detalles.get(linea - 1);
        Integer cantidad = in.entero("Cantidad a devolver: ");
        String motivo = in.texto("Motivo: ");
        try {
            Devolucion d = almacen.servicioDevoluciones.registrar(pedido, detalle, cantidad == null ? 0 : cantidad, motivo);
            System.out.printf("Devolucion #%d registrada. Reembolso: $%.2f MXN.%n", d.getId(), d.getMontoReembolsado());
            if (in.confirmar("El producto esta en buen estado, reingresarlo al inventario")) {
                Long varianteId = detalle.getVariante() == null ? null : detalle.getVariante().getId();
                almacen.servicioInventario.reabastecer(detalle.getProducto().getId(), varianteId, d.getCantidad(),
                        "Devolucion", "Devolucion #" + d.getId());
                System.out.println("Stock reingresado.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo registrar: " + e.getMessage());
        }
    }
}
