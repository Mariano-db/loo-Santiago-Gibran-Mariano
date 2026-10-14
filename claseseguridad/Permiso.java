package claseseguridad;

import java.util.Objects;

public class Permiso {

    public static final String GESTIONAR_USUARIOS = "GESTIONAR_USUARIOS";
    public static final String GESTIONAR_PRODUCTOS = "GESTIONAR_PRODUCTOS";
    public static final String GESTIONAR_CUPONES = "GESTIONAR_CUPONES";
    public static final String VER_VENTAS = "VER_VENTAS";

    private long id;
    private String codigo;
    private String descripcion;

    public Permiso() {
    }

    public Permiso(String codigo) {
        this.codigo = codigo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permiso)) {
            return false;
        }
        return Objects.equals(codigo, ((Permiso) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
