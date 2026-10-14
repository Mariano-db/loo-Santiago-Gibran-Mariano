package Api;

import Catalogo.Categoria;
import Catalogo.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ControladorCatalogo {

    private final ContextoApi contexto;

    public ControladorCatalogo(ContextoApi contexto) {
        this.contexto = contexto;
    }

    public Respuesta categorias(Peticion peticion) {
        List<Object> resultado = new ArrayList<>();
        for (Categoria categoria : contexto.categorias.listarTodos()) {
            resultado.add(Mapeadores.categoria(categoria));
        }
        return Respuesta.ok(resultado);
    }

    public Respuesta productos(Peticion peticion) {
        Long categoriaId = null;
        String categoriaTexto = peticion.getConsulta("categoriaId");
        if (categoriaTexto != null && !categoriaTexto.isBlank()) {
            try {
                categoriaId = Long.parseLong(categoriaTexto.trim());
            } catch (NumberFormatException e) {
                throw new ApiException(400, "categoriaId debe ser un numero entero");
            }
        }
        String busqueda = peticion.getConsulta("q");
        String buscado = busqueda == null ? "" : busqueda.trim().toLowerCase(Locale.ROOT);

        List<Object> resultado = new ArrayList<>();
        for (Producto producto : contexto.productos.listarTodos()) {
            if (!producto.isActivo()) {
                continue;
            }
            if (categoriaId != null && (producto.getCategoria() == null
                    || !categoriaId.equals(producto.getCategoria().getId()))) {
                continue;
            }
            if (!buscado.isEmpty() && !coincide(producto, buscado)) {
                continue;
            }
            resultado.add(Mapeadores.producto(producto, false));
        }
        return Respuesta.ok(resultado);
    }

    public Respuesta producto(Peticion peticion) {
        long id = peticion.idRuta("id");
        Producto producto = contexto.productos.buscarPorId(id)
                .filter(Producto::isActivo)
                .orElseThrow(() -> new ApiException(404, "No existe el producto " + id));
        return Respuesta.ok(Mapeadores.producto(producto, false));
    }

    private boolean coincide(Producto producto, String buscado) {
        return contiene(producto.getNombre(), buscado)
                || contiene(producto.getDescripcion(), buscado)
                || contiene(producto.getSku(), buscado);
    }

    private boolean contiene(String texto, String buscado) {
        return texto != null && texto.toLowerCase(Locale.ROOT).contains(buscado);
    }
}
