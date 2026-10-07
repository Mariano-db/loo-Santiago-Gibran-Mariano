package Servicios;

import clasepagos.EstrategiaPago;
import Ventas.Orden;

public interface IServiciosPagos {

    void procesarPago(Orden orden, EstrategiaPago estrategiaPago, double monto);
}
