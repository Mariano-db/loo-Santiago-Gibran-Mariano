package claseseguridad;

import Exepciones.AccesoDenegadoException;
import Utils.HashUtil;
import java.util.HashSet;
import java.util.Set;

public abstract class Usuario {

    private long id;
    private String nombre;
    private String username;
    private String email;
    private String passwordHash;
    private String sal;
    private String telefono;
    private Rol rol;
    private Set<Permiso> permisos = new HashSet<>();
    private boolean activo;
    private boolean sesionActiva;

    public Usuario() {
    }

    public void establecerPassword(String password) {
        this.sal = HashUtil.generarSal();
        this.passwordHash = HashUtil.hashear(password, sal);
    }

    public boolean autenticar(String password) {
        if (password == null || passwordHash == null || sal == null) {
            return false;
        }
        return HashUtil.coinciden(HashUtil.hashear(password, sal), passwordHash);
    }

    public void verificarPermiso(Permiso permiso) {
        if (!sesionActiva) {
            throw new AccesoDenegadoException("Debes iniciar sesion para realizar esta accion");
        }
        if (!tienePermiso(permiso)) {
            throw new AccesoDenegadoException("No tienes el permiso " + permiso.getCodigo());
        }
    }

    public boolean tienePermiso(Permiso permiso) {
        if (permisos.contains(permiso)) {
            return true;
        }
        return rol != null && rol.tienePermiso(permiso);
    }

    public void asignarPermiso(Permiso permiso) {
        permisos.add(permiso);
    }

    public boolean iniciarSesion(String password) {
        if (!activo || !autenticar(password)) {
            return false;
        }
        this.sesionActiva = true;
        return true;
    }

    public void cerrarSesion() {
        this.sesionActiva = false;
    }

    public boolean isSesionActiva() {
        return sesionActiva;
    }

    public String getSal() {
        return sal;
    }

    public void setSal(String sal) {
        this.sal = sal;
    }

    public void actualizarPerfil() {
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
