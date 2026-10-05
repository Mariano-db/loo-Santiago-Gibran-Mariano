package Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class JsonEscritor {

    private JsonEscritor() {
    }

    public static String escribir(Object valor) {
        StringBuilder sb = new StringBuilder();
        escribirValor(valor, sb, 0);
        return sb.toString();
    }

    public static void escribirArchivo(String ruta, Object valor) throws IOException {
        Files.writeString(Path.of(ruta), escribir(valor));
    }

    private static void escribirValor(Object valor, StringBuilder sb, int nivel) {
        if (valor == null) {
            sb.append("null");
        } else if (valor instanceof Map) {
            escribirObjeto((Map<?, ?>) valor, sb, nivel);
        } else if (valor instanceof List) {
            escribirArreglo((List<?>) valor, sb, nivel);
        } else if (valor instanceof String) {
            escribirCadena((String) valor, sb);
        } else if (valor instanceof Boolean) {
            sb.append(valor.toString());
        } else if (valor instanceof Number) {
            escribirNumero((Number) valor, sb);
        } else {
            throw new IllegalArgumentException("Tipo no soportado: " + valor.getClass());
        }
    }

    private static void escribirObjeto(Map<?, ?> mapa, StringBuilder sb, int nivel) {
        if (mapa.isEmpty()) {
            sb.append("{}");
            return;
        }
        sb.append("{\n");
        int i = 0;
        int total = mapa.size();
        for (Map.Entry<?, ?> entrada : mapa.entrySet()) {
            indentar(sb, nivel + 1);
            escribirCadena(String.valueOf(entrada.getKey()), sb);
            sb.append(": ");
            escribirValor(entrada.getValue(), sb, nivel + 1);
            if (++i < total) {
                sb.append(',');
            }
            sb.append('\n');
        }
        indentar(sb, nivel);
        sb.append('}');
    }

    private static void escribirArreglo(List<?> lista, StringBuilder sb, int nivel) {
        if (lista.isEmpty()) {
            sb.append("[]");
            return;
        }
        sb.append("[\n");
        for (int i = 0; i < lista.size(); i++) {
            indentar(sb, nivel + 1);
            escribirValor(lista.get(i), sb, nivel + 1);
            if (i < lista.size() - 1) {
                sb.append(',');
            }
            sb.append('\n');
        }
        indentar(sb, nivel);
        sb.append(']');
    }

    private static void escribirCadena(String texto, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }

    private static void escribirNumero(Number numero, StringBuilder sb) {
        double d = numero.doubleValue();
        if (d == Math.rint(d) && !Double.isInfinite(d)) {
            sb.append((long) d);
        } else {
            sb.append(d);
        }
    }

    private static void indentar(StringBuilder sb, int nivel) {
        sb.append("  ".repeat(nivel));
    }
}
