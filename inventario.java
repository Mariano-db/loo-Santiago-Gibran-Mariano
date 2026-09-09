import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class inventario {

    private Map<String, Integer> stockPorProducto = new HashMap<>();
    private Map<String, Integer> stockMinimoPorProducto = new HashMap<>();
    private List<observadorstock> observadores = new ArrayList<>();
    private List<existencia> existencias = new ArrayList<>();
    private List<movimientoinventario> movimientos = new ArrayList<>();

    public inventario() {
    }

    public void reducirStock(producto producto, int cantidad) {
    }

    public void incrementarStock(producto producto, int cantidad) {
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

    public List<observadorstock> getObservadores() {
        return observadores;
    }

    public void setObservadores(List<observadorstock> observadores) {
        this.observadores = observadores;
    }

    public List<existencia> getExistencias() {
        return existencias;
    }

    public void setExistencias(List<existencia> existencias) {
        this.existencias = existencias;
    }

    public List<movimientoinventario> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<movimientoinventario> movimientos) {
        this.movimientos = movimientos;
    }
}
