package Servicios;

import Catalogo.Producto;
import Catalogo.VarianteProducto;
import claseprovedores.DetalleOrdenCompra;
import claseprovedores.EstadoOrdenCompra;
import claseprovedores.OrdenCompra;
import claseprovedores.Proveedor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioOrdenCompraJson extends RepositorioJsonBase<OrdenCompra> {

    private final IRepositorio<Proveedor, Long> repositorioProveedores;
    private final IRepositorio<Producto, Long> repositorioProductos;

    public RepositorioOrdenCompraJson(String ruta, IRepositorio<Proveedor, Long> repositorioProveedores,
                                      IRepositorio<Producto, Long> repositorioProductos) {
        super(ruta);
        this.repositorioProveedores = repositorioProveedores;
        this.repositorioProductos = repositorioProductos;
    }

    @Override
    protected Map<String, Object> aMapa(OrdenCompra o) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", o.getId());
        m.put("proveedorId", o.getProveedor() == null ? null : o.getProveedor().getId());
        m.put("fecha", comoTexto(o.getFecha()));
        m.put("fechaRecepcion", comoTexto(o.getFechaRecepcion()));
        m.put("estado", o.getEstado().name());
        List<Object> detalles = new ArrayList<>();
        for (DetalleOrdenCompra d : o.getDetalles()) {
            Map<String, Object> dm = new LinkedHashMap<>();
            dm.put("productoId", d.getProducto().getId());
            dm.put("producto", d.getProducto().getNombre());
            dm.put("varianteId", d.getVariante() == null ? null : d.getVariante().getId());
            dm.put("cantidad", d.getCantidad());
            dm.put("costoUnitario", d.getCostoUnitario());
            detalles.add(dm);
        }
        m.put("detalles", detalles);
        return m;
    }

    @Override
    protected OrdenCompra deMapa(Map<?, ?> m) {
        OrdenCompra o = new OrdenCompra();
        o.setId(entero(m, "id"));
        Long proveedorId = entero(m, "proveedorId");
        if (proveedorId != null) {
            repositorioProveedores.buscarPorId(proveedorId).ifPresent(o::setProveedor);
        }
        o.setFecha(fechaHora(m, "fecha"));
        o.setFechaRecepcion(fechaHora(m, "fechaRecepcion"));
        o.setEstado(EstadoOrdenCompra.valueOf(texto(m, "estado")));
        Object raw = m.get("detalles");
        if (raw instanceof List) {
            for (Object item : (List<?>) raw) {
                Map<?, ?> dm = (Map<?, ?>) item;
                DetalleOrdenCompra d = new DetalleOrdenCompra();
                Long productoId = entero(dm, "productoId");
                Optional<Producto> real = repositorioProductos.buscarPorId(productoId);
                if (real.isPresent()) {
                    d.setProducto(real.get());
                } else {

                    Producto huella = new Producto();
                    huella.setId(productoId);
                    huella.setNombre(texto(dm, "producto"));
                    d.setProducto(huella);
                }
                Long varianteId = entero(dm, "varianteId");
                if (varianteId != null) {
                    VarianteProducto v = real.flatMap(p -> p.getVariantes().stream()
                            .filter(x -> x.getId().equals(varianteId)).findFirst()).orElse(null);
                    if (v == null) {
                        v = new VarianteProducto();
                        v.setId(varianteId);
                    }
                    d.setVariante(v);
                }
                d.setCantidad(intOr0(dm, "cantidad"));
                d.setCostoUnitario(decimal(dm, "costoUnitario"));
                o.getDetalles().add(d);
            }
        }
        return o;
    }

    @Override
    protected Long idDe(OrdenCompra o) {
        return o.getId();
    }

    @Override
    protected void asignarId(OrdenCompra o, long id) {
        o.setId(id);
    }
}
