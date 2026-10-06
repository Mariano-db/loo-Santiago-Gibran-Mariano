package claseseguridad;

import Exepciones.AccesoDenegadoException;

import java.util.HashSet;
import java.util.Set;

public abstract class Usuario {

    private long id;
    private String nombre;
    private String username;
    private String email;
    private String passwordHash;
    private String telefono;
    private Rol rol;
    private Set<Permiso> permisos = new HashSet<>();
    private boolean activo;
    private boolean sesionActiva;

    public Usuario() {
    }

    public boolean autenticar(String password) {
        return activo && Contrasenas.verificar(password, passwordHash);
    }

    public void verificarPermiso(Permiso permiso) {
        if (!tienePermiso(permiso)) {
            throw new AccesoDenegadoException("El usuario " + username + " no tiene el permiso "
                    + (permiso == null ? "(nulo)" : permiso.getCodigo()));
        }
    }

    public boolean tienePermiso(Permiso permiso) {
        if (permiso == null || !activo) {
            return false;
        }
        if (contiene(permisos, permiso)) {
            return true;
        }
        return rol != null && contiene(rol.getPermisos(), permiso);
    }

    private static boolean contiene(Set<Permiso> conjunto, Permiso buscado) {
        for (Permiso p : conjunto) {
            if (p.getCodigo() != null && p.getCodigo().equals(buscado.getCodigo())) {
                return true;
            }
        }
        return false;
    }

    public void asignarPermiso(Permiso permiso) {
        permisos.add(permiso);
    }

    public boolean iniciarSesion() {
        if (!activo) {
            return false;
        }
        sesionActiva = true;
        return true;
    }

    public void cerrarSesion() {
        sesionActiva = false;
    }

    public boolean isSesionActiva() {
        return sesionActiva;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new IllegalArgumentException("El correo no tiene un formato valido.");
        }
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Set<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<Permiso> permisos) {
        this.permisos = permisos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
