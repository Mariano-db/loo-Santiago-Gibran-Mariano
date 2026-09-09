package com.anahuac.astore.seguridad;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;


public class Rol {

    public final String id;
    public String nombre;
    public String descripcion;
    public final Set<Permiso> permisos = EnumSet.noneOf(Permiso.class);

    public Rol(String id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Set<Permiso> getPermisos() {
        return Collections.unmodifiableSet(permisos);
    }

    public static Rol cliente() {
        Rol r = new Rol("R-CLI", "CLIENTE", "Compras y consulta");
        return r;
    }

    public static Rol administrador() {
        Rol r = new Rol("R-ADMIN", "ADMINISTRADOR", "Acceso total al sistema");
        r.permisos.addAll(EnumSet.allOf(Permiso.class));
        return r;
    }

    public static Rol gerente() {
        Rol r = new Rol("R-GER", "GERENTE", "Gestion y reportes");
        r.permisos.addAll(EnumSet.of(Permiso.GESTIONAR_PRODUCTOS, Permiso.GENERAR_REPORTES,
                                     Permiso.APLICAR_DESCUENTO, Permiso.ANULAR_ORDEN));
        return r;
    }

    public static Rol vendedor() {
        Rol r = new Rol("R-VEN", "VENDEDOR", "Registro de ventas");
        r.permisos.addAll(EnumSet.of(Permiso.REGISTRAR_VENTA, Permiso.APLICAR_DESCUENTO));
        return r;
    }

    public static Rol cajero() {
        Rol r = new Rol("R-CAJ", "CAJERO", "Cobro de ventas");
        r.permisos.addAll(EnumSet.of(Permiso.REGISTRAR_VENTA));
        return r;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
