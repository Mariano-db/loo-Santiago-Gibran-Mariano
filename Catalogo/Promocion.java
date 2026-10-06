package Catalogo;

import clasedescuentos.TipoDescuento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Promocion {

    private Long id;
    private String nombre;
    private String descripcion;
    private TipoDescuento tipoDescuento;
    private double valor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa;
    private List<Producto> productos = new ArrayList<>();

    public Promocion() {
    }

    public boolean estaActiva() {
        return activa;
    }

    public double calcularDescuento(double monto) {
        LocalDate hoy = LocalDate.now();
        boolean vigente = activa && (fechaInicio == null || !hoy.isBefore(fechaInicio))
                && (fechaFin == null || !hoy.isAfter(fechaFin));
        if (!vigente || monto <= 0) {
            return 0;
        }
        double descuento = tipoDescuento == TipoDescuento.PORCENTAJE ? monto * valor / 100 : valor;
        return Math.min(Math.max(descuento, 0), monto);
    }

    public void activar() {
        this.activa = true;
    }

    public void desactivar() {
        this.activa = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoDescuento getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(TipoDescuento tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}
