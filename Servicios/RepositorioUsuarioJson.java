package Servicios;

import claseseguridad.Administrador;
import claseseguridad.Cliente;
import claseseguridad.Rol;
import claseseguridad.TipoCliente;
import claseseguridad.Usuario;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class RepositorioUsuarioJson extends RepositorioJsonBase<Usuario> {

    public static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";
    public static final String ROL_CLIENTE = "CLIENTE";

    public RepositorioUsuarioJson(String rutaArchivo) {
        super(rutaArchivo);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return listarTodos().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return listarTodos().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    @Override
    protected Map<String, Object> aMapa(Usuario usuario) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", usuario.getId());
        mapa.put("rol", nombreRol(usuario));
        mapa.put("nombre", usuario.getNombre());
        mapa.put("username", usuario.getUsername());
        mapa.put("email", usuario.getEmail());
        mapa.put("passwordHash", usuario.getPasswordHash());
        mapa.put("telefono", usuario.getTelefono());
        mapa.put("activo", usuario.isActivo());
        if (usuario instanceof Cliente) {
            Cliente cliente = (Cliente) usuario;
            mapa.put("rfc", cliente.getRfc());
            mapa.put("puntosFidelidad", cliente.getPuntosFidelidad());
        }
        return mapa;
    }

    @Override
    protected Usuario deMapa(Map<?, ?> mapa) {
        String nombreRol = (String) mapa.get("rol");
        Usuario usuario;
        if (ROL_ADMINISTRADOR.equals(nombreRol)) {
            usuario = new Administrador();
        } else {
            Cliente cliente = new Cliente();
            cliente.setTipo(TipoCliente.REGISTRADO);
            cliente.setRfc((String) mapa.get("rfc"));
            Object puntos = mapa.get("puntosFidelidad");
            cliente.setPuntosFidelidad(puntos == null ? 0 : ((Number) puntos).intValue());
            usuario = cliente;
            nombreRol = ROL_CLIENTE;
        }
        Rol rol = new Rol();
        rol.setNombre(nombreRol);
        usuario.setRol(rol);

        usuario.setId(((Number) mapa.get("id")).longValue());
        usuario.setNombre((String) mapa.get("nombre"));
        usuario.setUsername((String) mapa.get("username"));
        usuario.setEmail((String) mapa.get("email"));
        usuario.setPasswordHash((String) mapa.get("passwordHash"));
        usuario.setTelefono((String) mapa.get("telefono"));
        usuario.setActivo(Boolean.TRUE.equals(mapa.get("activo")));
        return usuario;
    }

    private String nombreRol(Usuario usuario) {
        if (usuario instanceof Administrador) {
            return ROL_ADMINISTRADOR;
        }
        return ROL_CLIENTE;
    }

    @Override
    protected Long idDe(Usuario e) {
        return e.getId() == 0 ? null : e.getId();
    }

    @Override
    protected void asignarId(Usuario e, long id) {
        e.setId(id);
    }
}
