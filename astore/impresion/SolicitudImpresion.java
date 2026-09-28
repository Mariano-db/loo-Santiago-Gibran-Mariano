package astore.impresion;

public class SolicitudImpresion {

    private Long id;
    private String archivo;
    private int cantidad;
    private String especificaciones;
    private EstadoImpresion estado;

    public SolicitudImpresion() {
    }

    public void enviar() {
    }

    public void cambiarEstado() {
    }

    public void cancelar() {
        this.estado = EstadoImpresion.cancelada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getEspecificaciones() {
        return especificaciones;
    }

    public void setEspecificaciones(String especificaciones) {
        this.especificaciones = especificaciones;
    }

    public EstadoImpresion getEstado() {
        return estado;
    }

    public void setEstado(EstadoImpresion estado) {
        this.estado = estado;
    }
}
