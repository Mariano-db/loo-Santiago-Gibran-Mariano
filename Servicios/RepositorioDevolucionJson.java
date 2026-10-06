package Servicios;

import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Ventas.Devolucion;
import Ventas.Pedido;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositorioDevolucionJson extends RepositorioJsonBase<Devolucion> {

    private final IRepositorio<Pedido, Long> repositorioPedidos;

    public RepositorioDevolucionJson(String ruta, IRepositorio<Pedido, Long> repositorioPedidos) {
        super(ruta);
        this.repositorioPedidos = repositorioPedidos;
    }

    public List<Devolucion> listarPorPedido(Long pedidoId) {
        return listarTodos().stream()
                .filter(d -> d.getPedidoOrigen() != null && d.getPedidoOrigen().getId().equals(pedidoId))
                .collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> aMapa(Devolucion d) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", d.getId());
        m.put("pedidoId", d.getPedidoOrigen().getId());
        m.put("productoId", d.getProducto().getId());
        m.put("producto", d.getProducto().getNombre());
        m.put("varianteId", d.getVariante() == null ? null : d.getVariante().getId());
        m.put("cantidad", d.getCantidad());
        m.put("motivo", d.getMotivo());
        m.put("fecha", comoTexto(d.getFecha()));
        m.put("montoReembolsado", d.getMontoReembolsado());
        return m;
    }

    @Override
    protected Devolucion deMapa(Map<?, ?> m) {
        Devolucion d = new Devolucion();
        d.setId(entero(m, "id"));
        repositorioPedidos.buscarPorId(entero(m, "pedidoId")).ifPresent(d::setPedidoOrigen);
        Producto p = new Producto();
        p.setId(entero(m, "productoId"));
        p.setNombre(texto(m, "producto"));
        d.setProducto(p);
        Long varianteId = entero(m, "varianteId");
        if (varianteId != null) {
            VarianteProducto v = new VarianteProducto();
            v.setId(varianteId);
            d.setVariante(v);
        }
        d.setCantidad(intOr0(m, "cantidad"));
        d.setMotivo(texto(m, "motivo"));
        d.setFecha(fechaHora(m, "fecha"));
        d.setMontoReembolsado(decimal(m, "montoReembolsado"));
        return d;
    }

    @Override
    protected Long idDe(Devolucion d) {
        return d.getId();
    }

    @Override
    protected void asignarId(Devolucion d, long id) {
        d.setId(id);
    }
}
