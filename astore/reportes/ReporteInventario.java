package astore.reportes;

import java.util.ArrayList;
import java.util.List;

import astore.catalogo.Producto;
import astore.inventario.Inventario;

public class ReporteInventario extends Reporte {

    private Inventario inventario;
    private List<Producto> catalogo = new ArrayList<>();

    public ReporteInventario() {
    }

    @Override
    protected String generarCuerpo() {
        return null;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public List<Producto> getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(List<Producto> catalogo) {
        this.catalogo = catalogo;
    }
}
