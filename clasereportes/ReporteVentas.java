package clasereportes;

import Ventas.DetallePedido;
import Ventas.Devolucion;
import Ventas.EstadoPedido;
import Ventas.Pedido;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteVentas extends Reporte {

    private final List<Pedido> pedidos;
    private final List<Devolucion> devoluciones;

    public ReporteVentas(List<Pedido> pedidos, List<Devolucion> devoluciones, LocalDate inicio, LocalDate fin) {
        this.titulo = "Reporte de ventas";
        this.pedidos = pedidos;
        this.devoluciones = devoluciones;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
    }

    private List<Pedido> ventasDelPeriodo() {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getEstado() != EstadoPedido.PENDIENTE && p.getEstado() != EstadoPedido.CANCELADO
                    && enPeriodo(p.getFecha())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    protected String generarCuerpo() {
        List<Pedido> ventas = ventasDelPeriodo();
        if (ventas.isEmpty()) {
            return "No hay ventas en el periodo.\n";
        }
        double bruto = 0;
        double descuentos = 0;
        Map<String, Double> porMetodo = new LinkedHashMap<>();
        Map<String, Integer> unidades = new LinkedHashMap<>();
        Map<String, Double> ingresos = new LinkedHashMap<>();
        for (Pedido p : ventas) {
            bruto += p.getTotal();
            descuentos += p.getDescuento();
            porMetodo.merge(p.getMetodoPago() == null ? "SIN DATO" : p.getMetodoPago(), p.getTotal(), Double::sum);
            for (DetallePedido d : p.getDetalles()) {
                String nombre = d.getProducto().getNombre();
                unidades.merge(nombre, d.getCantidad(), Integer::sum);
                ingresos.merge(nombre, d.getSubtotal(), Double::sum);
            }
        }
        double devuelto = 0;
        for (Devolucion d : devoluciones) {
            if (enPeriodo(d.getFecha())) {
                devuelto += d.getMontoReembolsado();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Pedidos pagados: %d%n", ventas.size()));
        sb.append(String.format("Ventas brutas:   $%.2f MXN%n", bruto));
        sb.append(String.format("Descuentos dados:$%.2f MXN%n", descuentos));
        sb.append(String.format("Devoluciones:    -$%.2f MXN%n", devuelto));
        sb.append(String.format("VENTAS NETAS:    $%.2f MXN%n", bruto - devuelto));
        sb.append(String.format("Ticket promedio: $%.2f MXN%n%n", bruto / ventas.size()));

        sb.append("Por metodo de pago:\n");
        porMetodo.forEach((metodo, monto) -> sb.append(String.format("  %-14s $%.2f%n", metodo, monto)));

        sb.append("\nProductos mas vendidos (por unidades):\n");
        unidades.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .forEach(e -> sb.append(String.format("  %-30s %3d uds  $%.2f%n", e.getKey(), e.getValue(),
                        ingresos.get(e.getKey()))));
        return sb.toString();
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }
}
