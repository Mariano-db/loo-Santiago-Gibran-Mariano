package Inventario;

import Catalogo.Producto;

public interface ObservadorStock {

    void notificarStockBajo(Producto producto, int stockActual);
}
