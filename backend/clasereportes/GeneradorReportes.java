package clasereportes;

public class GeneradorReportes {

    public GeneradorReportes() {
    }

    public Reporte crearReporteVentas(Object... args) {
        return new ReporteVentas();
    }

    public Reporte crearReporteInventario(Object... args) {
        return new ReporteInventario();
    }

    public Reporte crearReporteClientes(Object... args) {
        return new ReporteClientes();
    }
}
