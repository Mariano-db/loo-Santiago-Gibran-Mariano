package Api;

import Exepciones.AccesoDenegadoException;
import Json.JsonEscritor;
import Utils.ConfiguracionSistema;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Servidor HTTP del backend (solo JDK, sin librerias). Expone la logica de A-Store como API JSON
 * bajo /api y sirve el frontend estatico desde la carpeta indicada.
 *
 * Uso (desde backend/): java -cp out Api.ServidorApi [--port 8080] [--host 127.0.0.1] [--data data] [--front ../frontend]
 */
public class ServidorApi {

    private static final Object CERROJO = new Object();

    private final String host;
    private final int puerto;
    private final Path directorioFrontend;
    private final ContextoApi contexto;
    private final List<Ruta> rutas = new ArrayList<>();
    private HttpServer servidor;

    public ServidorApi(String host, int puerto, String directorioDatos, String directorioFrontend) {
        this.host = host;
        this.puerto = puerto;
        this.directorioFrontend = Path.of(directorioFrontend).toAbsolutePath().normalize();
        this.contexto = new ContextoApi(directorioDatos);
        registrarRutas();
    }

    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        int puerto = 8080;
        String datos = "data";
        String frontend = "../frontend";
        for (int i = 0; i < args.length; i++) {
            String siguiente = i + 1 < args.length ? args[i + 1] : null;
            switch (args[i]) {
                case "--host":
                    host = requerir(args[i], siguiente);
                    i++;
                    break;
                case "--port":
                    try {
                        puerto = Integer.parseInt(requerir(args[i], siguiente));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("--port debe ser un numero");
                    }
                    i++;
                    break;
                case "--data":
                    datos = requerir(args[i], siguiente);
                    i++;
                    break;
                case "--front":
                    frontend = requerir(args[i], siguiente);
                    i++;
                    break;
                default:
                    throw new IllegalArgumentException("Argumento desconocido: " + args[i]);
            }
        }

        ConfiguracionSistema config = ConfiguracionSistema.getInstancia();
        config.setNombreTienda("A-Store");
        config.setMoneda("MXN");
        config.setTasaImpuestoDefault(0.16);

