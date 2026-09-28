package astore.servicios;

import astore.pagos.Estrategiapago;
import astore.ventas.Orden;

public interface IServiciosPagos {

    void procesarPago(Orden orden, Estrategiapago estrategiaPago, double monto);
}
