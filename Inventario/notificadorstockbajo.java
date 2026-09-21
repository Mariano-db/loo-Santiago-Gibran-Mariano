import java.util.ArrayList;
import java.util.List;

public class notificadorstockbajo implements observadorstock {

    private List<String> alertas = new ArrayList<>();

    public notificadorstockbajo() {
    }

    @Override
    public void notificarStockBajo(producto producto, int stockActual) {
    }

    public List<String> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<String> alertas) {
        this.alertas = alertas;
    }
}
