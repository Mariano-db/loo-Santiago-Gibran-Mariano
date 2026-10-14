package Servicios;

import Exepciones.PagoInvalidoException;
import clasepagos.EstrategiaPago;
import Ventas.Orden;

public interface IServiciosPagos {

    void procesarPago(Orden orden, EstrategiaPago estrategiaPago, double monto);

    void procesarPago(EstrategiaPago estrategiaPago, double monto) throws PagoInvalidoException;
}
