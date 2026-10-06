package Servicios;

import Inventario.Existencia;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class RepositorioExistenciaJson extends RepositorioJsonBase<Existencia> {

    private final String ruta;

    public RepositorioExistenciaJson(String ruta) {
        super(ruta);
        this.ruta = ruta;
    }

    public boolean existeArchivo() {
        return java.nio.file.Files.exists(java.nio.file.Path.of(ruta));
    }

    public Optional<Existencia> buscar(Long productoId, Long varianteId) {
        return listarTodos().stream().filter(e -> e.es(productoId, varianteId)).findFirst();
    }

    @Override
    protected Map<String, Object> aMapa(Existencia e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", e.getId());
        m.put("productoId", e.getProductoId());
        m.put("varianteId", e.getVarianteId());
        m.put("stockFisico", e.getStockFisico());
        m.put("stockReservado", e.getStockReservado());
        m.put("stockMinimo", e.getStockMinimo());
        return m;
    }

    @Override
    protected Existencia deMapa(Map<?, ?> m) {
        Existencia e = new Existencia();
        e.setId(entero(m, "id"));
        e.setProductoId(entero(m, "productoId"));
        e.setVarianteId(entero(m, "varianteId"));
        e.setStockFisico(intOr0(m, "stockFisico"));
        e.setStockReservado(intOr0(m, "stockReservado"));
        e.setStockMinimo(intOr0(m, "stockMinimo"));
        return e;
    }

    @Override
    protected Long idDe(Existencia e) {
        return e.getId();
    }

    @Override
    protected void asignarId(Existencia e, long id) {
        e.setId(id);
    }
}
