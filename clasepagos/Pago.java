package clasepagos;

import java.time.LocalDateTime;

public abstract class Pago implements EstrategiaPago {

    private Long id;
    private double monto;
    private LocalDateTime fecha;
    private EstadoPago estado;
    private boolean exitoso;
    private String motivoRechazo;

    public Pago() {
    }

    public void confirmar() {
        this.estado = EstadoPago.COMPLETADO;
        this.exitoso = true;
    }

    public void cancelar() {
        this.estado = EstadoPago.RECHAZADO;
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

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public String getReferencia() {
        return null;
    }
}
