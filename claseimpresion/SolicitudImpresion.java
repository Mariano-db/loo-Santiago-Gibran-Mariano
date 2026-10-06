package claseimpresion;

import claseseguridad.Cliente;

import java.time.LocalDateTime;

public class SolicitudImpresion {

    public static final int MAX_COPIAS = 500;

    private Long id;
    private Cliente cliente;
    private String archivo;
    private int cantidad;
    private String especificaciones;
    private EstadoImpresion estado;
    private LocalDateTime fecha;

    public SolicitudImpresion() {
    }

    public void enviar() {
        if (archivo == null || archivo.isBlank() || archivo.length() > 200) {
            throw new IllegalArgumentException("El nombre del archivo es obligatorio (maximo 200 caracteres).");
        }
        if (cantidad < 1 || cantidad > MAX_COPIAS) {
            throw new IllegalArgumentException("La cantidad de copias debe estar entre 1 y " + MAX_COPIAS + ".");
        }
        this.estado = EstadoImpresion.PENDIENTE;
        this.fecha = LocalDateTime.now();
    }

    public void cambiarEstado(EstadoImpresion nuevo) {
        boolean permitido;
        switch (estado) {
            case PENDIENTE:
                permitido = nuevo == EstadoImpresion.EN_PROCESO || nuevo == EstadoImpresion.CANCELADA;
                break;
            case EN_PROCESO:
                permitido = nuevo == EstadoImpresion.TERMINADA || nuevo == EstadoImpresion.CANCELADA;
                break;
            case TERMINADA:
                permitido = nuevo == EstadoImpresion.ENTREGADA;
                break;
            default:
                permitido = false;
        }
        if (!permitido) {
            throw new IllegalStateException("No se puede pasar de " + estado + " a " + nuevo + ".");
        }
        this.estado = nuevo;
    }

    public void cancelar() {
        cambiarEstado(EstadoImpresion.CANCELADA);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
