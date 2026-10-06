package Servicios;

import Ventas.Pedido;
import claseimpresion.EstadoRecogida;
import claseimpresion.Recogida;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class RepositorioRecogidaJson extends RepositorioJsonBase<Recogida> {

    private final IRepositorio<Pedido, Long> repositorioPedidos;

    public RepositorioRecogidaJson(String ruta, IRepositorio<Pedido, Long> repositorioPedidos) {
        super(ruta);
        this.repositorioPedidos = repositorioPedidos;
    }

    public Optional<Recogida> buscarPorPedido(Long pedidoId) {
        return listarTodos().stream()
                .filter(r -> r.getPedido() != null && r.getPedido().getId().equals(pedidoId))
                .findFirst();
    }

    @Override
    protected Map<String, Object> aMapa(Recogida r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.getId());
        m.put("pedidoId", r.getPedido() == null ? null : r.getPedido().getId());
        m.put("fechaLimite", comoTexto(r.getFechaLimite()));
        m.put("fechaEntrega", comoTexto(r.getFechaEntrega()));
        m.put("estado", r.getEstado().name());
        return m;
    }

    @Override
    protected Recogida deMapa(Map<?, ?> m) {
        Recogida r = new Recogida();
        r.setId(entero(m, "id"));
        Long pedidoId = entero(m, "pedidoId");
        if (pedidoId != null) {
            repositorioPedidos.buscarPorId(pedidoId).ifPresent(r::setPedido);
        }
        r.setFechaLimite(fechaHora(m, "fechaLimite"));
        r.setFechaEntrega(fechaHora(m, "fechaEntrega"));
        r.setEstado(EstadoRecogida.valueOf(texto(m, "estado")));
        return r;
    }

    @Override
    protected Long idDe(Recogida r) {
        return r.getId();
    }

    @Override
    protected void asignarId(Recogida r, long id) {
        r.setId(id);
    }
}
