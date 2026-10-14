package Servicios;

import Exepciones.PagoInvalidoException;
import clasepagos.EstrategiaPago;
import Ventas.Orden;

public class ServicioPagos implements IServiciosPagos {

    public ServicioPagos() {
    }

    @Override
    public void procesarPago(Orden orden, EstrategiaPago estrategiaPago, double monto) {
    }

    @Override
    public void procesarPago(EstrategiaPago estrategiaPago, double monto) throws PagoInvalidoException {
        if (estrategiaPago == null) {
            throw new PagoInvalidoException("No se eligio un metodo de pago");
        }
        if (!estrategiaPago.procesar(monto)) {
            throw new PagoInvalidoException("El pago por " + estrategiaPago.tipo() + " fue rechazado: datos invalidos o monto insuficiente");
        }
    }
}
