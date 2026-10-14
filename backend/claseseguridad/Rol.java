package claseseguridad;

import java.util.HashSet;
import java.util.Set;

public class Rol {

    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String CLIENTE = "CLIENTE";

    private long id;
    private String nombre;
    private String descripcion;
    private Set<Permiso> permisos = new HashSet<>();

    public Rol() { }
            public static Rol crearPorNombre(String nombre) {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        if ("ADMINISTRADOR".equals(nombre)) {
            rol.agregarPermiso(new Permiso(Permiso.GESTIONAR_PRODUCTOS));
            rol.agregarPermiso(new Permiso(Permiso.GESTIONAR_USUARIOS));
            rol.agregarPermiso(new Permiso(Permiso.GESTIONAR_CUPONES));
            rol.agregarPermiso(new Permiso(Permiso.VER_VENTAS));
        } else if ("VENDEDOR".equals(nombre)) {
            rol.agregarPermiso(new Permiso(Permiso.VER_VENTAS));
        }
        return rol;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
