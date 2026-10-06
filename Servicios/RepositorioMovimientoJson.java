package Servicios;

import Inventario.MovimientoInventario;
import Inventario.TipoMovimiento;

import java.util.LinkedHashMap;
import java.util.Map;

public class RepositorioMovimientoJson extends RepositorioJsonBase<MovimientoInventario> {

    public RepositorioMovimientoJson(String ruta) {
        super(ruta);
    }

    @Override
    protected Map<String, Object> aMapa(MovimientoInventario mv) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", mv.getId());
        m.put("productoId", mv.getProductoId());
        m.put("varianteId", mv.getVarianteId());
        m.put("descripcion", mv.getDescripcion());
        m.put("tipo", mv.getTipo().name());
        m.put("cantidad", mv.getCantidad());
        m.put("stockResultante", mv.getStockResultante());
        m.put("fecha", comoTexto(mv.getFecha()));
        m.put("motivo", mv.getMotivo());
        m.put("referencia", mv.getReferencia());
        return m;
    }

    @Override
    protected MovimientoInventario deMapa(Map<?, ?> m) {
        MovimientoInventario mv = new MovimientoInventario();
        mv.setId(entero(m, "id"));
        mv.setProductoId(entero(m, "productoId"));
        mv.setVarianteId(entero(m, "varianteId"));
        mv.setDescripcion(texto(m, "descripcion"));
        mv.setTipo(TipoMovimiento.valueOf(texto(m, "tipo")));
        mv.setCantidad(intOr0(m, "cantidad"));
        mv.setStockResultante(intOr0(m, "stockResultante"));
        mv.setFecha(fechaHora(m, "fecha"));
        mv.setMotivo(texto(m, "motivo"));
        mv.setReferencia(texto(m, "referencia"));
        return mv;
    }

    @Override
    protected Long idDe(MovimientoInventario mv) {
        return mv.getId();
    }

    @Override
    protected void asignarId(MovimientoInventario mv, long id) {
        mv.setId(id);
    }
}
