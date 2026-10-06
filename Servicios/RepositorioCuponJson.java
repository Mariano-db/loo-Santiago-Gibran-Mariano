package Servicios;

import clasedescuentos.Cupon;
import clasedescuentos.TipoDescuento;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class RepositorioCuponJson extends RepositorioJsonBase<Cupon> {

    public RepositorioCuponJson(String rutaArchivo) {
        super(rutaArchivo);
    }

    public Optional<Cupon> buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return listarTodos().stream().filter(c -> c.getCodigo().equalsIgnoreCase(codigo.trim())).findFirst();
    }

    @Override
    protected Map<String, Object> aMapa(Cupon cupon) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", cupon.getId());
        mapa.put("codigo", cupon.getCodigo());
        mapa.put("tipo", cupon.getTipo().name());
        mapa.put("valor", cupon.getValor());
        mapa.put("fechaInicio", cupon.getFechaInicio() == null ? null : cupon.getFechaInicio().toString());
        mapa.put("fechaFin", cupon.getFechaFin() == null ? null : cupon.getFechaFin().toString());
        mapa.put("compraMinima", cupon.getCompraMinima());
        mapa.put("usosMaximos", cupon.getUsosMaximos());
        mapa.put("usosActuales", cupon.getUsosActuales());
        mapa.put("activo", cupon.isActivo());
        return mapa;
    }

    @Override
    protected Cupon deMapa(Map<?, ?> mapa) {
        Cupon cupon = new Cupon();
        cupon.setId(((Number) mapa.get("id")).longValue());
        cupon.setCodigo((String) mapa.get("codigo"));
        cupon.setTipo(TipoDescuento.valueOf((String) mapa.get("tipo")));
        cupon.setValor(numero(mapa.get("valor")).doubleValue());
        Object inicio = mapa.get("fechaInicio");
        cupon.setFechaInicio(inicio == null ? null : LocalDate.parse((String) inicio));
        Object fin = mapa.get("fechaFin");
        cupon.setFechaFin(fin == null ? null : LocalDate.parse((String) fin));
        cupon.setCompraMinima(numero(mapa.get("compraMinima")).doubleValue());
        cupon.setUsosMaximos(numero(mapa.get("usosMaximos")).intValue());
        cupon.setUsosActuales(numero(mapa.get("usosActuales")).intValue());
        cupon.setActivo(Boolean.TRUE.equals(mapa.get("activo")));
        return cupon;
    }

    private Number numero(Object valor) {
        return valor == null ? 0 : (Number) valor;
    }

    @Override
    protected Long idDe(Cupon e) {
        return e.getId();
    }

    @Override
    protected void asignarId(Cupon e, long id) {
        e.setId(id);
    }
}
