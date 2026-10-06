package Panel;

import Servicios.Almacen;
import clasereportes.Reporte;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PanelReportes {

    private final Entrada in;
    private final Almacen almacen;

    public PanelReportes(Entrada in, Almacen almacen) {
        this.in = in;
        this.almacen = almacen;
    }

    public void menu() {
        boolean volver = false;
        while (!volver) {
            System.out.println("--- Reportes ---");
            System.out.println("1. Ventas");
            System.out.println("2. Inventario");
            System.out.println("3. Clientes");
            System.out.println("0. Volver");
            String opcion = in.texto("Elige una opcion: ");
            try {
                switch (opcion) {
                    case "1": {
                        LocalDate[] periodo = pedirPeriodo();
                        mostrar(almacen.generadorReportes.crearReporteVentas(periodo[0], periodo[1]));
                        break;
                    }
                    case "2":
                        mostrar(almacen.generadorReportes.crearReporteInventario());
                        break;
                    case "3": {
                        LocalDate[] periodo = pedirPeriodo();
                        mostrar(almacen.generadorReportes.crearReporteClientes(periodo[0], periodo[1]));
                        break;
                    }
                    case "0":
                        volver = true;
                        break;
                    default:
                        System.out.println("Opcion invalida.");
                }
            } catch (DateTimeParseException e) {
                System.out.println("Fecha invalida, usa el formato AAAA-MM-DD.");
            }
            System.out.println();
        }
    }

    private LocalDate[] pedirPeriodo() {
        LocalDate inicio = in.fechaOpcional("Desde AAAA-MM-DD (vacio = desde el inicio): ");
        LocalDate fin = in.fechaOpcional("Hasta AAAA-MM-DD (vacio = hasta hoy): ");
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new DateTimeParseException("Rango invalido", "", 0);
        }
        return new LocalDate[]{inicio, fin};
    }

    private void mostrar(Reporte reporte) {
        System.out.println();
        System.out.println(reporte.generar());
        if (in.confirmar("Guardar este reporte en un archivo (carpeta reportes/)")) {
            try {
                Path ruta = reporte.guardarEnArchivo("reportes");
                System.out.println("Guardado en " + ruta);
            } catch (IOException e) {
                System.out.println("No se pudo guardar el archivo: " + e.getMessage());
            }
        }
    }
}
