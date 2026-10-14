import Catalogo.Categoria;
import Catalogo.Producto;
import Exepciones.AccesoDenegadoException;
import Servicios.RepositorioCategoriaJson;
import Servicios.RepositorioCuponJson;
import Servicios.RepositorioProductoJson;
import clasedescuentos.Cupon;
import clasedescuentos.TipoDescuento;
import claseseguridad.Permiso;
import claseseguridad.Usuario;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MenuAdministrador {

    private final Scanner scanner;
    private final RepositorioProductoJson repositorioProductos;
    private final RepositorioCategoriaJson repositorioCategorias;
    private final RepositorioCuponJson repositorioCupones;

    public MenuAdministrador(Scanner scanner, RepositorioProductoJson repositorioProductos,
                             RepositorioCategoriaJson repositorioCategorias, RepositorioCuponJson repositorioCupones) {
        this.scanner = scanner;
        this.repositorioProductos = repositorioProductos;
        this.repositorioCategorias = repositorioCategorias;
        this.repositorioCupones = repositorioCupones;
    }

    public void mostrar(Usuario usuario) {
        try {
            usuario.verificarPermiso(new Permiso(Permiso.GESTIONAR_PRODUCTOS));
        } catch (AccesoDenegadoException e) {
            System.out.println("Acceso denegado: " + e.getMessage());
            return;
        }

        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("===== PANEL DE ADMINISTRADOR =====");
            System.out.println("1. Listar productos");
            System.out.println("2. Agregar producto");
            System.out.println("3. Editar producto");
            System.out.println("4. Cambiar precio");
            System.out.println("5. Cambiar stock");
            System.out.println("6. Activar / desactivar producto");
            System.out.println("7. Eliminar producto");
            System.out.println("8. Gestionar cupones");
            System.out.println("0. Volver");
            System.out.print("Elige una opcion: ");
            String opcion = scanner.nextLine().trim();

            try {
                switch (opcion) {
                    case "1":
                        listarProductos();
                        break;
                    case "2":
                        agregarProducto();
                        break;
                    case "3":
                        editarProducto();
                        break;
                    case "4":
                        cambiarPrecio();
                        break;
                    case "5":
                        cambiarStock();
                        break;
                    case "6":
                        activarDesactivarProducto();
                        break;
                    case "7":
                        eliminarProducto();
                        break;
                    case "8":
                        usuario.verificarPermiso(new Permiso(Permiso.GESTIONAR_CUPONES));
                        menuCupones();
                        break;
                    case "0":
                        volver = true;
                        break;
                    default:
                        System.out.println("Opcion invalida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (AccesoDenegadoException e) {
                System.out.println("Acceso denegado: " + e.getMessage());
            }
        }
    }

    // ---------- Productos ----------

    private void listarProductos() {
        List<Producto> productos = repositorioProductos.listarTodos();
        if (productos.isEmpty()) {
            System.out.println("No hay productos.");
            return;
        }
        for (Producto producto : productos) {
            String categoria = producto.getCategoria() == null ? "Sin categoria" : producto.getCategoria().getNombre();
            System.out.printf("ID %d | %s | %s | [%s] | $%.2f | Stock: %d | %s%n",
                    producto.getId(), producto.getSku(), producto.getNombre(), categoria,
                    producto.getPrecioUnitario(), producto.getStock(),
                    producto.isActivo() ? "ACTIVO" : "INACTIVO");
        }
    }

    private void agregarProducto() {
        String nombre = leerTextoObligatorio("Nombre: ");
        String sku = leerTextoObligatorio("SKU: ").toUpperCase();
        boolean skuRepetido = repositorioProductos.listarTodos().stream()
                .anyMatch(p -> sku.equalsIgnoreCase(p.getSku()));
        if (skuRepetido) {
            throw new IllegalArgumentException("Ya existe un producto con el SKU " + sku);
        }
        System.out.print("Descripcion: ");
        String descripcion = scanner.nextLine().trim();
        double precio = leerDouble("Precio: ");
        int stock = leerEntero("Stock inicial: ");
        Categoria categoria = elegirCategoria();
        System.out.print("Capacidad (opcional, Enter para omitir): ");
        String capacidad = scanner.nextLine().trim();

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setSku(sku);
        producto.setDescripcion(descripcion);
        producto.cambiarPrecio(precio);
        producto.setPrecioBase(precio);
        producto.actualizarStock(stock);
        producto.setTasaImpuesto(0.16);
        producto.setMarca("Anahuac");
        producto.setImagen("");
        producto.setCategoria(categoria);
        producto.setCapacidad(capacidad);
        producto.setDimensiones("");
        producto.activar();

        repositorioProductos.guardar(producto);
        System.out.println("Producto agregado con ID " + producto.getId() + ".");
    }

    private void editarProducto() {
        Producto producto = elegirProducto();
        if (producto == null) {
            return;
        }
        System.out.println("Deja vacio y presiona Enter para conservar el valor actual.");
        producto.setNombre(leerTextoOpcional("Nombre", producto.getNombre()));
        producto.setDescripcion(leerTextoOpcional("Descripcion", producto.getDescripcion()));
        producto.setCapacidad(leerTextoOpcional("Capacidad", producto.getCapacidad()));

        System.out.print("Cambiar categoria? (s/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
            producto.setCategoria(elegirCategoria());
        }

        repositorioProductos.guardar(producto);
        System.out.println("Producto actualizado.");
    }

    private void cambiarPrecio() {
        Producto producto = elegirProducto();
        if (producto == null) {
            return;
        }
        System.out.printf("Precio actual: $%.2f%n", producto.getPrecioUnitario());
        double nuevoPrecio = leerDouble("Nuevo precio: ");
        producto.cambiarPrecio(nuevoPrecio);
        repositorioProductos.guardar(producto);
        System.out.println("Precio actualizado.");
    }

    private void cambiarStock() {
        Producto producto = elegirProducto();
        if (producto == null) {
            return;
        }
        System.out.println("Stock actual: " + producto.getStock());
        int nuevoStock = leerEntero("Nuevo stock: ");
        producto.actualizarStock(nuevoStock);
        repositorioProductos.guardar(producto);
        System.out.println("Stock actualizado.");
    }

    private void activarDesactivarProducto() {
        Producto producto = elegirProducto();
        if (producto == null) {
            return;
        }
        if (producto.isActivo()) {
            producto.desactivar();
            System.out.println("Producto desactivado: ya no aparece en el catalogo.");
        } else {
            producto.activar();
            System.out.println("Producto activado: vuelve a aparecer en el catalogo.");
        }
        repositorioProductos.guardar(producto);
    }

    private void eliminarProducto() {
        Producto producto = elegirProducto();
        if (producto == null) {
            return;
        }
        System.out.print("Seguro que quieres eliminar \"" + producto.getNombre() + "\"? (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Cancelado.");
            return;
        }
        repositorioProductos.eliminar(producto.getId());
        System.out.println("Producto eliminado.");
    }

    private Producto elegirProducto() {
        listarProductos();
        System.out.print("ID del producto (0 para cancelar): ");
        long id = leerEnteroSinValidar();
        if (id == 0) {
            return null;
        }
        Producto producto = repositorioProductos.buscarPorId(id).orElse(null);
        if (producto == null) {
            System.out.println("No existe un producto con ese ID.");
        }
        return producto;
    }

    private Categoria elegirCategoria() {
        List<Categoria> categorias = repositorioCategorias.listarTodos();
        for (Categoria categoria : categorias) {
            System.out.println(categoria.getId() + ". " + categoria.getNombre());
        }
        long id = leerEntero("ID de la categoria: ");
        return repositorioCategorias.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la categoria " + id));
    }

    // ---------- Cupones ----------

    private void menuCupones() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("----- CUPONES -----");
            System.out.println("1. Listar cupones");
            System.out.println("2. Crear cupon");
            System.out.println("3. Activar / desactivar cupon");
            System.out.println("4. Eliminar cupon");
            System.out.println("0. Volver");
            System.out.print("Elige una opcion: ");
            String opcion = scanner.nextLine().trim();

            try {
                switch (opcion) {
                    case "1":
                        listarCupones();
                        break;
                    case "2":
                        crearCupon();
                        break;
                    case "3":
                        activarDesactivarCupon();
                        break;
                    case "4":
                        eliminarCupon();
                        break;
                    case "0":
                        volver = true;
                        break;
                    default:
                        System.out.println("Opcion invalida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void listarCupones() {
        List<Cupon> cupones = repositorioCupones.listarTodos();
        if (cupones.isEmpty()) {
            System.out.println("No hay cupones.");
            return;
        }
        for (Cupon cupon : cupones) {
            String valor = cupon.getTipoDescuento() == TipoDescuento.PORCENTAJE
                    ? String.format("%.0f%%", cupon.getValor())
                    : String.format("$%.2f", cupon.getValor());
            System.out.printf("ID %d | %s | %s | %s a %s | %s%n",
                    cupon.getId(), cupon.getCodigo(), valor,
                    cupon.getFechaInicio(), cupon.getFechaFin(),
                    cupon.esValido() ? "VIGENTE" : (cupon.isActivo() ? "FUERA DE FECHA" : "INACTIVO"));
        }
    }

    private void crearCupon() {
        String codigo = leerTextoObligatorio("Codigo (ej. ANAHUAC10): ").toUpperCase();
        if (repositorioCupones.buscarPorCodigo(codigo).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cupon con el codigo " + codigo);
        }

        System.out.println("Tipo de descuento: 1. Porcentaje  2. Monto fijo");
        int tipo = leerEntero("Opcion: ");
        TipoDescuento tipoDescuento;
        if (tipo == 1) {
            tipoDescuento = TipoDescuento.PORCENTAJE;
        } else if (tipo == 2) {
            tipoDescuento = TipoDescuento.MONTO_FIJO;
        } else {
            throw new IllegalArgumentException("Tipo de descuento invalido");
        }

        double valor = leerDouble(tipoDescuento == TipoDescuento.PORCENTAJE ? "Porcentaje (1-100): " : "Monto en MXN: ");
        if (valor <= 0 || (tipoDescuento == TipoDescuento.PORCENTAJE && valor > 100)) {
            throw new IllegalArgumentException("Valor de descuento invalido");
        }

        LocalDate inicio = leerFecha("Fecha de inicio (AAAA-MM-DD): ");
        LocalDate fin = leerFecha("Fecha de fin (AAAA-MM-DD): ");
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser antes de la de inicio");
        }

        Cupon cupon = new Cupon();
        cupon.setCodigo(codigo);
        cupon.setTipoDescuento(tipoDescuento);
        cupon.setValor(valor);
        cupon.setFechaInicio(inicio);
        cupon.setFechaFin(fin);
        cupon.setActivo(true);
        repositorioCupones.guardar(cupon);
        System.out.println("Cupon " + codigo + " creado.");
    }

    private void activarDesactivarCupon() {
        Cupon cupon = elegirCupon();
        if (cupon == null) {
            return;
        }
        cupon.setActivo(!cupon.isActivo());
        repositorioCupones.guardar(cupon);
        System.out.println("Cupon " + (cupon.isActivo() ? "activado." : "desactivado."));
    }

    private void eliminarCupon() {
        Cupon cupon = elegirCupon();
        if (cupon == null) {
            return;
        }
        System.out.print("Seguro que quieres eliminar el cupon " + cupon.getCodigo() + "? (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Cancelado.");
            return;
        }
        repositorioCupones.eliminar(cupon.getId());
        System.out.println("Cupon eliminado.");
    }

    private Cupon elegirCupon() {
        listarCupones();
        System.out.print("ID del cupon (0 para cancelar): ");
        long id = leerEnteroSinValidar();
        if (id == 0) {
            return null;
        }
        Cupon cupon = repositorioCupones.buscarPorId(id).orElse(null);
        if (cupon == null) {
            System.out.println("No existe un cupon con ese ID.");
        }
        return cupon;
    }

    // ---------- Lectura de datos ----------

    private String leerTextoObligatorio(String mensaje) {
        System.out.print(mensaje);
        String texto = scanner.nextLine().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Este campo es obligatorio");
        }
        return texto;
    }

    private String leerTextoOpcional(String campo, String valorActual) {
        System.out.print(campo + " [" + (valorActual == null ? "" : valorActual) + "]: ");
        String texto = scanner.nextLine().trim();
        return texto.isEmpty() ? valorActual : texto;
    }

    private double leerDouble(String mensaje) {
        System.out.print(mensaje);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debes escribir un numero");
        }
    }

    private int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debes escribir un numero entero");
        }
    }

    private long leerEnteroSinValidar() {
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private LocalDate leerFecha(String mensaje) {
        System.out.print(mensaje);
        try {
            return LocalDate.parse(scanner.nextLine().trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha invalida, usa el formato AAAA-MM-DD");
        }
    }
}
