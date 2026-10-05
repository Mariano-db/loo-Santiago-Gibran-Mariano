package clasereportes;

import Ventas.Orden;

import java.util.ArrayList;
import java.util.List;

public class ReporteVentas extends Reporte {

    private List<Orden> ordenes = new ArrayList<>();

    public ReporteVentas() {
    }

    @Override
    protected String generarCuerpo() {
        return null;
    }

    public List<Orden> getOrdenes() {
        return ordenes;
    }

    public void setOrdenes(List<Orden> ordenes) {
        this.ordenes = ordenes;
    }
}
