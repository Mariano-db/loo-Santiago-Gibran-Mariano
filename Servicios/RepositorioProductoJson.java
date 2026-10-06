package Servicios;

import Catalogo.Categoria;
import Catalogo.EstadoProducto;
import Catalogo.Producto;
import Catalogo.VarianteProducto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepositorioProductoJson extends RepositorioJsonBase<Producto> {

    private final IRepositorio<Categoria, Long> repositorioCategorias;

    public RepositorioProductoJson(String rutaArchivo, IRepositorio<Categoria, Long> repositorioCategorias) {
        super(rutaArchivo);
        this.repositorioCategorias = repositorioCategorias;
    }

    public List<Producto> listarActivos() {
        List<Producto> activos = new ArrayList<>();
        for (Producto producto : listarTodos()) {
            if (producto.isActivo() && producto.getEstado() == EstadoProducto.ACTIVO) {
                activos.add(producto);
            }
        }
        return activos;
    }

    @Override
    protected Map<String, Object> aMapa(Producto producto) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", producto.getId());
        mapa.put("sku", producto.getSku());
        mapa.put("nombre", producto.getNombre());
        mapa.put("descripcion", producto.getDescripcion());
        mapa.put("precioBase", producto.getPrecioBase());
        mapa.put("precioUnitario", producto.getPrecioUnitario());
        mapa.put("tasaImpuesto", producto.getTasaImpuesto());
        mapa.put("stock", producto.getStock());
        mapa.put("marca", producto.getMarca());
        mapa.put("imagen", producto.getImagen());
        mapa.put("categoriaId", producto.getCategoria() == null ? null : producto.getCategoria().getId());
        mapa.put("estado", producto.getEstado() == null ? null : producto.getEstado().name());
        mapa.put("capacidad", producto.getCapacidad());
        mapa.put("dimensiones", producto.getDimensiones());
        mapa.put("activo", producto.isActivo());

        List<Object> variantes = new ArrayList<>();
        for (VarianteProducto variante : producto.getVariantes()) {
            variantes.add(aMapaVariante(variante));
        }
        mapa.put("variantes", variantes);
        return mapa;
    }

    private Map<String, Object> aMapaVariante(VarianteProducto variante) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", variante.getId());
        mapa.put("sku", variante.getSku());
        mapa.put("talla", variante.getTalla());
        mapa.put("color", variante.getColor());
        mapa.put("modelo", variante.getModelo());
        mapa.put("precioVenta", variante.getPrecioVenta());
        mapa.put("estado", variante.getEstado() == null ? null : variante.getEstado().name());
        return mapa;
    }

    @Override
    protected Producto deMapa(Map<?, ?> mapa) {
        Producto producto = new Producto();
        producto.setId(numeroALong(mapa.get("id")));
        producto.setSku((String) mapa.get("sku"));
        producto.setNombre((String) mapa.get("nombre"));
        producto.setDescripcion((String) mapa.get("descripcion"));
        producto.setPrecioBase(numeroADouble(mapa.get("precioBase")));
        producto.setPrecioUnitario(numeroADouble(mapa.get("precioUnitario")));
        producto.setTasaImpuesto(numeroADouble(mapa.get("tasaImpuesto")));
        producto.setStock(numeroADouble(mapa.get("stock")).intValue());
        producto.setMarca((String) mapa.get("marca"));
        producto.setImagen((String) mapa.get("imagen"));

        Object categoriaId = mapa.get("categoriaId");
        if (categoriaId != null) {
            repositorioCategorias.buscarPorId(numeroALong(categoriaId)).ifPresent(producto::setCategoria);
        }

        Object estado = mapa.get("estado");
        if (estado != null) {
            producto.setEstado(EstadoProducto.valueOf((String) estado));
        }

        producto.setCapacidad((String) mapa.get("capacidad"));
        producto.setDimensiones((String) mapa.get("dimensiones"));
        producto.setActivo(Boolean.TRUE.equals(mapa.get("activo")));

        Object variantesRaw = mapa.get("variantes");
        List<VarianteProducto> variantes = new ArrayList<>();
        if (variantesRaw instanceof List) {
            for (Object item : (List<?>) variantesRaw) {
                variantes.add(aVariante((Map<?, ?>) item));
            }
        }
        producto.setVariantes(variantes);

        return producto;
    }

    private VarianteProducto aVariante(Map<?, ?> mapa) {
        VarianteProducto variante = new VarianteProducto();
        variante.setId(numeroALong(mapa.get("id")));
        variante.setSku((String) mapa.get("sku"));
        variante.setTalla((String) mapa.get("talla"));
        variante.setColor((String) mapa.get("color"));
        variante.setModelo((String) mapa.get("modelo"));
        variante.setPrecioVenta(numeroADouble(mapa.get("precioVenta")));
        Object estado = mapa.get("estado");
        if (estado != null) {
            variante.setEstado(EstadoProducto.valueOf((String) estado));
        }
        return variante;
    }

    private Long numeroALong(Object valor) {
        return valor == null ? null : ((Number) valor).longValue();
    }

    private Double numeroADouble(Object valor) {
        return valor == null ? 0.0 : ((Number) valor).doubleValue();
    }

    @Override
    protected Long idDe(Producto e) {
        return e.getId();
    }

    @Override
    protected void asignarId(Producto e, long id) {
        e.setId(id);
    }
}
