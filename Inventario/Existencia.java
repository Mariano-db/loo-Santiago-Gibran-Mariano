package Inventario;

public class Existencia {

    private Long id;
    private Long productoId;
    private Long varianteId;
    private int stockFisico;
    private int stockReservado;
    private int stockMinimo;

    public Existencia() {
    }

    public int calcularDisponible() {
        return stockFisico - stockReservado;
    }

    public boolean estaBajoMinimo() {
        return calcularDisponible() <= stockMinimo;
    }

    public void reservar(int cantidad) {
        if (cantidad < 1 || cantidad > calcularDisponible()) {
            throw new IllegalArgumentException("No hay " + cantidad + " unidades disponibles para reservar.");
        }
        stockReservado += cantidad;
    }

    public void liberarReserva(int cantidad) {
        if (cantidad < 1 || cantidad > stockReservado) {
            throw new IllegalArgumentException("No hay tantas unidades reservadas.");
        }
        stockReservado -= cantidad;
    }

    public void retirar(int cantidad) {
        if (cantidad < 1 || cantidad > calcularDisponible()) {
            throw new IllegalArgumentException("Stock insuficiente: disponibles " + calcularDisponible()
                    + ", solicitados " + cantidad + ".");
        }
        stockFisico -= cantidad;
    }

    public void reponer(int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        stockFisico += cantidad;
    }

    public boolean es(Long productoId, Long varianteId) {
        return java.util.Objects.equals(this.productoId, productoId) && java.util.Objects.equals(this.varianteId, varianteId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getVarianteId() {
        return varianteId;
    }

    public void setVarianteId(Long varianteId) {
        this.varianteId = varianteId;
    }

    public int getStockFisico() {
        return stockFisico;
    }

    public void setStockFisico(int stockFisico) {
        if (stockFisico < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
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
        if (stockMinimo < 0) {
            throw new IllegalArgumentException("El stock minimo no puede ser negativo.");
        }
        this.stockMinimo = stockMinimo;
    }
}
