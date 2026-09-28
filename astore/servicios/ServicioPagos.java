package astore.servicios;

import astore.pagos.Estrategiapago;
import astore.ventas.Orden;

public class ServicioPagos implements IServiciosPagos {

    public ServicioPagos() {
    }

    @Override
    public void procesarPago(Orden orden, Estrategiapago estrategiaPago, double monto) {
    }
}
