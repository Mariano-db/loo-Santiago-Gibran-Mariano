package Servicios;

import Ventas.EstadoPedido;
import Ventas.Pedido;
import claseimpresion.EstadoRecogida;
import claseimpresion.Recogida;

import java.time.LocalDateTime;

public class ServicioRecogidas {

    public static final int DIAS_PARA_RECOGER = 7;

    private final RepositorioRecogidaJson recogidas;
    private final IRepositorio<Pedido, Long> pedidos;

    public ServicioRecogidas(RepositorioRecogidaJson recogidas, IRepositorio<Pedido, Long> pedidos) {
        this.recogidas = recogidas;
        this.pedidos = pedidos;
    }

    public Recogida crearParaPedido(Pedido pedido) {
        Recogida r = new Recogida();
        r.setPedido(pedido);
        r.setEstado(EstadoRecogida.PENDIENTE);
        r.setFechaLimite(LocalDateTime.now().plusDays(DIAS_PARA_RECOGER));
        recogidas.guardar(r);
        return r;
    }

    public void marcarLista(Recogida r) {
        r.marcarLista();
        r.setFechaLimite(LocalDateTime.now().plusDays(DIAS_PARA_RECOGER));
        r.getPedido().setEstado(EstadoPedido.LISTO_PARA_RECOGER);
        pedidos.guardar(r.getPedido());
        recogidas.guardar(r);
    }

    public void registrarEntrega(Recogida r) {
        r.registrarEntrega();
        r.getPedido().setEstado(EstadoPedido.ENTREGADO);
        pedidos.guardar(r.getPedido());
        recogidas.guardar(r);
    }
}
