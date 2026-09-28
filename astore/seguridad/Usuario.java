package astore.seguridad;

import java.util.Set;

public abstract class Usuario {

    private Long id;
    private String nombre;
    private String username;
    private String email;
    private String passwordHash;
    private String telefono;
    private Rol rol;
    private Set<Permiso> permisos;
    private boolean activo;

    public Usuario() {
    }

    public boolean autenticar(String password) {
        return false;
    }

    public void verificarPermiso(Permiso permiso) {
    }

    public boolean tienePermiso(Permiso permiso) {
        return false;
    }

    public void asignarPermiso(Permiso permiso) {
    }

    public boolean iniciarSesion() {
        return false;
    }

    public void cerrarSesion() {
    }

    public void actualizarPerfil() {
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
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
