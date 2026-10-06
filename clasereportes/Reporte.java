package clasereportes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Reporte {

    protected String titulo;
    protected LocalDate fechaInicio;
    protected LocalDate fechaFin;

    public Reporte() {
    }

    public final String generar() {
        StringBuilder sb = new StringBuilder();
        sb.append("==== ").append(titulo).append(" ====\n");
        sb.append("Generado: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append('\n');
        if (fechaInicio != null || fechaFin != null) {
            sb.append("Periodo: ").append(fechaInicio == null ? "inicio" : fechaInicio)
                    .append(" a ").append(fechaFin == null ? "hoy" : fechaFin).append('\n');
        }
        sb.append('\n').append(generarCuerpo());
        return sb.toString();
    }

    public Path guardarEnArchivo(String carpeta) throws IOException {
        Files.createDirectories(Path.of(carpeta));
        String nombre = titulo.toLowerCase().replaceAll("[^a-z0-9]+", "-")
                + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".txt";
        return Files.writeString(Path.of(carpeta, nombre), generar());
    }

    protected abstract String generarCuerpo();

    protected boolean enPeriodo(LocalDateTime fecha) {
        if (fecha == null) {
            return false;
        }
        LocalDate dia = fecha.toLocalDate();
        return (fechaInicio == null || !dia.isBefore(fechaInicio)) && (fechaFin == null || !dia.isAfter(fechaFin));
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }
}
