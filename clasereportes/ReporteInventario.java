package clasereportes;

import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Inventario.Existencia;

import java.util.ArrayList;
import java.util.List;

public class ReporteInventario extends Reporte {

    private final List<Producto> catalogo;
    private final List<Existencia> existencias;

    public ReporteInventario(List<Producto> catalogo, List<Existencia> existencias) {
        this.titulo = "Reporte de inventario";
        this.catalogo = catalogo;
        this.existencias = existencias;
    }

    private Existencia de(Long productoId, Long varianteId) {
        for (Existencia e : existencias) {
            if (e.es(productoId, varianteId)) {
                return e;
            }
        }
        return null;
    }

    @Override
    protected String generarCuerpo() {
        if (catalogo.isEmpty()) {
            return "El catalogo esta vacio.\n";
        }
        StringBuilder detalle = new StringBuilder();
        List<String> bajos = new ArrayList<>();
        double valor = 0;
        int unidades = 0;
        for (Producto p : catalogo) {
            int total = 0;
            StringBuilder lineasVariante = new StringBuilder();
            if (p.getVariantes().isEmpty()) {
                Existencia e = de(p.getId(), null);
                total = e == null ? 0 : e.calcularDisponible();
                if (e != null && e.estaBajoMinimo()) {
                    bajos.add(String.format("%s: %d (minimo %d)", p.getNombre(), total, e.getStockMinimo()));
                }
            } else {
                for (VarianteProducto v : p.getVariantes()) {
                    Existencia e = de(p.getId(), v.getId());
                    int cantidad = e == null ? 0 : e.calcularDisponible();
                    total += cantidad;
                    lineasVariante.append(String.format("        %-24s %3d uds%n", v.getColor() + ", " + v.getTalla(), cantidad));
                    if (e != null && e.estaBajoMinimo()) {
                        bajos.add(String.format("%s (%s, %s): %d (minimo %d)", p.getNombre(), v.getColor(), v.getTalla(),
                                cantidad, e.getStockMinimo()));
                    }
                }
            }
            valor += p.getPrecioUnitario() * total;
            unidades += total;
            detalle.append(String.format("  [%d] %-30s %5d uds  $%.2f c/u%s%n", p.getId(), p.getNombre(), total,
                    p.getPrecioUnitario(), p.isActivo() ? "" : "  (oculto)"));
            detalle.append(lineasVariante);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Productos en catalogo: %d%n", catalogo.size()));
        sb.append(String.format("Unidades disponibles: %d%n", unidades));
        sb.append(String.format("Valor del inventario (a precio de venta): $%.2f MXN%n%n", valor));
        sb.append("Existencias por producto:\n").append(detalle);
        sb.append("\nStock en o bajo el minimo:\n");
        if (bajos.isEmpty()) {
            sb.append("  Ninguno.\n");
        }
        for (String b : bajos) {
            sb.append("  ").append(b).append('\n');
        }
        return sb.toString();
    }

    public List<Producto> getCatalogo() {
        return catalogo;
    }
}
