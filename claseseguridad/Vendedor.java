package claseseguridad;

import Ventas.Orden;
import Ventas.Pedido;

import java.util.ArrayList;
import java.util.List;

public class Vendedor extends Usuario {

    private String turno;

    public Vendedor() {
    }

    public Orden registrarVenta() {
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
