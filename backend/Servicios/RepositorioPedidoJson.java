package Servicios;

import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Json.JsonEscritor;
import Json.JsonLector;
import Ventas.DetallePedido;
import Ventas.EstadoPedido;
import Ventas.Pedido;
import claseseguridad.Cliente;
import claseseguridad.TipoCliente;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioPedidoJson implements IRepositorio<Pedido, Long> {

    private final String rutaArchivo;

    public RepositorioPedidoJson(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public void guardar(Pedido entidad) {
        List<Pedido> pedidos = listarTodos();
        if (entidad.getId() == null) {
            long siguienteId = pedidos.stream().mapToLong(Pedido::getId).max().orElse(0L) + 1;
            entidad.setId(siguienteId);
            pedidos.add(entidad);
        } else {
            boolean reemplazado = false;
            for (int i = 0; i < pedidos.size(); i++) {
                if (pedidos.get(i).getId().equals(entidad.getId())) {
                    pedidos.set(i, entidad);
                    reemplazado = true;
                    break;
                }
            }
            if (!reemplazado) {
                pedidos.add(entidad);
            }
        }
        escribir(pedidos);
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return listarTodos().stream().filter(p -> p.getId().equals(id)).findFirst();
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
    public List<Pedido> listarTodos() {
        if (!Files.exists(Path.of(rutaArchivo))) {
            return new ArrayList<>();
        }
        try {
            Object raiz = JsonLector.parsearArchivo(rutaArchivo);
            List<Pedido> pedidos = new ArrayList<>();
            for (Object item : (List<?>) raiz) {
                pedidos.add(aPedido((Map<?, ?>) item));
            }
            return pedidos;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + rutaArchivo, e);
        }
    }

    @Override
    public void eliminar(Long id) {
        List<Pedido> pedidos = listarTodos();
        pedidos.removeIf(p -> p.getId().equals(id));
        escribir(pedidos);
    }

    private void escribir(List<Pedido> pedidos) {
        List<Object> lista = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            lista.add(aMapa(pedido));
        }
        try {
            JsonEscritor.escribirArchivo(rutaArchivo, lista);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir " + rutaArchivo, e);
        }
    }

    private Map<String, Object> aMapa(Pedido pedido) {
        Cliente cliente = pedido.getCliente();
        boolean registrado = cliente != null && cliente.getId() != 0;

        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", pedido.getId());
        mapa.put("clienteId", registrado ? cliente.getId() : null);
        mapa.put("clienteNombre", cliente == null ? null : cliente.getNombre());
        mapa.put("fecha", pedido.getFecha() == null ? null : pedido.getFecha().toString());
        mapa.put("estado", pedido.getEstado() == null ? null : pedido.getEstado().name());
        mapa.put("total", pedido.getTotal());
        mapa.put("descuento", pedido.getDescuento());
        mapa.put("cuponCodigo", pedido.getCuponCodigo());
        mapa.put("metodoPago", pedido.getMetodoPago());
        mapa.put("referenciaPago", pedido.getReferenciaPago());

        List<Object> detalles = new ArrayList<>();
        for (DetallePedido detalle : pedido.getDetalles()) {
            detalles.add(aMapaDetalle(detalle));
        }
        mapa.put("detalles", detalles);
        return mapa;
    }

    private Map<String, Object> aMapaDetalle(DetallePedido detalle) {
        Producto producto = detalle.getProducto();
        VarianteProducto variante = detalle.getVariante();

        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("productoId", producto == null ? null : producto.getId());
        mapa.put("productoSku", producto == null ? null : producto.getSku());
        mapa.put("productoNombre", producto == null ? null : producto.getNombre());
        mapa.put("varianteId", variante == null ? null : variante.getId());
        mapa.put("varianteSku", variante == null ? null : variante.getSku());
        mapa.put("color", variante == null ? null : variante.getColor());
        mapa.put("talla", variante == null ? null : variante.getTalla());
        mapa.put("cantidad", detalle.getCantidad());
        mapa.put("precioUnitario", detalle.getPrecioUnitario());
        mapa.put("subtotal", detalle.getSubtotal());
        return mapa;
    }

    private Pedido aPedido(Map<?, ?> mapa) {
        Pedido pedido = new Pedido();
        pedido.setId(numeroALong(mapa.get("id")));

        Cliente cliente = new Cliente();
        Long clienteId = numeroALong(mapa.get("clienteId"));
        cliente.setId(clienteId == null ? 0 : clienteId);
        cliente.setNombre((String) mapa.get("clienteNombre"));
        cliente.setTipo(clienteId == null ? TipoCliente.INVITADO : TipoCliente.REGISTRADO);
        pedido.setCliente(cliente);

        Object fecha = mapa.get("fecha");
        if (fecha != null) {
            pedido.setFecha(LocalDateTime.parse((String) fecha));
        }
        Object estado = mapa.get("estado");
        if (estado != null) {
            pedido.setEstado(EstadoPedido.valueOf((String) estado));
        }
        pedido.setTotal(numeroADouble(mapa.get("total")));
        pedido.setDescuento(numeroADouble(mapa.get("descuento")));
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
        producto.setId(numeroALong(mapa.get("productoId")));
        producto.setSku((String) mapa.get("productoSku"));
        producto.setNombre((String) mapa.get("productoNombre"));
        detalle.setProducto(producto);

        Long varianteId = numeroALong(mapa.get("varianteId"));
        if (varianteId != null) {
            VarianteProducto variante = new VarianteProducto();
            variante.setId(varianteId);
            variante.setSku((String) mapa.get("varianteSku"));
            variante.setColor((String) mapa.get("color"));
            variante.setTalla((String) mapa.get("talla"));
            detalle.setVariante(variante);
        }

        detalle.setCantidad(numeroADouble(mapa.get("cantidad")).intValue());
        detalle.setPrecioUnitario(numeroADouble(mapa.get("precioUnitario")));
        detalle.setSubtotal(numeroADouble(mapa.get("subtotal")));
        return detalle;
    }

    private Long numeroALong(Object valor) {
        return valor == null ? null : ((Number) valor).longValue();
    }

    private Double numeroADouble(Object valor) {
        return valor == null ? 0.0 : ((Number) valor).doubleValue();
    }
}
