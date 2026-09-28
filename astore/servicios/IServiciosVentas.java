package astore.servicios;

import astore.seguridad.Usuario;
import astore.ventas.Orden;

public interface IServiciosVentas {

    Orden confirmarVenta(Object... args);

    void anularVenta(Orden orden, Usuario usuario);
}
