package clasepagos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Pago {

    private Long id;
    private double monto;
    private LocalDateTime fecha;
    private EstadoPago estado;
    private boolean exitoso;
    private EstrategiaPago estrategia;
    private List<DetallePago> detallesPago = new ArrayList<>();

    public Pago() {
    }

    public void ejecutar() {
    }

    public void procesar() {
    }

    public void confirmar() {
        this.estado = EstadoPago.COMPLETADO;
        this.exitoso = true;
    }

    public void cancelar() {
        this.estado = EstadoPago.RECHAZADO;
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

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }

    public EstrategiaPago getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(EstrategiaPago estrategia) {
        this.estrategia = estrategia;
    }

    public List<DetallePago> getDetallesPago() {
        return detallesPago;
    }

    public void setDetallesPago(List<DetallePago> detallesPago) {
        this.detallesPago = detallesPago;
    }
}
