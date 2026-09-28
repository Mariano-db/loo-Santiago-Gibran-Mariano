package astore.servicios;

import astore.inventario.Inventario;

public class ServiciosInventario implements IServiciosInventario {

    private Inventario inventario;

    public ServiciosInventario() {
    }

    @Override
    public void registrarProducto(Object... args) {
    }

    @Override
    public void reabastecer(Object... args) {
    }

    @Override
    public void confirmarSalida(Object... args) {
    }

    @Override
    public int consultarStock(String sku) {
        return 0;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }
}
