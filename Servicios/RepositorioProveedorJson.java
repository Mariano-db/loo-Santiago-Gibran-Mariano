package Servicios;

import Catalogo.Producto;
import claseprovedores.Proveedor;
import claseprovedores.Suministro;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioProveedorJson extends RepositorioJsonBase<Proveedor> {

    private final IRepositorio<Producto, Long> repositorioProductos;

    public RepositorioProveedorJson(String ruta, IRepositorio<Producto, Long> repositorioProductos) {
        super(ruta);
        this.repositorioProductos = repositorioProductos;
    }

    @Override
    protected Map<String, Object> aMapa(Proveedor p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("nombre", p.getNombre());
        m.put("telefono", p.getTelefono());
        m.put("correo", p.getCorreo());
        m.put("activo", p.isActivo());
        List<Object> suministros = new ArrayList<>();
        for (Suministro s : p.getSuministros()) {
            Map<String, Object> sm = new LinkedHashMap<>();
            sm.put("productoId", s.getProducto().getId());
            sm.put("costo", s.getCosto());
            sm.put("tiempoRestockDias", s.getTiempoRestockDias());
            suministros.add(sm);
        }
        m.put("suministros", suministros);
        return m;
    }

    @Override
    protected Proveedor deMapa(Map<?, ?> m) {
        Proveedor p = new Proveedor();
        p.setId(entero(m, "id"));
        p.setNombre(texto(m, "nombre"));
        p.setTelefono(texto(m, "telefono"));
        p.setCorreo(texto(m, "correo"));
        p.setActivo(Boolean.TRUE.equals(m.get("activo")));
        Object raw = m.get("suministros");
        if (raw instanceof List) {
            for (Object item : (List<?>) raw) {
                Map<?, ?> sm = (Map<?, ?>) item;
                Optional<Producto> producto = repositorioProductos.buscarPorId(entero(sm, "productoId"));
                if (producto.isEmpty()) {
                    continue;
                }
                Suministro s = new Suministro();
                s.setProducto(producto.get());
                s.setCosto(decimal(sm, "costo"));
                s.setTiempoRestockDias(intOr0(sm, "tiempoRestockDias"));
                p.getSuministros().add(s);
            }
        }
        return p;
    }

    @Override
    protected Long idDe(Proveedor p) {
        return p.getId();
    }

    @Override
    protected void asignarId(Proveedor p, long id) {
        p.setId(id);
    }
}
