public class serviciosinventario implements iserviciosinventario {

    private inventario inventario;

    public serviciosinventario() {
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

    public inventario getInventario() {
        return inventario;
    }

    public void setInventario(inventario inventario) {
        this.inventario = inventario;
    }
}
