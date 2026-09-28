package astore.seguridad;

public class Permiso {

    private Long id;
    private String codigo;
    private String descripcion;

    // valores tipicos: GESTIONAR_PRODUCTOS, REGISTRAR_VENTA,
    //   APLICAR_DESCUENTO, GENERAR_REPORTES, ANULAR_ORDEN

    public Permiso() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
