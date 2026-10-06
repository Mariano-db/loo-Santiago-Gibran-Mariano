package Servicios;

import claseimpresion.EstadoImpresion;
import claseimpresion.SolicitudImpresion;
import claseseguridad.Cliente;
import claseseguridad.Usuario;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositorioImpresionJson extends RepositorioJsonBase<SolicitudImpresion> {

    private final IRepositorio<Usuario, Long> repositorioUsuarios;

    public RepositorioImpresionJson(String ruta, IRepositorio<Usuario, Long> repositorioUsuarios) {
        super(ruta);
        this.repositorioUsuarios = repositorioUsuarios;
    }

    public List<SolicitudImpresion> listarPorCliente(long clienteId) {
        return listarTodos().stream()
                .filter(s -> s.getCliente() != null && s.getCliente().getId() == clienteId)
                .collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> aMapa(SolicitudImpresion s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("clienteId", s.getCliente() == null ? null : s.getCliente().getId());
        m.put("archivo", s.getArchivo());
        m.put("cantidad", s.getCantidad());
        m.put("especificaciones", s.getEspecificaciones());
        m.put("estado", s.getEstado().name());
        m.put("fecha", comoTexto(s.getFecha()));
        return m;
    }

    @Override
    protected SolicitudImpresion deMapa(Map<?, ?> m) {
        SolicitudImpresion s = new SolicitudImpresion();
        s.setId(entero(m, "id"));
        Long clienteId = entero(m, "clienteId");
        if (clienteId != null) {
            repositorioUsuarios.buscarPorId(clienteId)
                    .filter(u -> u instanceof Cliente)
                    .ifPresent(u -> s.setCliente((Cliente) u));
        }
        s.setArchivo(texto(m, "archivo"));
        s.setCantidad(intOr0(m, "cantidad"));
        s.setEspecificaciones(texto(m, "especificaciones"));
        s.setEstado(EstadoImpresion.valueOf(texto(m, "estado")));
        s.setFecha(fechaHora(m, "fecha"));
        return s;
    }

    @Override
    protected Long idDe(SolicitudImpresion s) {
        return s.getId();
    }

    @Override
    protected void asignarId(SolicitudImpresion s, long id) {
        s.setId(id);
    }
}
