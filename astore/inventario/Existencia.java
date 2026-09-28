package astore.inventario;

import astore.catalogo.VarianteProducto;

public class Existencia {

    private VarianteProducto varianteProducto;
    private int stockFisico;
    private int stockReservado;
    private int stockMinimo;
    private int capacidadAlmacen;
    private int tiempoRestockDias;

    public Existencia() {
    }

    public int calcularDisponible() {
        return stockFisico - stockReservado;
    }

    public void reservar() {
    }

    public void liberarReserva() {
    }

    public void retirar() {
    }

    public VarianteProducto getVarianteProducto() {
        return varianteProducto;
    }

    public void setVarianteProducto(VarianteProducto varianteProducto) {
        this.varianteProducto = varianteProducto;
    }

    public int getStockFisico() {
        return stockFisico;
    }

    public void setStockFisico(int stockFisico) {
        this.stockFisico = stockFisico;
    }

    public int getStockReservado() {
        return stockReservado;
    }

    public void setStockReservado(int stockReservado) {
        this.stockReservado = stockReservado;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public int getCapacidadAlmacen() {
        return capacidadAlmacen;
    }

    public void setCapacidadAlmacen(int capacidadAlmacen) {
        this.capacidadAlmacen = capacidadAlmacen;
    }

    public int getTiempoRestockDias() {
        return tiempoRestockDias;
    }

    public void setTiempoRestockDias(int tiempoRestockDias) {
        this.tiempoRestockDias = tiempoRestockDias;
    }
}
