package Panel;

import Catalogo.Categoria;
import Catalogo.EstadoProducto;
import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Servicios.Almacen;

import java.util.List;
import java.util.Optional;

public class PanelProductos {

    private static final double IVA_POR_DEFECTO = 0.16;

    private final Entrada in;
    private final Almacen almacen;

    public PanelProductos(Entrada in, Almacen almacen) {
        this.in = in;
        this.almacen = almacen;
    }

    public void menuProductos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("--- Productos ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear producto (sin variantes)");
            System.out.println("3. Editar (nombre, descripcion, precio)");
            System.out.println("4. Activar / desactivar");
            System.out.println("5. Eliminar");
            System.out.println("0. Volver");
            switch (in.texto("Elige una opcion: ")) {
                case "1": listar(); break;
                case "2": crear(); break;
                case "3": editar(); break;
                case "4": alternarVisibilidad(); break;
                case "5": eliminar(); break;
                case "0": volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private void listar() {
        List<Producto> lista = almacen.productos.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay productos.");
            return;
        }
        for (Producto p : lista) {
            String categoria = p.getCategoria() == null ? "Sin categoria" : p.getCategoria().getNombre();
            System.out.printf("[%d] %s - $%.2f - stock: %d - %s - %s%s%n", p.getId(), p.getNombre(), p.getPrecioUnitario(),
                    p.getStock(), categoria, p.isActivo() ? "visible" : "OCULTO",
                    p.getVariantes().isEmpty() ? "" : " - " + p.getVariantes().size() + " variantes");
        }
    }

    private void crear() {
        String nombre = in.texto("Nombre: ");
        String sku = in.texto("SKU: ");
        if (nombre.isBlank() || sku.isBlank()) {
            System.out.println("El nombre y el SKU no pueden estar vacios.");
            return;
        }
        if (almacen.productos.listarTodos().stream().anyMatch(p -> sku.equalsIgnoreCase(p.getSku()))) {
            System.out.println("Ya existe un producto con ese SKU.");
            return;
        }
        Double precio = in.decimal("Precio (MXN): ");
        Integer stock = in.entero("Stock inicial: ");
        if (precio == null || precio < 0 || stock == null || stock < 0) {
            System.out.println("Precio y stock deben ser numeros no negativos.");
            return;
        }
        Categoria categoria = elegirCategoria();
        if (categoria == null) {
            return;
        }

        Producto p = new Producto();
        p.setNombre(nombre);
        p.setSku(sku);
        p.setDescripcion(in.texto("Descripcion (opcional): "));
        p.setPrecioBase(precio);
        p.setPrecioUnitario(precio);
        p.setTasaImpuesto(IVA_POR_DEFECTO);
        p.setMarca("Anahuac");
        p.setImagen("");
        p.setCapacidad("");
        p.setDimensiones("");
        p.setCategoria(categoria);
        p.setEstado(EstadoProducto.ACTIVO);
        p.setActivo(true);
        almacen.productos.guardar(p);
        almacen.servicioInventario.registrarProducto(p);
        if (stock > 0) {
            almacen.servicioInventario.reabastecer(p.getId(), null, stock, "Stock inicial", null);
        }
        System.out.println("Producto creado con id " + p.getId() + ".");
    }

    private void editar() {
        Producto p = elegirProducto();
        if (p == null) {
            return;
        }
        System.out.println("Deja vacio lo que no quieras cambiar.");
        String nombre = in.texto("Nombre [" + p.getNombre() + "]: ");
        if (!nombre.isBlank()) {
            p.setNombre(nombre);
        }
        String descripcion = in.texto("Descripcion: ");
        if (!descripcion.isBlank()) {
            p.setDescripcion(descripcion);
        }
        String precioTexto = in.texto(String.format("Precio [%.2f]: ", p.getPrecioUnitario()));
        if (!precioTexto.isBlank()) {
            cambiarPrecio(p, precioTexto);
        }
        almacen.productos.guardar(p);
        System.out.println("Producto actualizado. (El stock se cambia en Inventario.)");
    }

    private void cambiarPrecio(Producto p, String texto) {
        try {
            double precio = Double.parseDouble(texto.replace(",", "."));
            p.setPrecioBase(precio);
            p.setPrecioUnitario(precio);

            for (VarianteProducto v : p.getVariantes()) {
                v.setPrecioVenta(precio);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Precio invalido, no se cambio.");
        }
    }

    private void alternarVisibilidad() {
        Producto p = elegirProducto();
        if (p == null) {
            return;
        }
        if (p.isActivo()) {
            p.desactivar();
        } else {
            p.activar();
        }
        almacen.productos.guardar(p);
        System.out.println(p.getNombre() + " ahora esta " + (p.isActivo() ? "visible" : "oculto") + " para los clientes.");
    }

    private void eliminar() {
        Producto p = elegirProducto();
        if (p != null && in.confirmar("Eliminar \"" + p.getNombre() + "\" del catalogo")) {
            almacen.productos.eliminar(p.getId());
            almacen.servicioInventario.eliminarProducto(p.getId());
            System.out.println("Producto eliminado. Los pedidos anteriores conservan su informacion.");
        }
    }

    private Producto elegirProducto() {
        listar();
        Long id = in.id("Id del producto (vacio para cancelar): ");
        if (id == null) {
            return null;
        }
        Optional<Producto> p = almacen.productos.buscarPorId(id);
        if (p.isEmpty()) {
            System.out.println("Producto no encontrado.");
        }
        return p.orElse(null);
    }

    public void menuCategorias() {
        System.out.println("--- Categorias ---");
        for (Categoria c : almacen.categorias.listarTodos()) {
            System.out.printf("[%d] %s - %s%n", c.getId(), c.getNombre(), c.getDescripcion());
        }
        if (!in.confirmar("Crear una categoria nueva")) {
            return;
        }
        String nombre = in.texto("Nombre: ");
        if (nombre.isBlank()) {
            System.out.println("El nombre no puede estar vacio.");
            return;
        }
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(in.texto("Descripcion: "));
        almacen.categorias.guardar(c);
        System.out.println("Categoria creada con id " + c.getId() + ".");
    }

    private Categoria elegirCategoria() {
        List<Categoria> lista = almacen.categorias.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Primero crea una categoria.");
            return null;
        }
        for (Categoria c : lista) {
            System.out.printf("  [%d] %s%n", c.getId(), c.getNombre());
        }
        Long id = in.id("Id de la categoria: ");
        Optional<Categoria> elegida = id == null ? Optional.empty() : almacen.categorias.buscarPorId(id);
        if (elegida.isEmpty()) {
            System.out.println("Categoria no encontrada.");
        }
        return elegida.orElse(null);
    }
}
