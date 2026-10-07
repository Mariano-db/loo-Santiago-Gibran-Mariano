package Servicios;

import Catalogo.Categoria;
import Json.JsonEscritor;
import Json.JsonLector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioCategoriaJson implements IRepositorio<Categoria, Long> {

    private final String rutaArchivo;

    public RepositorioCategoriaJson(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public void guardar(Categoria entidad) {
        List<Categoria> categorias = listarTodos();
        if (entidad.getId() == null) {
            long siguienteId = categorias.stream().mapToLong(Categoria::getId).max().orElse(0L) + 1;
            entidad.setId(siguienteId);
            categorias.add(entidad);
        } else {
            categorias.removeIf(c -> c.getId().equals(entidad.getId()));
            categorias.add(entidad);
        }
        escribir(categorias);
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return listarTodos().stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public List<Categoria> listarTodos() {
        if (!Files.exists(Path.of(rutaArchivo))) {
            return new ArrayList<>();
        }
        try {
            Object raiz = JsonLector.parsearArchivo(rutaArchivo);
            List<Categoria> categorias = new ArrayList<>();
            for (Object item : (List<?>) raiz) {
                categorias.add(aCategoria((Map<?, ?>) item));
            }
            return categorias;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + rutaArchivo, e);
        }
    }

    @Override
    public void eliminar(Long id) {
        List<Categoria> categorias = listarTodos();
        categorias.removeIf(c -> c.getId().equals(id));
        escribir(categorias);
    }

    private void escribir(List<Categoria> categorias) {
        List<Object> lista = new ArrayList<>();
        for (Categoria categoria : categorias) {
            lista.add(aMapa(categoria));
        }
        try {
            JsonEscritor.escribirArchivo(rutaArchivo, lista);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir " + rutaArchivo, e);
        }
    }

    private Map<String, Object> aMapa(Categoria categoria) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", categoria.getId());
        mapa.put("nombre", categoria.getNombre());
        mapa.put("descripcion", categoria.getDescripcion());
        return mapa;
    }

    private Categoria aCategoria(Map<?, ?> mapa) {
        Categoria categoria = new Categoria();
        categoria.setId(((Number) mapa.get("id")).longValue());
        categoria.setNombre((String) mapa.get("nombre"));
        categoria.setDescripcion((String) mapa.get("descripcion"));
        return categoria;
    }
}
