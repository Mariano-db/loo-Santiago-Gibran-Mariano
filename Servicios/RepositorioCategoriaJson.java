package Servicios;

import Catalogo.Categoria;

import java.util.LinkedHashMap;
import java.util.Map;

public class RepositorioCategoriaJson extends RepositorioJsonBase<Categoria> {

    public RepositorioCategoriaJson(String rutaArchivo) {
        super(rutaArchivo);
    }

    @Override
    protected Map<String, Object> aMapa(Categoria categoria) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", categoria.getId());
        mapa.put("nombre", categoria.getNombre());
        mapa.put("descripcion", categoria.getDescripcion());
        return mapa;
    }

    @Override
    protected Categoria deMapa(Map<?, ?> mapa) {
        Categoria categoria = new Categoria();
        categoria.setId(((Number) mapa.get("id")).longValue());
        categoria.setNombre((String) mapa.get("nombre"));
        categoria.setDescripcion((String) mapa.get("descripcion"));
        return categoria;
    }

    @Override
    protected Long idDe(Categoria e) {
        return e.getId();
    }

    @Override
    protected void asignarId(Categoria e, long id) {
        e.setId(id);
    }
}
