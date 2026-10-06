package clasereportes;

import Ventas.EstadoPedido;
import Ventas.Pedido;
import claseseguridad.Cliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteClientes extends Reporte {

    private final List<Cliente> clientes;
    private final List<Pedido> pedidos;

    public ReporteClientes(List<Cliente> clientes, List<Pedido> pedidos, LocalDate inicio, LocalDate fin) {
        this.titulo = "Reporte de clientes";
        this.clientes = clientes;
        this.pedidos = pedidos;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
    }

    @Override
    protected String generarCuerpo() {
        if (clientes.isEmpty()) {
            return "No hay clientes registrados.\n";
        }
        Map<Long, double[]> acumulado = new LinkedHashMap<>();
        for (Pedido p : pedidos) {
            if (p.getCliente() == null || p.getEstado() == EstadoPedido.PENDIENTE
                    || p.getEstado() == EstadoPedido.CANCELADO || !enPeriodo(p.getFecha())) {
                continue;
            }
            double[] datos = acumulado.computeIfAbsent(p.getCliente().getId(), k -> new double[2]);
            datos[0]++;
            datos[1] += p.getTotal();
        }
        List<Cliente> ordenados = new ArrayList<>(clientes);
        ordenados.sort((a, b) -> Double.compare(gastado(acumulado, b), gastado(acumulado, a)));

        long conCompras = ordenados.stream().filter(c -> acumulado.containsKey(c.getId())).count();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Clientes registrados: %d (con compras en el periodo: %d)%n%n", clientes.size(), conCompras));
        sb.append("Clientes por monto gastado:\n");
        for (Cliente c : ordenados) {
            double[] datos = acumulado.getOrDefault(c.getId(), new double[2]);
            sb.append(String.format("  %-25s %-12s %2d pedidos  $%.2f%s%n", c.getNombre(), c.getUsername(),
                    (int) datos[0], datos[1], c.isActivo() ? "" : "  (cuenta desactivada)"));
        }
        return sb.toString();
    }

    private double gastado(Map<Long, double[]> acumulado, Cliente c) {
        double[] datos = acumulado.get(c.getId());
        return datos == null ? 0 : datos[1];
    }

    public List<Cliente> getClientes() {
        return clientes;
    }
}
