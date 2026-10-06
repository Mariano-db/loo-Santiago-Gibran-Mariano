package Panel;

import Exepciones.CarritoVacioException;
import Exepciones.PagoInvalidoException;
import Exepciones.StockInsuficienteException;
import Servicios.Almacen;
import Ventas.DetallePedido;
import Ventas.Pedido;
import clasecarrostotal.CarritoCompra;
import clasecarrostotal.ItemCarrito;
import clasedescuentos.Cupon;
import clasepagos.Pago;
import clasepagos.PagoEfectivo;
import clasepagos.PagoPaypal;
import clasepagos.PagoTarjeta;
import clasepagos.PagoTransferencia;
import clasepagos.TipoTarjeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProcesoCompra {

    private final Entrada in;
    private final Almacen almacen;
    private final CarritoCompra carrito;

    public ProcesoCompra(Entrada in, Almacen almacen, CarritoCompra carrito) {
        this.in = in;
        this.almacen = almacen;
        this.carrito = carrito;
    }

    public boolean ejecutar() {
        try {
            carrito.validarNoVacio();
            validarStock();
        } catch (CarritoVacioException e) {
            System.out.println("No se puede finalizar la compra: " + e.getMessage());
            return false;
        } catch (StockInsuficienteException e) {
            System.out.println(e.getMessage());
            System.out.println("Ajusta tu carrito e intenta de nuevo. No se te cobro nada.");
            return false;
        }

        Cupon cuponUsado = revalidarCupon();
        double total = carrito.totalConDescuento();
        System.out.printf("Total a pagar: $%.2f MXN%n", total);

        Pago pago = cobrar(total);
        if (pago == null) {
            System.out.println("Compra cancelada. Tu carrito sigue igual.");
            return false;
        }
        return registrarPedido(pago, cuponUsado);
    }

    private void validarStock() throws StockInsuficienteException {
        List<DetallePedido> lineas = new ArrayList<>();
        for (ItemCarrito item : carrito.getItems()) {
            DetallePedido linea = new DetallePedido();
            linea.setProducto(item.getProducto());
            linea.setVariante(item.getVariante());
            linea.setCantidad(item.getCantidad());
            lineas.add(linea);
        }
        almacen.servicioInventario.validarDisponibilidad(lineas);
    }

    private Cupon revalidarCupon() {
        if (carrito.getCuponAplicado() == null) {
            return null;
        }
        Optional<Cupon> actual = almacen.cupones.buscarPorCodigo(carrito.getCuponAplicado().getCodigo());
        String motivo = actual.isEmpty() ? "El cupon ya no existe." : actual.get().motivoNoAplicable(carrito.calcularTotal());
        if (motivo != null) {
            carrito.quitarCupon();
            System.out.println("Se quito el cupon: " + motivo);
            return null;
        }
        carrito.aplicarCupon(actual.get());
        return actual.get();
    }

    private Pago cobrar(double total) {
        while (true) {
            System.out.println("Metodo de pago:");
            System.out.println("1. Tarjeta");
            System.out.println("2. PayPal");
            System.out.println("3. Transferencia");
            System.out.println("4. Efectivo (pago al recoger)");
            System.out.println("0. Cancelar");
            String opcion = in.texto("Elige una opcion: ");
            if (opcion.equals("0")) {
                return null;
            }
            Pago pago = pedirDatosDePago(opcion);
            if (pago != null) {
                try {
                    return almacen.servicioPagos.cobrar(pago, total);
                } catch (PagoInvalidoException e) {
                    System.out.println("Pago rechazado: " + e.getMessage());
                }
            }
            System.out.println();
        }
    }

    private Pago pedirDatosDePago(String opcion) {
        switch (opcion) {
            case "1": {
                PagoTarjeta pago = new PagoTarjeta();
                pago.setTipoTarjeta(in.texto("Tipo (1 = debito, 2 = credito): ").equals("2") ? TipoTarjeta.CREDITO : TipoTarjeta.DEBITO);
                String numero = in.texto("Numero de tarjeta: ");
                String vencimiento = in.texto("Vencimiento (MM/AA): ");
                String cvv = in.texto("CVV: ");
                pago.setDatosTarjeta(numero, vencimiento, cvv);
                return pago;
            }
            case "2": {
                PagoPaypal pago = new PagoPaypal();
                pago.setCuentaPaypal(in.texto("Correo de tu cuenta PayPal: "));
                return pago;
            }
            case "3": {
                PagoTransferencia pago = new PagoTransferencia();
                pago.setReferenciaBancaria(in.texto("Referencia / clave de rastreo de la transferencia: "));
                return pago;
            }
            case "4": {
                PagoEfectivo pago = new PagoEfectivo();
                Double monto = in.decimal("Monto con el que pagaras: ");
                pago.setMontoRecibido(monto == null ? -1 : monto);
                return pago;
            }
            default:
                System.out.println("Opcion invalida.");
                return null;
        }
    }

    private boolean registrarPedido(Pago pago, Cupon cuponUsado) {
        try {
            Pedido pedido = carrito.convertirAPedido();
            almacen.servicioPagos.registrarPagoEnPedido(pedido, pago);
            almacen.pedidos.guardar(pedido);
            almacen.servicioInventario.confirmarSalida(pedido);
            almacen.servicioRecogidas.crearParaPedido(pedido);
            if (cuponUsado != null && pedido.getCuponCodigo() != null) {
                cuponUsado.registrarUso();
                almacen.cupones.guardar(cuponUsado);
            }
            imprimirResumen(pedido, pago);
            return true;
        } catch (CarritoVacioException | StockInsuficienteException e) {

            System.out.println("Error despues del pago, avisa a la tienda: " + e.getMessage());
            return false;
        }
    }

    private void imprimirResumen(Pedido pedido, Pago pago) {
        if (pago instanceof PagoEfectivo && ((PagoEfectivo) pago).getCambio() > 0) {
            System.out.printf("Cambio: $%.2f MXN%n", ((PagoEfectivo) pago).getCambio());
        }
        System.out.println("Pago aprobado (" + pedido.getMetodoPago() + ", ref: " + pedido.getReferenciaPago() + ").");
        System.out.println("Pedido #" + pedido.getId() + " generado y guardado correctamente.");
        if (pedido.getDescuento() > 0) {
            System.out.printf("Descuento (cupon %s): -$%.2f MXN%n", pedido.getCuponCodigo(), pedido.getDescuento());
        }
        System.out.printf("Total del pedido: $%.2f MXN%n", pedido.getTotal());
        System.out.println("Productos:");
        for (DetallePedido detalle : pedido.getDetalles()) {
            System.out.printf("  - %s%s x%d - $%.2f%n", detalle.getProducto().getNombre(),
                    Descripciones.variante(detalle.getVariante()), detalle.getCantidad(), detalle.getSubtotal());
        }
    }
}
