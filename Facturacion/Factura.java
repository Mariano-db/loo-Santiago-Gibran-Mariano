public class Factura {

    private Long id;
    private LocalDateTime fechaSolicitud;
    private EstadoFactura estado;
    private DatosFiscales datosFiscales;

    public Factura() {
    }

    public void solicitar() {
        this.estado = EstadoFactura.SOLICITADA;
    }

    public void validarDatosFiscales() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }

    public DatosFiscales getDatosFiscales() {
        return datosFiscales;
    }

    public void setDatosFiscales(DatosFiscales datosFiscales) {
        this.datosFiscales = datosFiscales;
    }
}
