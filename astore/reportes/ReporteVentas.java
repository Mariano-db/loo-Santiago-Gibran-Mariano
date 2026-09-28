package astore.reportes;

import java.util.ArrayList;
import java.util.List;

import astore.ventas.Orden;

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
