package Servicios;

import Exepciones.StockInsuficienteException;
import Ventas.Pedido;

public interface IServiciosInventario {

    int consultarStock(Long productoId, Long varianteId);

    void reabastecer(Long productoId, Long varianteId, int cantidad, String motivo, String referencia);

    void confirmarSalida(Pedido pedido) throws StockInsuficienteException;
}
