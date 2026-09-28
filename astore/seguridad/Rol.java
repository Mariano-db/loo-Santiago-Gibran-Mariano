package astore.seguridad;

import java.util.HashSet;
import java.util.Set;

public class Rol {

    private Long id;
    private String nombre;
    private String descripcion;
    private Set<Permiso> permisos = new HashSet<>();

    // valores tipicos: ADMINISTRADOR, GERENTE, VENDEDOR, CAJERO

    public Rol() {
    }

    public void agregarPermiso(Permiso permiso) {
        permisos.add(permiso);
    }

    public void quitarPermiso(Permiso permiso) {
        permisos.remove(permiso);
    }

    public boolean tienePermiso(Permiso permiso) {
        return permisos.contains(permiso);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<Permiso> permisos) {
        this.permisos = permisos;
    }
}
