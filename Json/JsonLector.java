package Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonLector {

    private final String texto;
    private int posicion;

    private JsonLector(String texto) {
        this.texto = texto;
        this.posicion = 0;
    }

    public static Object parsear(String json) {
        JsonLector lector = new JsonLector(json);
        lector.saltarEspacios();
        Object valor = lector.leerValor();
        lector.saltarEspacios();
        if (lector.posicion != lector.texto.length()) {
            throw new IllegalArgumentException("Texto sobrante despues del JSON en la posicion " + lector.posicion);
        }
        return valor;
    }

    public static Object parsearArchivo(String ruta) throws IOException {
        String contenido = Files.readString(Path.of(ruta));
        return parsear(contenido);
    }

    private Object leerValor() {
        char c = actual();
        switch (c) {
            case '{':
                return leerObjeto();
            case '[':
                return leerArreglo();
            case '"':
                return leerCadena();
            case 't':
            case 'f':
                return leerBooleano();
            case 'n':
                return leerNulo();
            default:
                return leerNumero();
        }
    }

    private Map<String, Object> leerObjeto() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        esperar('{');
        saltarEspacios();
        if (actual() == '}') {
            posicion++;
            return mapa;
        }
        while (true) {
            saltarEspacios();
            String clave = leerCadena();
            saltarEspacios();
            esperar(':');
            saltarEspacios();
            Object valor = leerValor();
            mapa.put(clave, valor);
            saltarEspacios();
            char c = actual();
            if (c == ',') {
                posicion++;
            } else if (c == '}') {
                posicion++;
                break;
            } else {
                throw new IllegalArgumentException("Se esperaba ',' o '}' en la posicion " + posicion);
            }
        }
        return mapa;
    }

    private List<Object> leerArreglo() {
        List<Object> lista = new ArrayList<>();
        esperar('[');
        saltarEspacios();
        if (actual() == ']') {
            posicion++;
            return lista;
        }
        while (true) {
            saltarEspacios();
            lista.add(leerValor());
            saltarEspacios();
            char c = actual();
            if (c == ',') {
                posicion++;
            } else if (c == ']') {
                posicion++;
                break;
            } else {
                throw new IllegalArgumentException("Se esperaba ',' o ']' en la posicion " + posicion);
            }
        }
        return lista;
    }

    private String leerCadena() {
        esperar('"');
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = texto.charAt(posicion++);
            if (c == '"') {
                break;
            }
            if (c == '\\') {
                char escape = texto.charAt(posicion++);
                switch (escape) {
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'u':
                        String hex = texto.substring(posicion, posicion + 4);
                        sb.append((char) Integer.parseInt(hex, 16));
                        posicion += 4;
                        break;
                    default:
                        throw new IllegalArgumentException("Escape invalido: \\" + escape);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private Boolean leerBooleano() {
        if (texto.startsWith("true", posicion)) {
            posicion += 4;
            return Boolean.TRUE;
        }
        if (texto.startsWith("false", posicion)) {
            posicion += 5;
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Valor booleano invalido en la posicion " + posicion);
    }

    private Object leerNulo() {
        if (texto.startsWith("null", posicion)) {
            posicion += 4;
            return null;
        }
        throw new IllegalArgumentException("Valor invalido en la posicion " + posicion);
    }

    private Double leerNumero() {
        int inicio = posicion;
        if (actual() == '-') {
            posicion++;
        }
        while (posicion < texto.length() && Character.isDigit(texto.charAt(posicion))) {
            posicion++;
        }
        if (posicion < texto.length() && texto.charAt(posicion) == '.') {
            posicion++;
            while (posicion < texto.length() && Character.isDigit(texto.charAt(posicion))) {
                posicion++;
            }
        }
        if (posicion < texto.length() && (texto.charAt(posicion) == 'e' || texto.charAt(posicion) == 'E')) {
            posicion++;
            if (posicion < texto.length() && (texto.charAt(posicion) == '+' || texto.charAt(posicion) == '-')) {
                posicion++;
            }
            while (posicion < texto.length() && Character.isDigit(texto.charAt(posicion))) {
                posicion++;
            }
        }
        String numeroTexto = texto.substring(inicio, posicion);
        if (numeroTexto.isEmpty() || numeroTexto.equals("-")) {
            throw new IllegalArgumentException("Numero invalido en la posicion " + inicio);
        }
        return Double.parseDouble(numeroTexto);
    }

    private void saltarEspacios() {
        while (posicion < texto.length() && Character.isWhitespace(texto.charAt(posicion))) {
            posicion++;
        }
    }

    private void esperar(char esperado) {
        if (actual() != esperado) {
            throw new IllegalArgumentException("Se esperaba '" + esperado + "' en la posicion " + posicion);
        }
        posicion++;
    }

    private char actual() {
        if (posicion >= texto.length()) {
            throw new IllegalArgumentException("Fin de texto inesperado");
        }
        return texto.charAt(posicion);
    }
}
