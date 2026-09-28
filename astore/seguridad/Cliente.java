package astore.seguridad;

import java.util.ArrayList;
import java.util.List;

import astore.carrito.CarritoCompra;
import astore.catalogo.Producto;
import astore.ventas.Orden;

public class Cliente extends Usuario {

    private String rfc;
    private TipoCliente tipo;
    private int puntosFidelidad;
    private List<Orden> historialOrdenes = new ArrayList<>();
    private CarritoCompra carrito;

    public Cliente() {
    }

    public void registrarCompra(Orden orden) {
        historialOrdenes.add(orden);
    }

    public double totalGastadoHistorico() {
        return 0;
    }

    public List<Producto> verProductos() {
        return new ArrayList<>();
    }

    public List<Producto> buscarProductos() {
        return new ArrayList<>();
    }

    public void agregarAlCarrito() {
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public TipoCliente getTipo() {
        return tipo;
    }

    public void setTipo(TipoCliente tipo) {
        this.tipo = tipo;
    }

    public int getPuntosFidelidad() {
        return puntosFidelidad;
    }

    public void setPuntosFidelidad(int puntosFidelidad) {
        this.puntosFidelidad = puntosFidelidad;
    }

    public List<Orden> getHistorialOrdenes() {
        return historialOrdenes;
    }

    public void setHistorialOrdenes(List<Orden> historialOrdenes) {
        this.historialOrdenes = historialOrdenes;
    }

    public CarritoCompra getCarrito() {
        return carrito;
    }

    public void setCarrito(CarritoCompra carrito) {
        this.carrito = carrito;
    }
}