        ServidorApi api = new ServidorApi(host, puerto, datos, frontend);
        api.iniciar();
        System.out.println("A-Store API escuchando en http://" + host + ":" + puerto);
        System.out.println("Datos: " + Path.of(datos).toAbsolutePath().normalize());
        System.out.println("Frontend: " + api.directorioFrontend);
    }

    private static String requerir(String opcion, String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Falta el valor de " + opcion);
        }
        return valor;
    }

    public void iniciar() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(host, puerto), 0);
        servidor.createContext("/", this::atender);
        servidor.setExecutor(Executors.newFixedThreadPool(8));
        servidor.start();
    }

    public void detener() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    public int getPuerto() {
        return servidor == null ? puerto : servidor.getAddress().getPort();
    }

    // ---------- Rutas ----------

    private void registrarRutas() {
        ControladorCatalogo catalogo = new ControladorCatalogo(contexto);
        ControladorAuth auth = new ControladorAuth(contexto);
        ControladorCarrito carrito = new ControladorCarrito(contexto);
        ControladorAdmin admin = new ControladorAdmin(contexto);

        agregar("GET", "/api/salud", this::salud);
        agregar("GET", "/api/categorias", catalogo::categorias);
        agregar("GET", "/api/productos", catalogo::productos);
        agregar("GET", "/api/productos/{id}", catalogo::producto);

        agregar("POST", "/api/registro", auth::registro);
        agregar("POST", "/api/login", auth::login);
        agregar("POST", "/api/invitado", auth::invitado);
        agregar("POST", "/api/logout", auth::logout);
        agregar("GET", "/api/yo", auth::yo);

        agregar("GET", "/api/carrito", carrito::ver);
        agregar("DELETE", "/api/carrito", carrito::vaciar);
        agregar("POST", "/api/carrito/items", carrito::agregar);
        agregar("PUT", "/api/carrito/items/{indice}", carrito::cambiarCantidad);
        agregar("DELETE", "/api/carrito/items/{indice}", carrito::eliminar);
        agregar("POST", "/api/carrito/cupon", carrito::aplicarCupon);
        agregar("DELETE", "/api/carrito/cupon", carrito::quitarCupon);
        agregar("POST", "/api/checkout", carrito::checkout);
        agregar("GET", "/api/pedidos", carrito::misPedidos);

        agregar("GET", "/api/admin/productos", admin::listarProductos);
        agregar("POST", "/api/admin/productos", admin::crearProducto);
        agregar("GET", "/api/admin/productos/{id}", admin::verProducto);
        agregar("PUT", "/api/admin/productos/{id}", admin::editarProducto);
        agregar("DELETE", "/api/admin/productos/{id}", admin::eliminarProducto);
        agregar("GET", "/api/admin/cupones", admin::listarCupones);
        agregar("POST", "/api/admin/cupones", admin::crearCupon);
        agregar("PUT", "/api/admin/cupones/{id}", admin::editarCupon);
        agregar("DELETE", "/api/admin/cupones/{id}", admin::eliminarCupon);
        agregar("GET", "/api/admin/usuarios", admin::listarUsuarios);
        agregar("GET", "/api/admin/pedidos", admin::listarPedidos);
        agregar("PUT", "/api/admin/pedidos/{id}/estado", admin::cambiarEstadoPedido);
    }

    private void agregar(String metodo, String patron, Manejador manejador) {
        rutas.add(new Ruta(metodo, patron, manejador));
    }

    private Respuesta salud(Peticion peticion) {
        ConfiguracionSistema config = ConfiguracionSistema.getInstancia();
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("estado", "ok");
        mapa.put("tienda", config.getNombreTienda());
        mapa.put("moneda", config.getMoneda());
        return Respuesta.ok(mapa);
    }

    // ---------- Atencion de peticiones ----------

    private void atender(HttpExchange exchange) throws IOException {
        try {
            agregarCabecerasCors(exchange);
            String metodo = exchange.getRequestMethod();
            String ruta = exchange.getRequestURI().getPath();

            if (metodo.equals("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (ruta.equals("/api") || ruta.startsWith("/api/")) {
                atenderApi(exchange, metodo, ruta);
            } else if (metodo.equals("GET") || metodo.equals("HEAD")) {
                servirArchivo(exchange, ruta);
            } else {
                enviarJson(exchange, 405, error("Metodo no permitido"));
            }
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e);
            e.printStackTrace();
            try {
                enviarJson(exchange, 500, error("Error interno del servidor"));
            } catch (Exception ignorada) {
                // la conexion ya se cerro
            }
        } finally {
            exchange.close();
        }
    }

    private void atenderApi(HttpExchange exchange, String metodo, String ruta) throws IOException {
        Ruta coincidencia = null;
        boolean rutaExiste = false;
        Map<String, String> parametros = null;
        for (Ruta candidata : rutas) {
            Map<String, String> encontrados = candidata.coincide(ruta);
            if (encontrados == null) {
                continue;
            }
            rutaExiste = true;
            if (candidata.metodo.equals(metodo)) {
                coincidencia = candidata;
                parametros = encontrados;
                break;
            }
        }
        if (coincidencia == null) {
            if (rutaExiste) {
                enviarJson(exchange, 405, error("Metodo " + metodo + " no permitido en " + ruta));
            } else {
                enviarJson(exchange, 404, error("Ruta no encontrada: " + ruta));
            }
            return;
        }

        Respuesta respuesta;
        long inicio = System.currentTimeMillis();
        synchronized (CERROJO) {
            respuesta = ejecutar(exchange, coincidencia, parametros);
        }
        enviarJson(exchange, respuesta.getEstado(), respuesta.getCuerpo());
        System.out.printf("%s %s -> %d (%d ms)%n", metodo, ruta, respuesta.getEstado(),
                System.currentTimeMillis() - inicio);
    }

    private Respuesta ejecutar(HttpExchange exchange, Ruta ruta, Map<String, String> parametros) {
        try {
            Sesion sesion = contexto.sesiones.obtener(Peticion.tokenDe(exchange));
            Peticion peticion = new Peticion(exchange, sesion);
            parametros.forEach(peticion::setParametroRuta);
            return ruta.manejador.manejar(peticion);
        } catch (ApiException e) {
            return Respuesta.con(e.getEstado(), error(e.getMessage()));
        } catch (AccesoDenegadoException e) {
            return Respuesta.con(403, error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return Respuesta.con(400, error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error en " + exchange.getRequestMethod() + " " + exchange.getRequestURI() + ": " + e);
            e.printStackTrace();
            return Respuesta.con(500, error("Error interno del servidor"));
        }
    }

    private static Map<String, Object> error(String mensaje) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("error", mensaje);
        return mapa;
    }

    private void agregarCabecerasCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Authorization, Content-Type");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    }

    private void enviarJson(HttpExchange exchange, int estado, Object cuerpo) throws IOException {
        byte[] bytes = JsonEscritor.escribir(cuerpo).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        escribir(exchange, estado, bytes);
    }

    private void escribir(HttpExchange exchange, int estado, byte[] bytes) throws IOException {
        if (exchange.getRequestMethod().equals("HEAD")) {
            exchange.sendResponseHeaders(estado, -1);
            return;
        }
        exchange.sendResponseHeaders(estado, bytes.length);
        try (OutputStream salida = exchange.getResponseBody()) {
            salida.write(bytes);
        }
    }

    // ---------- Archivos estaticos ----------

    private void servirArchivo(HttpExchange exchange, String ruta) throws IOException {
        String relativa = ruta.equals("/") ? "index.html" : ruta.substring(1);
        Path archivo = relativa.indexOf('\0') >= 0 ? null : directorioFrontend.resolve(relativa).normalize();
        if (archivo != null && Files.isDirectory(archivo)) {
            archivo = archivo.resolve("index.html");
        }
        if (archivo == null || !archivo.startsWith(directorioFrontend) || !Files.isRegularFile(archivo)) {
            byte[] mensaje = "Archivo no encontrado".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            escribir(exchange, 404, mensaje);
            return;
        }
        exchange.getResponseHeaders().set("Content-Type", tipoContenido(archivo.getFileName().toString()));
        escribir(exchange, 200, Files.readAllBytes(archivo));
    }

    private String tipoContenido(String nombre) {
        String minusculas = nombre.toLowerCase();
        if (minusculas.endsWith(".html") || minusculas.endsWith(".htm")) {
            return "text/html; charset=utf-8";
        }
        if (minusculas.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (minusculas.endsWith(".js") || minusculas.endsWith(".mjs")) {
            return "text/javascript; charset=utf-8";
        }
        if (minusculas.endsWith(".json")) {
            return "application/json; charset=utf-8";
        }
        if (minusculas.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (minusculas.endsWith(".png")) {
            return "image/png";
        }
        if (minusculas.endsWith(".jpg") || minusculas.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (minusculas.endsWith(".gif")) {
            return "image/gif";
        }
        if (minusculas.endsWith(".webp")) {
            return "image/webp";
        }
        if (minusculas.endsWith(".ico")) {
            return "image/x-icon";
        }
        if (minusculas.endsWith(".woff2")) {
            return "font/woff2";
        }
        if (minusculas.endsWith(".txt")) {
            return "text/plain; charset=utf-8";
        }
        return "application/octet-stream";
    }

    // ---------- Ruta ----------

    private static final class Ruta {

        private final String metodo;
        private final String[] segmentos;
        private final Manejador manejador;

        private Ruta(String metodo, String patron, Manejador manejador) {
            this.metodo = metodo;
            this.segmentos = patron.substring(1).split("/");
            this.manejador = manejador;
        }

        private Map<String, String> coincide(String ruta) {
            String limpia = ruta.length() > 1 && ruta.endsWith("/") ? ruta.substring(0, ruta.length() - 1) : ruta;
            String[] recibidos = limpia.substring(1).split("/");
            if (recibidos.length != segmentos.length) {
                return null;
            }
            Map<String, String> parametros = new LinkedHashMap<>();
            for (int i = 0; i < segmentos.length; i++) {
                String esperado = segmentos[i];
                if (esperado.startsWith("{") && esperado.endsWith("}")) {
                    parametros.put(esperado.substring(1, esperado.length() - 1), recibidos[i]);
                } else if (!esperado.equals(recibidos[i])) {
                    return null;
                }
            }
            return parametros;
        }
    }
}
