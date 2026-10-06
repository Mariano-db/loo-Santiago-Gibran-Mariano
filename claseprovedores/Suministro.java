package claseprovedores;

import Catalogo.Producto;

public class Suministro {

    private Producto producto;
    private double costo;
    private int tiempoRestockDias;

    public Suministro() {
    }

    public void actualizarCosto(double nuevoCosto) {
        if (nuevoCosto < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo.");
        }
        this.costo = nuevoCosto;
    }

    public void actualizarTiempoRestock(int dias) {
        if (dias < 0) {
            throw new IllegalArgumentException("Los dias no pueden ser negativos.");
        }
        this.tiempoRestockDias = dias;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public int getTiempoRestockDias() {
        return tiempoRestockDias;
    }

    public void setTiempoRestockDias(int tiempoRestockDias) {
        this.tiempoRestockDias = tiempoRestockDias;
    }
}
