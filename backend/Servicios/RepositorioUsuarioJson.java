package Servicios;

import Json.JsonEscritor;
import Json.JsonLector;
import claseseguridad.Administrador;
import claseseguridad.Cliente;
import claseseguridad.Permiso;
import claseseguridad.Rol;
import claseseguridad.TipoCliente;
import claseseguridad.Usuario;
import claseseguridad.Vendedor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioUsuarioJson implements IRepositorio<Usuario, Long> {

    public static final String ROL_CLIENTE = "CLIENTE";
    public static final String ROL_VENDEDOR = "VENDEDOR";
    public static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";

    private final String rutaArchivo;

    public RepositorioUsuarioJson(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public void guardar(Usuario entidad) {
        List<Usuario> usuarios = listarTodos();
        if (entidad.getId() == 0) {
            long siguienteId = usuarios.stream().mapToLong(Usuario::getId).max().orElse(0L) + 1;
            entidad.setId(siguienteId);
            usuarios.add(entidad);
        } else {
            usuarios.removeIf(u -> u.getId() == entidad.getId());
            usuarios.add(entidad);
        }
        escribir(usuarios);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return listarTodos().stream().filter(u -> id != null && u.getId() == id).findFirst();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        String buscado = email.trim();
        return listarTodos().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(buscado))
                .findFirst();
    }

    @Override
    public List<Usuario> listarTodos() {
        if (!Files.exists(Path.of(rutaArchivo))) {
            return new ArrayList<>();
        }
        try {
            Object raiz = JsonLector.parsearArchivo(rutaArchivo);
            List<Usuario> usuarios = new ArrayList<>();
            for (Object item : (List<?>) raiz) {
                usuarios.add(aUsuario((Map<?, ?>) item));
            }
            return usuarios;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + rutaArchivo, e);
        }
    }

    @Override
    public void eliminar(Long id) {
        List<Usuario> usuarios = listarTodos();
        usuarios.removeIf(u -> id != null && u.getId() == id);
        escribir(usuarios);
    }

    public static String nombreRol(Usuario usuario) {
        if (usuario instanceof Administrador) {
            return ROL_ADMINISTRADOR;
        }
        if (usuario instanceof Vendedor) {
            return ROL_VENDEDOR;
        }
        return ROL_CLIENTE;
    }

      public static Rol crearRol(String nombre) {
        return Rol.crearPorNombre(nombre);
    }


    private void escribir(List<Usuario> usuarios) {
        List<Object> lista = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            lista.add(aMapa(usuario));
        }
        try {
            JsonEscritor.escribirArchivo(rutaArchivo, lista);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir " + rutaArchivo, e);
        }
    }

    private Map<String, Object> aMapa(Usuario usuario) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", usuario.getId());
        mapa.put("rol", nombreRol(usuario));
        mapa.put("nombre", usuario.getNombre());
        mapa.put("username", usuario.getUsername());
        mapa.put("email", usuario.getEmail());
        mapa.put("passwordHash", usuario.getPasswordHash());
        mapa.put("sal", usuario.getSal());
        mapa.put("telefono", usuario.getTelefono());
        mapa.put("activo", usuario.isActivo());

        List<Object> permisos = new ArrayList<>();
        for (Permiso permiso : usuario.getPermisos()) {
            permisos.add(permiso.getCodigo());
        }
        mapa.put("permisos", permisos);

        if (usuario instanceof Cliente) {
            Cliente cliente = (Cliente) usuario;
            mapa.put("rfc", cliente.getRfc());
            mapa.put("tipo", cliente.getTipo() == null ? null : cliente.getTipo().name());
            mapa.put("puntosFidelidad", cliente.getPuntosFidelidad());
        }
        if (usuario instanceof Vendedor) {
            mapa.put("turno", ((Vendedor) usuario).getTurno());
        }
        return mapa;
    }

    private Usuario aUsuario(Map<?, ?> mapa) {
        String rol = (String) mapa.get("rol");
        Usuario usuario;
        switch (rol == null ? ROL_CLIENTE : rol) {
            case ROL_ADMINISTRADOR:
                usuario = new Administrador();
                break;
            case ROL_VENDEDOR:
                Vendedor vendedor = new Vendedor();
                vendedor.setTurno((String) mapa.get("turno"));
                usuario = vendedor;
                break;
            case ROL_CLIENTE:
                Cliente cliente = new Cliente();
                cliente.setRfc((String) mapa.get("rfc"));
                Object tipo = mapa.get("tipo");
                cliente.setTipo(tipo == null ? TipoCliente.REGISTRADO : TipoCliente.valueOf((String) tipo));
                Object puntos = mapa.get("puntosFidelidad");
                cliente.setPuntosFidelidad(puntos == null ? 0 : ((Number) puntos).intValue());
                usuario = cliente;
                break;
            default:
                throw new IllegalArgumentException("Rol desconocido en " + rutaArchivo + ": " + rol);
        }

        usuario.setId(((Number) mapa.get("id")).longValue());
        usuario.setRol(crearRol(nombreRol(usuario)));
        usuario.setNombre((String) mapa.get("nombre"));
        usuario.setUsername((String) mapa.get("username"));
        usuario.setEmail((String) mapa.get("email"));
        usuario.setPasswordHash((String) mapa.get("passwordHash"));
        usuario.setSal((String) mapa.get("sal"));
        usuario.setTelefono((String) mapa.get("telefono"));
        usuario.setActivo(Boolean.TRUE.equals(mapa.get("activo")));

        Object permisosRaw = mapa.get("permisos");
        if (permisosRaw instanceof List) {
            for (Object codigo : (List<?>) permisosRaw) {
                usuario.asignarPermiso(new Permiso((String) codigo));
            }
        }
        return usuario;
    }
}
