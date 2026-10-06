package Servicios;

import Exepciones.PagoInvalidoException;
import Ventas.EstadoPedido;
import Ventas.Pedido;
import clasepagos.Pago;

import java.time.LocalDateTime;

public class ServicioPagos {

    public ServicioPagos() {
    }

    public Pago cobrar(Pago pago, double monto) throws PagoInvalidoException {
        if (monto <= 0) {
            throw new PagoInvalidoException("El monto a pagar debe ser mayor a cero.");
        }
        pago.setMonto(monto);
        pago.setFecha(LocalDateTime.now());
        if (pago.procesar(monto)) {
            pago.confirmar();
            return pago;
        }
        pago.cancelar();
        throw new PagoInvalidoException(pago.getMotivoRechazo() != null ? pago.getMotivoRechazo() : "Pago rechazado.");
    }

    public void registrarPagoEnPedido(Pedido pedido, Pago pago) {
        pedido.setEstado(EstadoPedido.PAGADO);
        pedido.setMetodoPago(pago.tipo());
        pedido.setReferenciaPago(pago.getReferencia());
    }
}
