package Api;

import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Exepciones.CarritoVacioException;
import Exepciones.PagoInvalidoException;
import Exepciones.StockInsuficienteException;
import Ventas.Pedido;
import clasecarrostotal.CarritoCompra;
import clasedescuentos.Cupon;
import clasepagos.EstrategiaPago;
import clasepagos.PagoEfectivo;
import clasepagos.PagoPaypal;
import clasepagos.PagoTarjeta;
import clasepagos.PagoTransferencia;
import claseseguridad.Cliente;
import claseseguridad.TipoCliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControladorCarrito {

    private final ContextoApi contexto;

    public ControladorCarrito(ContextoApi contexto) {
        this.contexto = contexto;
    }

    public Respuesta ver(Peticion peticion) {
        return Respuesta.ok(Mapeadores.carrito(peticion.sesionRequerida().getCarrito()));
    }

    public Respuesta agregar(Peticion peticion) {
        CarritoCompra carrito = peticion.sesionRequerida().getCarrito();
        long productoId = peticion.largo("productoId") == null
                ? fallar("El campo 'productoId' es obligatorio") : peticion.largo("productoId");
        Integer cantidadIndicada = peticion.entero("cantidad");
        int cantidad = cantidadIndicada == null ? 1 : cantidadIndicada;
        if (cantidad <= 0) {
            throw new ApiException(400, "La cantidad debe ser mayor a cero");
        }

        Producto producto = contexto.productos.buscarPorId(productoId)
                .filter(Producto::isActivo)
                .orElseThrow(() -> new ApiException(404, "No existe el producto " + productoId));

        VarianteProducto variante = null;
        Long varianteId = peticion.largo("varianteId");
        if (producto.getVariantes().isEmpty()) {
            if (varianteId != null) {
                throw new ApiException(400, "Este producto no tiene variantes");
            }
        } else {
            if (varianteId == null) {
                throw new ApiException(400, "Este producto requiere elegir una variante (varianteId)");
            }
            for (VarianteProducto candidata : producto.getVariantes()) {
                if (varianteId.equals(candidata.getId())) {
                    variante = candidata;
                    break;
                }
            }
            if (variante == null) {
                throw new ApiException(404, "La variante " + varianteId + " no pertenece a ese producto");
            }
        }

        try {
            carrito.agregarProducto(producto, variante, cantidad);
        } catch (StockInsuficienteException e) {
            throw new ApiException(409, e.getMessage());
        }
        return Respuesta.ok(Mapeadores.carrito(carrito));
    }

    public Respuesta cambiarCantidad(Peticion peticion) {
        CarritoCompra carrito = peticion.sesionRequerida().getCarrito();
        int indice = indiceValido(peticion, carrito);
        int cantidad = peticion.enteroObligatorio("cantidad");
        try {
            carrito.actualizarCantidad(indice, cantidad);
        } catch (StockInsuficienteException e) {
            throw new ApiException(409, e.getMessage());
        }
        return Respuesta.ok(Mapeadores.carrito(carrito));
    }

    public Respuesta eliminar(Peticion peticion) {
        CarritoCompra carrito = peticion.sesionRequerida().getCarrito();
        carrito.eliminarProducto(indiceValido(peticion, carrito));
        return Respuesta.ok(Mapeadores.carrito(carrito));
    }

    public Respuesta vaciar(Peticion peticion) {
        Sesion sesion = peticion.sesionRequerida();
        sesion.reiniciarCarrito();
        return Respuesta.ok(Mapeadores.carrito(sesion.getCarrito()));
    }

    public Respuesta aplicarCupon(Peticion peticion) {
        CarritoCompra carrito = peticion.sesionRequerida().getCarrito();
        String codigo = peticion.textoObligatorio("codigo");
        Cupon cupon = contexto.cupones.buscarPorCodigo(codigo)
                .orElseThrow(() -> new ApiException(404, "El cupon " + codigo + " no existe"));
        try {
            carrito.aplicarCupon(cupon);
        } catch (IllegalArgumentException e) {
            throw new ApiException(409, e.getMessage());
        }
        return Respuesta.ok(Mapeadores.carrito(carrito));
    }

    public Respuesta quitarCupon(Peticion peticion) {
        CarritoCompra carrito = peticion.sesionRequerida().getCarrito();
        carrito.quitarCupon();
        return Respuesta.ok(Mapeadores.carrito(carrito));
    }

    public Respuesta checkout(Peticion peticion) {
        Sesion sesion = peticion.sesionRequerida();
        if (!(sesion.getUsuario() instanceof Cliente)) {
            throw new ApiException(403, "Solo las cuentas de cliente pueden comprar");
        }
        CarritoCompra carrito = sesion.getCarrito();
        EstrategiaPago pago = construirPago(peticion);

        Pedido pedido;
        try {
            pedido = contexto.servicioPedidos.finalizarCompra(carrito, pago);
        } catch (CarritoVacioException e) {
            throw new ApiException(409, e.getMessage());
        } catch (StockInsuficienteException e) {
            throw new ApiException(409, e.getMessage());
        } catch (PagoInvalidoException e) {
            throw new ApiException(402, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ApiException(409, e.getMessage());
        }
        sesion.reiniciarCarrito();

        Map<String, Object> cuerpo = Mapeadores.pedido(pedido);
        if (pago instanceof PagoEfectivo) {
            cuerpo.put("cambio", ((PagoEfectivo) pago).getCambio());
        }
        return Respuesta.creado(cuerpo);
    }

    public Respuesta misPedidos(Peticion peticion) {
        Sesion sesion = peticion.sesionRequerida();
        boolean registrado = sesion.getUsuario() instanceof Cliente
                && ((Cliente) sesion.getUsuario()).getTipo() == TipoCliente.REGISTRADO
                && sesion.getUsuario().getId() != 0;
        if (!registrado) {
            throw new ApiException(403, "Inicia sesion con una cuenta de cliente para ver tus pedidos");
        }
        List<Object> resultado = new ArrayList<>();
        for (Pedido pedido : contexto.servicioPedidos.pedidosDeCliente(sesion.getUsuario().getId())) {
            resultado.add(Mapeadores.pedido(pedido));
        }
        return Respuesta.ok(resultado);
    }

    private EstrategiaPago construirPago(Peticion peticion) {
        String metodo = peticion.textoObligatorio("metodoPago").toUpperCase();
        switch (metodo) {
            case "TARJETA":
                PagoTarjeta tarjeta = new PagoTarjeta();
                tarjeta.setDatosTarjeta(peticion.textoObligatorio("numeroTarjeta"),
                        peticion.enteroObligatorio("mesVencimiento"),
                        peticion.enteroObligatorio("anioVencimiento"),
                        peticion.textoObligatorio("cvv"));
                return tarjeta;
            case "TRANSFERENCIA":
                PagoTransferencia transferencia = new PagoTransferencia();
                transferencia.setReferenciaBancaria(peticion.textoObligatorio("referenciaBancaria"));
                return transferencia;
            case "PAYPAL":
                PagoPaypal paypal = new PagoPaypal();
                paypal.setCuentaPaypal(peticion.textoObligatorio("cuentaPaypal"));
                return paypal;
            case "EFECTIVO":
                PagoEfectivo efectivo = new PagoEfectivo();
                efectivo.setMontoRecibido(peticion.numeroObligatorio("montoRecibido"));
                return efectivo;
            default:
                throw new ApiException(400, "Metodo de pago invalido: " + metodo
                        + " (usa TARJETA, TRANSFERENCIA, PAYPAL o EFECTIVO)");
        }
    }

    private int indiceValido(Peticion peticion, CarritoCompra carrito) {
        int indice = peticion.indiceRuta("indice");
        if (indice >= carrito.getItems().size()) {
            throw new ApiException(404, "No existe el item " + indice + " en el carrito");
        }
        return indice;
    }

    private static long fallar(String mensaje) {
        throw new ApiException(400, mensaje);
    }
}
