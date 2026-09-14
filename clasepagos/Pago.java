import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Pago {

    private Long id;
    private double monto;
    private LocalDateTime fecha;
    private Estadopago estado;
    private boolean exitoso;
    private Estrategiapago estrategia;
    private List<Detallepago> detallesPago = new ArrayList<>();

    public Pago() {
    }

    public void ejecutar() {
    }

    public void procesar() {
    }

    public void confirmar() {
        this.estado = Estadopago.COMPLETADO;
        this.exitoso = true;
    }

    public void cancelar() {
        this.estado = Estadopago.RECHAZADO;
    }

    public boolean validar() {
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Estadopago getEstado() {
        return estado;
    }

    public void setEstado(Estadopago estado) {
        this.estado = estado;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }

    public Estrategiapago getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(Estrategiapago estrategia) {
        this.estrategia = estrategia;
    }

    public List<Detallepago> getDetallesPago() {
        return detallesPago;
    }

    public void setDetallesPago(List<Detallepago> detallesPago) {
        this.detallesPago = detallesPago;
    }
}
