package Servicios;

import Json.JsonEscritor;
import Json.JsonLector;
import clasedescuentos.Cupon;
import clasedescuentos.TipoDescuento;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioCuponJson implements IRepositorio<Cupon, Long> {

    private final String rutaArchivo;

    public RepositorioCuponJson(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public void guardar(Cupon entidad) {
        List<Cupon> cupones = listarTodos();
        if (entidad.getId() == null) {
            long siguienteId = cupones.stream().mapToLong(Cupon::getId).max().orElse(0L) + 1;
            entidad.setId(siguienteId);
            cupones.add(entidad);
        } else {
            boolean reemplazado = false;
            for (int i = 0; i < cupones.size(); i++) {
                if (cupones.get(i).getId().equals(entidad.getId())) {
                    cupones.set(i, entidad);
                    reemplazado = true;
                    break;
                }
            }
            if (!reemplazado) {
                cupones.add(entidad);
            }
        }
        escribir(cupones);
    }

    @Override
    public Optional<Cupon> buscarPorId(Long id) {
        return listarTodos().stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public Optional<Cupon> buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        String buscado = codigo.trim();
        return listarTodos().stream()
                .filter(c -> c.getCodigo() != null && c.getCodigo().equalsIgnoreCase(buscado))
                .findFirst();
    }

    @Override
    public List<Cupon> listarTodos() {
        if (!Files.exists(Path.of(rutaArchivo))) {
            return new ArrayList<>();
        }
        try {
            Object raiz = JsonLector.parsearArchivo(rutaArchivo);
            List<Cupon> cupones = new ArrayList<>();
            for (Object item : (List<?>) raiz) {
                cupones.add(aCupon((Map<?, ?>) item));
            }
            return cupones;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + rutaArchivo, e);
        }
    }

    @Override
    public void eliminar(Long id) {
        List<Cupon> cupones = listarTodos();
        cupones.removeIf(c -> c.getId().equals(id));
        escribir(cupones);
    }

    private void escribir(List<Cupon> cupones) {
        List<Object> lista = new ArrayList<>();
        for (Cupon cupon : cupones) {
            lista.add(aMapa(cupon));
        }
        try {
            JsonEscritor.escribirArchivo(rutaArchivo, lista);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir " + rutaArchivo, e);
        }
    }

    private Map<String, Object> aMapa(Cupon cupon) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", cupon.getId());
        mapa.put("codigo", cupon.getCodigo());
        mapa.put("tipoDescuento", cupon.getTipoDescuento() == null ? null : cupon.getTipoDescuento().name());
        mapa.put("valor", cupon.getValor());
        mapa.put("fechaInicio", cupon.getFechaInicio() == null ? null : cupon.getFechaInicio().toString());
        mapa.put("fechaFin", cupon.getFechaFin() == null ? null : cupon.getFechaFin().toString());
        mapa.put("activo", cupon.isActivo());
        return mapa;
    }

    private Cupon aCupon(Map<?, ?> mapa) {
        Cupon cupon = new Cupon();
        cupon.setId(((Number) mapa.get("id")).longValue());
        cupon.setCodigo((String) mapa.get("codigo"));
        Object tipo = mapa.get("tipoDescuento");
        if (tipo != null) {
            cupon.setTipoDescuento(TipoDescuento.valueOf((String) tipo));
        }
        Object valor = mapa.get("valor");
        cupon.setValor(valor == null ? 0 : ((Number) valor).doubleValue());
        Object inicio = mapa.get("fechaInicio");
        if (inicio != null) {
            cupon.setFechaInicio(LocalDate.parse((String) inicio));
        }
        Object fin = mapa.get("fechaFin");
        if (fin != null) {
            cupon.setFechaFin(LocalDate.parse((String) fin));
        }
        cupon.setActivo(Boolean.TRUE.equals(mapa.get("activo")));
        return cupon;
    }
}
