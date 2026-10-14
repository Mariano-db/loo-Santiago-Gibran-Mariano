package Api;

import Catalogo.Categoria;
import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Ventas.DetallePedido;
import Ventas.Pedido;
import clasecarrostotal.CarritoCompra;
import clasecarrostotal.ItemCarrito;
import clasedescuentos.Cupon;
import claseseguridad.Cliente;
import claseseguridad.Permiso;
import claseseguridad.Usuario;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Convierte objetos del dominio en mapas listos para serializar a JSON. */
public final class Mapeadores {

    private static final String[] PERMISOS = {
        Permiso.GESTIONAR_USUARIOS, Permiso.GESTIONAR_PRODUCTOS, Permiso.GESTIONAR_CUPONES, Permiso.VER_VENTAS
    };

    private Mapeadores() {
    }

    public static Map<String, Object> categoria(Categoria categoria) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", categoria.getId());
        mapa.put("nombre", categoria.getNombre());
        mapa.put("descripcion", categoria.getDescripcion());
        return mapa;
    }

    public static Map<String, Object> variante(VarianteProducto variante) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", variante.getId());
        mapa.put("sku", variante.getSku());
        mapa.put("color", variante.getColor());
        mapa.put("talla", variante.getTalla());
        mapa.put("modelo", variante.getModelo());
        mapa.put("precioVenta", variante.getPrecioVenta());
        return mapa;
    }

    public static Map<String, Object> producto(Producto producto, boolean paraAdministrador) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", producto.getId());
        mapa.put("sku", producto.getSku());
        mapa.put("nombre", producto.getNombre());
        mapa.put("descripcion", producto.getDescripcion());
        mapa.put("precio", producto.getPrecioUnitario());
        mapa.put("stock", producto.getStock());
        mapa.put("disponible", producto.isActivo() && producto.getStock() > 0);
        mapa.put("marca", producto.getMarca());
        mapa.put("imagen", producto.getImagen());
        mapa.put("capacidad", producto.getCapacidad());
        mapa.put("dimensiones", producto.getDimensiones());
        Categoria categoria = producto.getCategoria();
        mapa.put("categoria", categoria == null ? null : categoria(categoria));
        List<Object> variantes = new ArrayList<>();
        for (VarianteProducto variante : producto.getVariantes()) {
            variantes.add(variante(variante));
        }
        mapa.put("variantes", variantes);
        if (paraAdministrador) {
            mapa.put("activo", producto.isActivo());
            mapa.put("estado", producto.getEstado() == null ? null : producto.getEstado().name());
            mapa.put("precioBase", producto.getPrecioBase());
            mapa.put("tasaImpuesto", producto.getTasaImpuesto());
        }
        return mapa;
    }

    public static Map<String, Object> usuario(Usuario usuario) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", usuario.getId() == 0 ? null : usuario.getId());
        mapa.put("nombre", usuario.getNombre());
        mapa.put("email", usuario.getEmail());
        mapa.put("rol", usuario.getRol() == null ? "INVITADO" : usuario.getRol().getNombre());
        mapa.put("activo", usuario.isActivo());
        if (usuario instanceof Cliente) {
            Cliente cliente = (Cliente) usuario;
            mapa.put("tipo", cliente.getTipo() == null ? null : cliente.getTipo().name());
        }
        List<Object> permisos = new ArrayList<>();
        for (String codigo : PERMISOS) {
            if (usuario.tienePermiso(new Permiso(codigo))) {
                permisos.add(codigo);
            }
        }
        mapa.put("permisos", permisos);
        return mapa;
    }

    public static Map<String, Object> cupon(Cupon cupon) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", cupon.getId());
        mapa.put("codigo", cupon.getCodigo());
        mapa.put("tipoDescuento", cupon.getTipoDescuento() == null ? null : cupon.getTipoDescuento().name());
        mapa.put("valor", cupon.getValor());
        mapa.put("fechaInicio", cupon.getFechaInicio() == null ? null : cupon.getFechaInicio().toString());
        mapa.put("fechaFin", cupon.getFechaFin() == null ? null : cupon.getFechaFin().toString());
        mapa.put("activo", cupon.isActivo());
        mapa.put("vigente", cupon.esValido());
        return mapa;
    }

    public static Map<String, Object> carrito(CarritoCompra carrito) {
        List<Object> items = new ArrayList<>();
        int articulos = 0;
        int indice = 0;
        for (ItemCarrito item : carrito.getItems()) {
            Map<String, Object> mapa = new LinkedHashMap<>();
            mapa.put("indice", indice++);
            mapa.put("productoId", item.getProducto().getId());
            mapa.put("sku", item.getProducto().getSku());
            mapa.put("nombre", item.getProducto().getNombre());
            VarianteProducto variante = item.getVariante();
            mapa.put("varianteId", variante == null ? null : variante.getId());
            mapa.put("color", variante == null ? null : variante.getColor());
            mapa.put("talla", variante == null ? null : variante.getTalla());
            mapa.put("cantidad", item.getCantidad());
            mapa.put("precioUnitario", item.getPrecioUnitario());
            mapa.put("subtotal", item.subtotal());
            items.add(mapa);
            articulos += item.getCantidad();
        }
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("items", items);
        mapa.put("articulos", articulos);
        mapa.put("subtotal", carrito.calcularTotal());
        mapa.put("descuento", carrito.calcularTotal() - carrito.totalConDescuento());
        mapa.put("total", carrito.totalConDescuento());
        Cupon cupon = carrito.getCupon();
        if (cupon == null) {
            mapa.put("cupon", null);
        } else {
            Map<String, Object> resumen = new LinkedHashMap<>();
            resumen.put("codigo", cupon.getCodigo());
            resumen.put("tipoDescuento", cupon.getTipoDescuento() == null ? null : cupon.getTipoDescuento().name());
            resumen.put("valor", cupon.getValor());
            mapa.put("cupon", resumen);
        }
        return mapa;
    }

    public static Map<String, Object> pedido(Pedido pedido) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", pedido.getId());
        mapa.put("fecha", pedido.getFecha() == null ? null : pedido.getFecha().toString());
        mapa.put("estado", pedido.getEstado() == null ? null : pedido.getEstado().name());
        Cliente cliente = pedido.getCliente();
        mapa.put("clienteId", cliente == null || cliente.getId() == 0 ? null : cliente.getId());
        mapa.put("clienteNombre", cliente == null ? null : cliente.getNombre());
        mapa.put("subtotal", pedido.getTotal() + pedido.getDescuento());
        mapa.put("descuento", pedido.getDescuento());
        mapa.put("total", pedido.getTotal());
        mapa.put("cuponCodigo", pedido.getCuponCodigo());
        mapa.put("metodoPago", pedido.getMetodoPago());
        mapa.put("referenciaPago", pedido.getReferenciaPago());
        List<Object> detalles = new ArrayList<>();
        for (DetallePedido detalle : pedido.getDetalles()) {
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("productoId", detalle.getProducto() == null ? null : detalle.getProducto().getId());
            d.put("nombre", detalle.getProducto() == null ? null : detalle.getProducto().getNombre());
            VarianteProducto variante = detalle.getVariante();
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

    public static Map<String, Object> mensaje(String clave, Object valor) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put(clave, valor);
        return mapa;
    }
}
