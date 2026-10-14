package Api;

import claseseguridad.Cliente;
import claseseguridad.TipoCliente;
import claseseguridad.Usuario;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ControladorAuth {

    private final ContextoApi contexto;

    public ControladorAuth(ContextoApi contexto) {
        this.contexto = contexto;
    }

    public Respuesta registro(Peticion peticion) {
        String nombre = peticion.texto("nombre");
        String email = peticion.texto("email");
        String password = peticion.textoCrudo("password");

        Cliente cliente;
        try {
            cliente = contexto.servicioClientes.registrarCliente(nombre, email, password);
        } catch (IllegalArgumentException e) {
            throw new ApiException(400, e.getMessage());
        }
        cliente.iniciarSesion(password);
        return Respuesta.creado(sesionIniciada(contexto.sesiones.crear(cliente)));
    }

    public Respuesta login(Peticion peticion) {
        String email = peticion.texto("email");
        String password = peticion.textoCrudo("password");
        Optional<Usuario> usuario = contexto.servicioClientes.iniciarSesion(email, password);
        if (usuario.isEmpty()) {
            throw new ApiException(401, "Correo o contrasena incorrectos");
        }
        return Respuesta.ok(sesionIniciada(contexto.sesiones.crear(usuario.get())));
    }

    public Respuesta invitado(Peticion peticion) {
        Cliente invitado = new Cliente();
        invitado.setNombre("Invitado");
        invitado.setTipo(TipoCliente.INVITADO);
        return Respuesta.creado(sesionIniciada(contexto.sesiones.crear(invitado)));
    }

    public Respuesta logout(Peticion peticion) {
        Sesion sesion = peticion.sesionRequerida();
        contexto.servicioClientes.cerrarSesion(sesion.getUsuario());
        contexto.sesiones.cerrar(sesion.getToken());
        return Respuesta.ok(Mapeadores.mensaje("ok", true));
    }

    public Respuesta yo(Peticion peticion) {
        return Respuesta.ok(Mapeadores.usuario(peticion.sesionRequerida().getUsuario()));
    }

    private Map<String, Object> sesionIniciada(Sesion sesion) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("token", sesion.getToken());
        mapa.put("usuario", Mapeadores.usuario(sesion.getUsuario()));
        return mapa;
    }
}
