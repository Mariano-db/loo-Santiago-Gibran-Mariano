package Api;

import claseseguridad.Usuario;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GestorSesiones {

    private static final Duration INACTIVIDAD_MAXIMA = Duration.ofHours(12);

    private final Map<String, Sesion> sesiones = new ConcurrentHashMap<>();
    private final SecureRandom aleatorio = new SecureRandom();

    public Sesion crear(Usuario usuario) {
        byte[] bytes = new byte[32];
        aleatorio.nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);
        Sesion sesion = new Sesion(token, usuario);
        sesiones.put(token, sesion);
        return sesion;
    }

    public Sesion obtener(String token) {
        if (token == null) {
            return null;
        }
        Sesion sesion = sesiones.get(token);
        if (sesion == null) {
            return null;
        }
        if (Duration.between(sesion.getUltimoUso(), Instant.now()).compareTo(INACTIVIDAD_MAXIMA) > 0) {
            sesiones.remove(token);
            return null;
        }
        sesion.tocar();
        return sesion;
    }

    public void cerrar(String token) {
        if (token != null) {
            sesiones.remove(token);
        }
    }
}
