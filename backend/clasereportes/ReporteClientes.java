package clasereportes;

import claseseguridad.Cliente;

import java.util.ArrayList;
import java.util.List;

public class ReporteClientes extends Reporte {

    private List<Cliente> clientes = new ArrayList<>();

    public ReporteClientes() {
    }

    @Override
    protected String generarCuerpo() {
        return null;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
}
