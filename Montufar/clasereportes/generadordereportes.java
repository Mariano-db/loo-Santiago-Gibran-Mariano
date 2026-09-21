public class generadorreportes {

    public generadorreportes() {
    }

    public reporte crearreporteventas(object... args) {
        return new reporteventas();
    }

    public reporte crearreporteinventario(object... args) {
        return new reporteinventario();
    }

    public reporte crearreporteclientes(object... args) {
        return new reporteclientes();
    }
}