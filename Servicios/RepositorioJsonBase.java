package Servicios;

import Json.JsonEscritor;
import Json.JsonLector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class RepositorioJsonBase<T> implements IRepositorio<T, Long> {

    private final String rutaArchivo;

    protected RepositorioJsonBase(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    protected abstract Map<String, Object> aMapa(T entidad);

    protected abstract T deMapa(Map<?, ?> mapa);

    protected abstract Long idDe(T entidad);

    protected abstract void asignarId(T entidad, long id);

    @Override
    public void guardar(T entidad) {
        List<T> todos = listarTodos();
        if (idDe(entidad) == null) {
            long siguiente = todos.stream().mapToLong(e -> idDe(e)).max().orElse(0L) + 1;
            asignarId(entidad, siguiente);
        } else {
            todos.removeIf(e -> idDe(e).equals(idDe(entidad)));
        }
        todos.add(entidad);
        escribir(todos);
    }

    @Override
    public Optional<T> buscarPorId(Long id) {
        return listarTodos().stream().filter(e -> idDe(e).equals(id)).findFirst();
    }

    @Override
    public List<T> listarTodos() {
        if (!Files.exists(Path.of(rutaArchivo))) {
            return new ArrayList<>();
        }
        try {
            Object raiz = JsonLector.parsearArchivo(rutaArchivo);
            List<T> resultado = new ArrayList<>();
            for (Object item : (List<?>) raiz) {
                resultado.add(deMapa((Map<?, ?>) item));
            }
            return resultado;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + rutaArchivo, e);
        }
    }

    @Override
    public void eliminar(Long id) {
        List<T> todos = listarTodos();
        todos.removeIf(e -> idDe(e).equals(id));
        escribir(todos);
    }

    public void reemplazarTodos(List<T> entidades) {
        escribir(entidades);
    }

    private void escribir(List<T> entidades) {
        List<Object> lista = new ArrayList<>();
        for (T entidad : entidades) {
            lista.add(aMapa(entidad));
        }
        try {
            Path ruta = Path.of(rutaArchivo);
            if (ruta.getParent() != null) {
                Files.createDirectories(ruta.getParent());
            }
            JsonEscritor.escribirArchivo(rutaArchivo, lista);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir " + rutaArchivo, e);
        }
    }

    protected static String texto(Map<?, ?> m, String clave) {
        return (String) m.get(clave);
    }

    protected static Long entero(Map<?, ?> m, String clave) {
        Object v = m.get(clave);
        return v == null ? null : ((Number) v).longValue();
    }

    protected static int intOr0(Map<?, ?> m, String clave) {
        Object v = m.get(clave);
        return v == null ? 0 : ((Number) v).intValue();
    }

    protected static double decimal(Map<?, ?> m, String clave) {
        Object v = m.get(clave);
        return v == null ? 0 : ((Number) v).doubleValue();
    }

    protected static LocalDateTime fechaHora(Map<?, ?> m, String clave) {
        Object v = m.get(clave);
        return v == null ? null : LocalDateTime.parse((String) v);
    }

    protected static LocalDate fecha(Map<?, ?> m, String clave) {
        Object v = m.get(clave);
        return v == null ? null : LocalDate.parse((String) v);
    }

    protected static String comoTexto(Object valor) {
        return valor == null ? null : valor.toString();
    }
}
