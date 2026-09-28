package astore.inventario;

import astore.catalogo.Producto;

public interface ObservadorStock {

    void notificarStockBajo(Producto producto, int stockActual);
}
