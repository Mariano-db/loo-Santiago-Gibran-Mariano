package astore.servicios;

import astore.seguridad.Usuario;
import astore.ventas.Orden;

public class ServicioVentas implements IServiciosVentas {

    private IServiciosInventario servicioInventario;

    public ServicioVentas() {
    }

    @Override
    public Orden confirmarVenta(Object... args) {
        return null;
    }

    @Override
    public void anularVenta(Orden orden, Usuario usuario) {
    }

    public IServiciosInventario getServicioInventario() {
        return servicioInventario;
    }

    public void setServicioInventario(IServiciosInventario servicioInventario) {
        this.servicioInventario = servicioInventario;
    }
}
