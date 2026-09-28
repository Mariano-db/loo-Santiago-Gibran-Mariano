package astore.reportes;

import java.time.LocalDate;

public abstract class Reporte {

    protected String titulo;
    protected LocalDate fechaInicio;
    protected LocalDate fechaFin;

    public Reporte() {
    }

    public final String generar() {
        return generarCuerpo();
    }

    protected abstract String generarCuerpo();

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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
}
