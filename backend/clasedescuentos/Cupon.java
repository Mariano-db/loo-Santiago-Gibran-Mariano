package clasedescuentos;

import java.time.LocalDate;

public class Cupon implements EstrategiaDescuento {

    private Long id;
    private String codigo;
    private TipoDescuento tipoDescuento;
    private double valor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activo;

    public Cupon() {
    }

    public boolean esValido() {
        return esValidoEn(LocalDate.now());
    }

    public boolean esValidoEn(LocalDate fecha) {
        if (!activo) {
            return false;
        }
        if (fechaInicio != null && fecha.isBefore(fechaInicio)) {
            return false;
        }
        return fechaFin == null || !fecha.isAfter(fechaFin);
    }

    @Override
    public double calcularDescuento(double monto) {
        if (tipoDescuento == TipoDescuento.PORCENTAJE) {
            return monto * valor / 100;
        }
        return Math.min(valor, monto);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
