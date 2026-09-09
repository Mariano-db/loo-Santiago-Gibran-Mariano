package com.anahuac.astore.seguridad;

import java.util.EnumSet;
import java.util.Set;


public class Cliente extends Usuario {

    public TipoCliente tipo;
    public int puntosFidelidad;

    public Cliente(String id, String nombre, String username, String email,
                   String password, TipoCliente tipo) {
        super(id, nombre, username, email, password, Rol.cliente());
        this.tipo = tipo;
        this.puntosFidelidad = 0;
    }

    @Override
    public String descripcionRol() {
        return "Cliente " + tipo + " - compra productos y consulta su historial";
    }

    @Override
    public Set<Permiso> permisosBase() {
        return EnumSet.noneOf(Permiso.class);
    }

    public void acumularPuntos(double montoGastado) {
        this.puntosFidelidad += (int) (montoGastado / 20);
    }

    public int getPuntosFidelidad() { return puntosFidelidad; }
    public TipoCliente getTipo() { return tipo; }
    public void setTipo(TipoCliente tipo) { this.tipo = tipo; }
}
