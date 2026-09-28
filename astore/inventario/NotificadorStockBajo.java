package astore.inventario;

import java.util.ArrayList;
import java.util.List;

import astore.catalogo.Producto;

public class NotificadorStockBajo implements ObservadorStock {

    private List<String> alertas = new ArrayList<>();

    public NotificadorStockBajo() {
    }

    @Override
    public void notificarStockBajo(Producto producto, int stockActual) {
    }

    public List<String> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<String> alertas) {
        this.alertas = alertas;
    }
}
