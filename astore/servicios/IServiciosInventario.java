package astore.servicios;

public interface IServiciosInventario {

    void registrarProducto(Object... args);

    void reabastecer(Object... args);

    void confirmarSalida(Object... args);

    int consultarStock(String sku);
}
