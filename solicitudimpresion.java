public class solicitudimpresion {
    private final Long id;
    private final String nombre;
    private final int cantidad;
    private final String modalidadDesde;
    private estadoimpresion estado;

    public solicitudimpresion(Long id, String nombre, int cantidad, 
                             String modalidadDesde, estadoimpresion estado) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.modalidadDesde = modalidadDesde;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getModalidadDesde() {
        return modalidadDesde;
    }

    public estadoimpresion getEstado() {
        return estado;
    }

    public void cambiarEstado(estadoimpresion nuevoEstado) {
        this.estado = nuevoEstado;
    }
}