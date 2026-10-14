package Api;

import Catalogo.Categoria;
import Catalogo.EstadoProducto;
import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Ventas.DetallePedido;
import Ventas.EstadoPedido;
import Ventas.Pedido;
import clasedescuentos.Cupon;
import clasedescuentos.TipoDescuento;
import claseseguridad.Permiso;
import claseseguridad.Usuario;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControladorAdmin {

    private final ContextoApi contexto;

    public ControladorAdmin(ContextoApi contexto) {
        this.contexto = contexto;
    }

    // ---------- Productos ----------

    public Respuesta listarProductos(Peticion peticion) {
        peticion.usuarioConPermiso(Permiso.GESTIONAR_PRODUCTOS);
        List<Object> resultado = new ArrayList<>();
        for (Producto producto : contexto.productos.listarTodos()) {
            resultado.add(Mapeadores.producto(producto, true));
        }
        return Respuesta.ok(resultado);
    }

    public Respuesta verProducto(Peticion peticion) {
        peticion.usuarioConPermiso(Permiso.GESTIONAR_PRODUCTOS);
        return Respuesta.ok(Mapeadores.producto(productoPorId(peticion.idRuta("id")), true));
    }

    public Respuesta crearProducto(Peticion peticion) {
        peticion.usuarioConPermiso(Permiso.GESTIONAR_PRODUCTOS);

        Producto producto = new Producto();
        producto.setTasaImpuesto(0.16);
        producto.setMarca("Anahuac");
        producto.setImagen("");
        producto.setDimensiones("");
        producto.setDescripcion("");
        producto.setCapacidad("");
        producto.setEstado(EstadoProducto.ACTIVO);
        producto.setActivo(true);
        for (String obligatorio : new String[] {"nombre", "sku", "precio", "stock", "categoriaId"}) {
            if (!peticion.tiene(obligatorio) && !(obligatorio.equals("precio") && peticion.tiene("precioUnitario"))) {
                throw new ApiException(400, "El campo '" + obligatorio + "' es obligatorio");
            }
        }
        aplicarCampos(producto, peticion);
        producto.setPrecioBase(producto.getPrecioUnitario());
        contexto.productos.guardar(producto);
        return Respuesta.creado(Mapeadores.producto(producto, true));
    }

    public Respuesta editarProducto(Peticion peticion) {
        peticion.usuarioConPermiso(Permiso.GESTIONAR_PRODUCTOS);
        Producto producto = productoPorId(peticion.idRuta("id"));
        aplicarCampos(producto, peticion);
        contexto.productos.guardar(producto);
        return Respuesta.ok(Mapeadores.producto(producto, true));
    }

    public Respuesta eliminarProducto(Peticion peticion) {
        peticion.usuarioConPermiso(Permiso.GESTIONAR_PRODUCTOS);
        Producto producto = productoPorId(peticion.idRuta("id"));
        contexto.productos.eliminar(producto.getId());
        return Respuesta.ok(Mapeadores.mensaje("eliminado", producto.getId()));
    }

    private void aplicarCampos(Producto producto, Peticion peticion) {
        if (peticion.tiene("nombre")) {
            producto.setNombre(peticion.textoObligatorio("nombre"));
        }
        if (peticion.tiene("sku")) {
            String sku = peticion.textoObligatorio("sku").toUpperCase();
            for (Producto otro : contexto.productos.listarTodos()) {
                boolean mismo = producto.getId() != null && producto.getId().equals(otro.getId());
                if (!mismo && sku.equalsIgnoreCase(otro.getSku())) {
                    throw new ApiException(409, "Ya existe un producto con el SKU " + sku);
                }
            }
            producto.setSku(sku);
        }
        if (peticion.tiene("descripcion")) {
            producto.setDescripcion(peticion.texto("descripcion"));
        }
        String clavePrecio = peticion.tiene("precio") ? "precio" : "precioUnitario";
        if (peticion.tiene(clavePrecio)) {
            double precio = peticion.numeroObligatorio(clavePrecio);
            if (precio < 0) {
                throw new ApiException(400, "El precio no puede ser negativo");
            }
            producto.cambiarPrecio(precio);
        }
        if (peticion.tiene("stock")) {
            int stock = peticion.enteroObligatorio("stock");
            if (stock < 0) {
                throw new ApiException(400, "El stock no puede ser negativo");
            }
            producto.actualizarStock(stock);
        }
        if (peticion.tiene("categoriaId")) {
            long categoriaId = peticion.largo("categoriaId");
            Categoria categoria = contexto.categorias.buscarPorId(categoriaId)
                    .orElseThrow(() -> new ApiException(400, "No existe la categoria " + categoriaId));
            producto.setCategoria(categoria);
        }
        if (peticion.tiene("capacidad")) {
            producto.setCapacidad(peticion.texto("capacidad"));
        }
        if (peticion.tiene("dimensiones")) {
            producto.setDimensiones(peticion.texto("dimensiones"));
        }
        if (peticion.tiene("marca")) {
            producto.setMarca(peticion.texto("marca"));
        }
        if (peticion.tiene("imagen")) {
            producto.setImagen(peticion.texto("imagen"));
        }
        if (peticion.tiene("activo")) {
            boolean activo = peticion.booleano("activo");
            producto.setActivo(activo);
            if (activo && producto.getEstado() == EstadoProducto.INACTIVO) {
                producto.setEstado(EstadoProducto.ACTIVO);
            } else if (!activo && producto.getEstado() == EstadoProducto.ACTIVO) {
                producto.setEstado(EstadoProducto.INACTIVO);
            }
        }
        List<Map<String, Object>> variantes = peticion.listaDeObjetos("variantes");
        if (variantes != null) {
            producto.setVariantes(construirVariantes(producto, variantes));
        }
    }

    private List<VarianteProducto> construirVariantes(Producto producto, List<Map<String, Object>> entradas) {
        long siguienteId = 0;
        for (Producto otro : contexto.productos.listarTodos()) {
            for (VarianteProducto variante : otro.getVariantes()) {
                if (variante.getId() != null) {
                    siguienteId = Math.max(siguienteId, variante.getId());
                }
            }
        }
        siguienteId++;

        List<VarianteProducto> resultado = new ArrayList<>();
        for (Map<String, Object> entrada : entradas) {
            String sku = textoDe(entrada, "sku");
            if (sku == null || sku.isEmpty()) {
                throw new ApiException(400, "Cada variante necesita un 'sku'");
            }
            VarianteProducto variante = new VarianteProducto();
            Object id = entrada.get("id");
            if (id instanceof Number && existeVariante(producto, ((Number) id).longValue())) {
                variante.setId(((Number) id).longValue());
            } else {
                variante.setId(siguienteId++);
            }
            variante.setSku(sku.toUpperCase());
            variante.setColor(textoDe(entrada, "color"));
            variante.setTalla(textoDe(entrada, "talla"));
            variante.setModelo(textoDe(entrada, "modelo"));
            Object precio = entrada.get("precioVenta");
            if (precio != null && !(precio instanceof Number)) {
                throw new ApiException(400, "'precioVenta' de la variante debe ser un numero");
            }
            double precioVenta = precio == null ? producto.getPrecioUnitario() : ((Number) precio).doubleValue();
            if (precioVenta < 0) {
                throw new ApiException(400, "El precio de la variante no puede ser negativo");
            }
            variante.setPrecioVenta(precioVenta);
            variante.setEstado(EstadoProducto.ACTIVO);
            resultado.add(variante);
        }
        return resultado;
    }

    private boolean existeVariante(Producto producto, long id) {
        for (VarianteProducto variante : producto.getVariantes()) {
            if (variante.getId() != null && variante.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private String textoDe(Map<String, Object> mapa, String clave) {
        Object valor = mapa.get(clave);
        if (valor == null) {
            return null;
        }
        if (!(valor instanceof String)) {
            throw new ApiException(400, "El campo '" + clave + "' de la variante debe ser texto");
        }
        return ((String) valor).trim();
    }

    private Producto productoPorId(long id) {
        return contexto.productos.buscarPorId(id)
                .orElseThrow(() -> new ApiException(404, "No existe el producto " + id));
    }

    // ---------- Cupones ----------

    public Respuesta listarCupones(Peticion peticion) {
        peticion.usuarioConPermiso(Permiso.GESTIONAR_CUPONES);
        List<Object> resultado = new ArrayList<>();
        for (Cupon cupon : contexto.cupones.listarTodos()) {
            resultado.add(Mapeadores.cupon(cupon));
        }
        return Respuesta.ok(resultado);
    }

    public Respuesta crearCupon(Peticion peticion) {
        peticion.usuarioConPermiso(Permiso.GESTIONAR_CUPONES);
        Cupon cupon = new Cupon();
        cupon.setActivo(true);
        for (String obligatorio : new String[] {"codigo", "tipoDescuento", "valor", "fechaInicio", "fechaFin"}) {
            if (!peticion.tiene(obligatorio)) {
                throw new ApiException(400, "El campo '" + obligatorio + "' es obligatorio");
            }
        }
        aplicarCamposCupon(cupon, peticion);
        contexto.cupones.guardar(cupon);
        return Respuesta.creado(Mapeadores.cupon(cupon));
    }

    public Respuesta editarCupon(Peticion peticion) {
        peticion.usuarioConPermiso(Permiso.GESTIONAR_CUPONES);
        Cupon cupon = cuponPorId(peticion.idRuta("id"));
        aplicarCamposCupon(cupon, peticion);
        contexto.cupones.guardar(cupon);
        return Respuesta.ok(Mapeadores.cupon(cupon));
    }

    public Respuesta eliminarCupon(Peticion peticion) {
        peticion.usuarioConPermiso(Permiso.GESTIONAR_CUPONES);
        Cupon cupon = cuponPorId(peticion.idRuta("id"));
        contexto.cupones.eliminar(cupon.getId());
        return Respuesta.ok(Mapeadores.mensaje("eliminado", cupon.getId()));
    }

    private void aplicarCamposCupon(Cupon cupon, Peticion peticion) {
        if (peticion.tiene("codigo")) {
            String codigo = peticion.textoObligatorio("codigo").toUpperCase();
            for (Cupon otro : contexto.cupones.listarTodos()) {
                boolean mismo = cupon.getId() != null && cupon.getId().equals(otro.getId());
                if (!mismo && codigo.equalsIgnoreCase(otro.getCodigo())) {
                    throw new ApiException(409, "Ya existe un cupon con el codigo " + codigo);
                }
            }
            cupon.setCodigo(codigo);
        }
        if (peticion.tiene("tipoDescuento")) {
            String tipo = peticion.textoObligatorio("tipoDescuento").toUpperCase();
            try {
                cupon.setTipoDescuento(TipoDescuento.valueOf(tipo));
            } catch (IllegalArgumentException e) {
                throw new ApiException(400, "tipoDescuento invalido (usa PORCENTAJE o MONTO_FIJO)");
            }
        }
        if (peticion.tiene("valor")) {
            cupon.setValor(peticion.numeroObligatorio("valor"));
        }
        if (cupon.getValor() <= 0
                || (cupon.getTipoDescuento() == TipoDescuento.PORCENTAJE && cupon.getValor() > 100)) {
            throw new ApiException(400, "Valor de descuento invalido");
        }
        if (peticion.tiene("fechaInicio")) {
            cupon.setFechaInicio(fecha(peticion, "fechaInicio"));
        }
        if (peticion.tiene("fechaFin")) {
            cupon.setFechaFin(fecha(peticion, "fechaFin"));
        }
        if (cupon.getFechaInicio() != null && cupon.getFechaFin() != null
                && cupon.getFechaFin().isBefore(cupon.getFechaInicio())) {
            throw new ApiException(400, "La fecha de fin no puede ser antes de la de inicio");
        }
        if (peticion.tiene("activo")) {
            cupon.setActivo(peticion.booleano("activo"));
        }
    }

    private LocalDate fecha(Peticion peticion, String clave) {
        try {
            return LocalDate.parse(peticion.textoObligatorio(clave));
        } catch (DateTimeParseException e) {
            throw new ApiException(400, "'" + clave + "' debe tener el formato AAAA-MM-DD");
        }
    }

    private Cupon cuponPorId(long id) {
        return contexto.cupones.buscarPorId(id)
                .orElseThrow(() -> new ApiException(404, "No existe el cupon " + id));
    }

    // ---------- Usuarios y pedidos ----------

    public Respuesta listarUsuarios(Peticion peticion) {
        peticion.usuarioConPermiso(Permiso.GESTIONAR_USUARIOS);
        List<Object> resultado = new ArrayList<>();
        for (Usuario usuario : contexto.servicioClientes.listarUsuarios()) {
            resultado.add(Mapeadores.usuario(usuario));
        }
        return Respuesta.ok(resultado);
    }

    public Respuesta listarPedidos(Peticion peticion) {
        peticion.usuarioConPermiso(Permiso.VER_VENTAS);
        List<Object> resultado = new ArrayList<>();
        for (Pedido pedido : contexto.pedidos.listarTodos()) {
            resultado.add(Mapeadores.pedido(pedido));
        }
        return Respuesta.ok(resultado);
    }

    public Respuesta cambiarEstadoPedido(Peticion peticion) {
        peticion.usuarioConPermiso(Permiso.VER_VENTAS);
        long id = peticion.idRuta("id");
        Pedido pedido = contexto.pedidos.buscarPorId(id)
                .orElseThrow(() -> new ApiException(404, "No existe el pedido " + id));
        EstadoPedido nuevo = peticion.estadoPedido("estado");

        if (pedido.getEstado() == EstadoPedido.CANCELADO && nuevo != EstadoPedido.CANCELADO) {
            throw new ApiException(409, "Un pedido cancelado no se puede reactivar");
        }
        if (nuevo == EstadoPedido.CANCELADO && pedido.getEstado() != EstadoPedido.CANCELADO) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
                    continue;
                }
                contexto.productos.buscarPorId(detalle.getProducto().getId()).ifPresent(producto -> {
                    producto.actualizarStock(producto.getStock() + detalle.getCantidad());
                    contexto.productos.guardar(producto);
                });
            }
        }
        pedido.setEstado(nuevo);
        contexto.pedidos.guardar(pedido);
        return Respuesta.ok(Mapeadores.pedido(pedido));
    }
}
