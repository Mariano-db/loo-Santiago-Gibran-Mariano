package Api;

import Json.JsonLector;
import Ventas.EstadoPedido;
import com.sun.net.httpserver.HttpExchange;
import claseseguridad.Permiso;
import claseseguridad.Usuario;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Peticion {

    private static final int MAXIMO_CUERPO = 1_000_000;

    private final HttpExchange exchange;
    private final String ruta;
    private final Map<String, String> consulta;
    private final Map<String, String> parametrosRuta = new HashMap<>();
    private final Sesion sesion;
    private Map<String, Object> cuerpo;

    public Peticion(HttpExchange exchange, Sesion sesion) {
        this.exchange = exchange;
        this.ruta = exchange.getRequestURI().getPath();
        this.consulta = parsearConsulta(exchange.getRequestURI().getRawQuery());
        this.sesion = sesion;
    }

    public String getMetodo() {
        return exchange.getRequestMethod();
    }

    public String getRuta() {
        return ruta;
    }

    public void setParametroRuta(String nombre, String valor) {
        parametrosRuta.put(nombre, valor);
    }

    public String getConsulta(String nombre) {
        return consulta.get(nombre);
    }

    public Sesion getSesion() {
        return sesion;
    }

    public static String tokenDe(HttpExchange exchange) {
        String cabecera = exchange.getRequestHeaders().getFirst("Authorization");
        if (cabecera == null) {
            return null;
        }
        String prefijo = "Bearer ";
        if (cabecera.regionMatches(true, 0, prefijo, 0, prefijo.length())) {
            return cabecera.substring(prefijo.length()).trim();
        }
        return null;
    }

    // ---------- Autenticacion ----------

    public Sesion sesionRequerida() {
        if (sesion == null) {
            throw new ApiException(401, "Debes iniciar sesion (falta el token o ya expiro)");
        }
        return sesion;
    }

    public Usuario usuarioConPermiso(String codigoPermiso) {
        Usuario usuario = sesionRequerida().getUsuario();
        if (!usuario.isSesionActiva()) {
            throw new ApiException(401, "Debes iniciar sesion con una cuenta");
        }
        if (!usuario.tienePermiso(new Permiso(codigoPermiso))) {
            throw new ApiException(403, "No tienes el permiso " + codigoPermiso);
        }
        return usuario;
    }

    // ---------- Parametros de ruta ----------

    public long idRuta(String nombre) {
        String valor = parametrosRuta.get(nombre);
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
            throw new ApiException(400, "El parametro '" + nombre + "' debe ser un numero entero");
        }
    }

    public int indiceRuta(String nombre) {
        long valor = idRuta(nombre);
        if (valor < 0 || valor > Integer.MAX_VALUE) {
            throw new ApiException(400, "El parametro '" + nombre + "' esta fuera de rango");
        }
        return (int) valor;
    }

    // ---------- Cuerpo JSON ----------

    public Map<String, Object> cuerpo() {
        if (cuerpo != null) {
            return cuerpo;
        }
        byte[] bytes;
        try {
            bytes = exchange.getRequestBody().readNBytes(MAXIMO_CUERPO + 1);
        } catch (IOException e) {
            throw new ApiException(400, "No se pudo leer el cuerpo de la peticion");
        }
        if (bytes.length > MAXIMO_CUERPO) {
            throw new ApiException(413, "El cuerpo de la peticion es demasiado grande");
        }
        String texto = new String(bytes, StandardCharsets.UTF_8).trim();
        if (texto.isEmpty()) {
            cuerpo = new LinkedHashMap<>();
            return cuerpo;
        }
        Object valor;
        try {
            valor = JsonLector.parsear(texto);
        } catch (RuntimeException e) {
            throw new ApiException(400, "JSON invalido: " + e.getMessage());
        }
        if (!(valor instanceof Map)) {
            throw new ApiException(400, "El cuerpo debe ser un objeto JSON");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> mapa = (Map<String, Object>) valor;
        cuerpo = mapa;
        return cuerpo;
    }

    public boolean tiene(String clave) {
        return cuerpo().get(clave) != null;
    }

    public String texto(String clave) {
        Object valor = cuerpo().get(clave);
        if (valor == null) {
            return null;
        }
        if (!(valor instanceof String)) {
            throw new ApiException(400, "El campo '" + clave + "' debe ser texto");
        }
        return ((String) valor).trim();
    }

    public String textoCrudo(String clave) {
        Object valor = cuerpo().get(clave);
        if (valor == null) {
            return null;
        }
        if (!(valor instanceof String)) {
            throw new ApiException(400, "El campo '" + clave + "' debe ser texto");
        }
        return (String) valor;
    }

    public String textoObligatorio(String clave) {
        String valor = texto(clave);
        if (valor == null || valor.isEmpty()) {
            throw new ApiException(400, "El campo '" + clave + "' es obligatorio");
        }
        return valor;
    }

    public Double numero(String clave) {
        Object valor = cuerpo().get(clave);
        if (valor == null) {
            return null;
        }
        if (!(valor instanceof Number)) {
            throw new ApiException(400, "El campo '" + clave + "' debe ser un numero");
        }
        double numero = ((Number) valor).doubleValue();
        if (Double.isNaN(numero) || Double.isInfinite(numero)) {
            throw new ApiException(400, "El campo '" + clave + "' no es un numero valido");
        }
        return numero;
    }

    public double numeroObligatorio(String clave) {
        Double valor = numero(clave);
        if (valor == null) {
            throw new ApiException(400, "El campo '" + clave + "' es obligatorio");
        }
        return valor;
    }

    public Integer entero(String clave) {
        Double valor = numero(clave);
        if (valor == null) {
            return null;
        }
        if (valor != Math.rint(valor) || Math.abs(valor) > Integer.MAX_VALUE) {
            throw new ApiException(400, "El campo '" + clave + "' debe ser un entero");
        }
        return valor.intValue();
    }

    public int enteroObligatorio(String clave) {
        Integer valor = entero(clave);
        if (valor == null) {
            throw new ApiException(400, "El campo '" + clave + "' es obligatorio");
        }
        return valor;
    }

    public Long largo(String clave) {
        Double valor = numero(clave);
        if (valor == null) {
            return null;
        }
        if (valor != Math.rint(valor)) {
            throw new ApiException(400, "El campo '" + clave + "' debe ser un entero");
        }
        return valor.longValue();
    }

    public Boolean booleano(String clave) {
        Object valor = cuerpo().get(clave);
        if (valor == null) {
            return null;
        }
        if (!(valor instanceof Boolean)) {
            throw new ApiException(400, "El campo '" + clave + "' debe ser true o false");
        }
        return (Boolean) valor;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listaDeObjetos(String clave) {
        Object valor = cuerpo().get(clave);
        if (valor == null) {
            return null;
        }
        if (!(valor instanceof List)) {
            throw new ApiException(400, "El campo '" + clave + "' debe ser una lista");
        }
        for (Object elemento : (List<?>) valor) {
            if (!(elemento instanceof Map)) {
                throw new ApiException(400, "El campo '" + clave + "' debe contener objetos");
            }
        }
        return (List<Map<String, Object>>) valor;
    }

    public EstadoPedido estadoPedido(String clave) {
        String texto = textoObligatorio(clave);
        try {
            return EstadoPedido.valueOf(texto.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException(400, "Estado de pedido invalido: " + texto);
        }
    }

    private static Map<String, String> parsearConsulta(String consultaCruda) {
        Map<String, String> mapa = new HashMap<>();
        if (consultaCruda == null || consultaCruda.isEmpty()) {
            return mapa;
        }
        for (String par : consultaCruda.split("&")) {
            if (par.isEmpty()) {
                continue;
            }
            int igual = par.indexOf('=');
            String clave = igual < 0 ? par : par.substring(0, igual);
            String valor = igual < 0 ? "" : par.substring(igual + 1);
            mapa.put(URLDecoder.decode(clave, StandardCharsets.UTF_8), URLDecoder.decode(valor, StandardCharsets.UTF_8));
        }
        return mapa;
    }
}
