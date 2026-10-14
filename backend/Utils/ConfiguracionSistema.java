package Utils;

public class ConfiguracionSistema {

    private static ConfiguracionSistema instancia;
    private String nombreTienda;
    private String moneda;
    private double tasaImpuestoDefault;

    private ConfiguracionSistema() {
    }

    public static ConfiguracionSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionSistema();
        }
        return instancia;
    }

    public String getNombreTienda() {
        return nombreTienda;
    }

    public void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public double getTasaImpuestoDefault() {
        return tasaImpuestoDefault;
    }

    public void setTasaImpuestoDefault(double tasaImpuestoDefault) {
        this.tasaImpuestoDefault = tasaImpuestoDefault;
    }
}
