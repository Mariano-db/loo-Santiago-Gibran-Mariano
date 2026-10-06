package Panel;

import Catalogo.VarianteProducto;

final class Descripciones {

    private Descripciones() {
    }

    static String variante(VarianteProducto variante) {
        if (variante == null) {
            return "";
        }
        return String.format(" (color: %s, talla: %s)", variante.getColor(), variante.getTalla());
    }
}
