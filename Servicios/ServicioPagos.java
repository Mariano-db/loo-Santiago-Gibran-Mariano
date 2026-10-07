package Servicios;

import clasepagos.EstrategiaPago;
import Ventas.Orden;

public class ServicioPagos implements IServiciosPagos {

    public ServicioPagos() {
    }

    @Override
    public void procesarPago(Orden orden, EstrategiaPago estrategiaPago, double monto) {
    }
}
