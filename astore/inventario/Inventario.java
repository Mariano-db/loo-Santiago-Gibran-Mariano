package astore.inventario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import astore.catalogo.Producto;

public class Inventario {

    private Map<String, Integer> stockPorProducto = new HashMap<>();
    private Map<String, Integer> stockMinimoPorProducto = new HashMap<>();
    private List<ObservadorStock> observadores = new ArrayList<>();
    private List<Existencia> existencias = new ArrayList<>();
    private List<MovimientoInventario> movimientos = new ArrayList<>();

    public Inventario() {
    }

    public void reducirStock(Producto producto, int cantidad) {
    }

    public void incrementarStock(Producto producto, int cantidad) {
    }

    public int consultarStock(String sku) {
        return 0;
    }

    public void obtenerExistencia() {
    }

    public void registrarEntrada() {
    }

    public void registrarSalida() {
    }

    public void reservar() {
    }

    public void liberarReserva() {
    }

    public Map<String, Integer> getStockPorProducto() {
        return stockPorProducto;
    }

    public void setStockPorProducto(Map<String, Integer> stockPorProducto) {
        this.stockPorProducto = stockPorProducto;
    }

    public Map<String, Integer> getStockMinimoPorProducto() {
        return stockMinimoPorProducto;
    }

    public void setStockMinimoPorProducto(Map<String, Integer> stockMinimoPorProducto) {
        this.stockMinimoPorProducto = stockMinimoPorProducto;
    }

    public List<ObservadorStock> getObservadores() {
        return observadores;
    }

    public void setObservadores(List<ObservadorStock> observadores) {
        this.observadores = observadores;
    }

    public List<Existencia> getExistencias() {
        return existencias;
    }

    public void setExistencias(List<Existencia> existencias) {
        this.existencias = existencias;
    }

    public List<MovimientoInventario> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoInventario> movimientos) {
        this.movimientos = movimientos;
    }
}
