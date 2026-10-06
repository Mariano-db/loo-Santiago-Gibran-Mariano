package Servicios;

import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Ventas.DetallePedido;
import Ventas.EstadoPedido;
import Ventas.Pedido;
import claseseguridad.Cliente;
import claseseguridad.Usuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioPedidoJson extends RepositorioJsonBase<Pedido> {

    private final IRepositorio<Usuario, Long> repositorioUsuarios;

    public RepositorioPedidoJson(String rutaArchivo, IRepositorio<Usuario, Long> repositorioUsuarios) {
        super(rutaArchivo);
        this.repositorioUsuarios = repositorioUsuarios;
    }

    public List<Pedido> listarPorCliente(long clienteId) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido pedido : listarTodos()) {
            if (pedido.getCliente() != null && pedido.getCliente().getId() == clienteId) {
                resultado.add(pedido);
            }
        }
        return resultado;
    }

    @Override
    protected Map<String, Object> aMapa(Pedido pedido) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", pedido.getId());
        mapa.put("clienteId", pedido.getCliente() == null ? null : pedido.getCliente().getId());
        mapa.put("fecha", pedido.getFecha() == null ? null : pedido.getFecha().toString());
        mapa.put("estado", pedido.getEstado() == null ? null : pedido.getEstado().name());
        mapa.put("total", pedido.getTotal());
        mapa.put("descuento", pedido.getDescuento());
        mapa.put("cuponCodigo", pedido.getCuponCodigo());
        mapa.put("metodoPago", pedido.getMetodoPago());
        mapa.put("referenciaPago", pedido.getReferenciaPago());

        List<Object> detalles = new ArrayList<>();
        for (DetallePedido detalle : pedido.getDetalles()) {
            Map<String, Object> d = new LinkedHashMap<>();
            Producto producto = detalle.getProducto();
            VarianteProducto variante = detalle.getVariante();
            d.put("productoId", producto == null ? null : producto.getId());
            d.put("producto", producto == null ? null : producto.getNombre());
            d.put("varianteId", variante == null ? null : variante.getId());
            d.put("sku", variante != null ? variante.getSku() : (producto == null ? null : producto.getSku()));
            d.put("color", variante == null ? null : variante.getColor());
            d.put("talla", variante == null ? null : variante.getTalla());
            d.put("cantidad", detalle.getCantidad());
            d.put("precioUnitario", detalle.getPrecioUnitario());
            d.put("subtotal", detalle.getSubtotal());
            detalles.add(d);
        }
        mapa.put("detalles", detalles);
        return mapa;
    }

    @Override
    protected Pedido deMapa(Map<?, ?> mapa) {
        Pedido pedido = new Pedido();
        pedido.setId(((Number) mapa.get("id")).longValue());

        Object clienteId = mapa.get("clienteId");
        if (clienteId != null) {
            Optional<Usuario> usuario = repositorioUsuarios.buscarPorId(((Number) clienteId).longValue());
            if (usuario.isPresent() && usuario.get() instanceof Cliente) {
                pedido.setCliente((Cliente) usuario.get());
            }
        }
        Object fecha = mapa.get("fecha");
        if (fecha != null) {
            pedido.setFecha(LocalDateTime.parse((String) fecha));
        }
        Object estado = mapa.get("estado");
        if (estado != null) {
            pedido.setEstado(EstadoPedido.valueOf((String) estado));
        }
        pedido.setTotal(((Number) mapa.get("total")).doubleValue());
        Object descuento = mapa.get("descuento");
        pedido.setDescuento(descuento == null ? 0 : ((Number) descuento).doubleValue());
        pedido.setCuponCodigo((String) mapa.get("cuponCodigo"));
        pedido.setMetodoPago((String) mapa.get("metodoPago"));
        pedido.setReferenciaPago((String) mapa.get("referenciaPago"));

        List<DetallePedido> detalles = new ArrayList<>();
        Object detallesRaw = mapa.get("detalles");
        if (detallesRaw instanceof List) {
            for (Object item : (List<?>) detallesRaw) {
                detalles.add(aDetalle((Map<?, ?>) item));
            }
        }
        pedido.setDetalles(detalles);
        return pedido;
    }

    private DetallePedido aDetalle(Map<?, ?> mapa) {
        DetallePedido detalle = new DetallePedido();

        Producto producto = new Producto();
        Object productoId = mapa.get("productoId");
        producto.setId(productoId == null ? null : ((Number) productoId).longValue());
        producto.setNombre((String) mapa.get("producto"));
        detalle.setProducto(producto);

        Object varianteId = mapa.get("varianteId");
        if (varianteId != null) {
            VarianteProducto variante = new VarianteProducto();
            variante.setId(((Number) varianteId).longValue());
            variante.setSku((String) mapa.get("sku"));
            variante.setColor((String) mapa.get("color"));
            variante.setTalla((String) mapa.get("talla"));
            detalle.setVariante(variante);
        } else {
            producto.setSku((String) mapa.get("sku"));
        }

        detalle.setCantidad(((Number) mapa.get("cantidad")).intValue());
        detalle.setPrecioUnitario(((Number) mapa.get("precioUnitario")).doubleValue());
        detalle.setSubtotal(((Number) mapa.get("subtotal")).doubleValue());
        return detalle;
    }

    @Override
    protected Long idDe(Pedido e) {
        return e.getId();
    }

    @Override
    protected void asignarId(Pedido e, long id) {
        e.setId(id);
    }
}
