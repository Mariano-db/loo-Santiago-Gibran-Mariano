package Servicios;

import claseseguridad.Usuario;
import Ventas.Orden;

public interface IServiciosVentas {

    Orden confirmarVenta(Object... args);

    void anularVenta(Orden orden, Usuario usuario);
}
