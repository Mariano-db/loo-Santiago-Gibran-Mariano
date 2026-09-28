package astore.seguridad;

import java.util.ArrayList;
import java.util.List;

import astore.ventas.Pedido;
import astore.ventas.Venta;

public class Vendedor extends Usuario {

    private String turno;

    public Vendedor() {
    }

    public Venta registrarVenta() {
        return null;
    }

    public List<Pedido> consultarPedidos() {
        return new ArrayList<>();
    }

    public Cliente buscarCliente() {
        return null;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }
}
