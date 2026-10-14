import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Exepciones.AccesoDenegadoException;
import Exepciones.CarritoVacioException;
import Exepciones.PagoInvalidoException;
import Exepciones.StockInsuficienteException;
import Servicios.RepositorioCategoriaJson;
import Servicios.RepositorioCuponJson;
import Servicios.RepositorioPedidoJson;
import Servicios.RepositorioProductoJson;
import Servicios.RepositorioUsuarioJson;
import Servicios.ServicioClientes;
import Servicios.ServicioPagos;
import Servicios.ServicioPedidos;
import Utils.ConfiguracionSistema;
import Ventas.DetallePedido;
import Ventas.Pedido;
import clasecarrostotal.CarritoCompra;
import clasecarrostotal.ItemCarrito;
import clasedescuentos.Cupon;
import clasepagos.EstrategiaPago;
import clasepagos.PagoEfectivo;
import clasepagos.PagoPaypal;
import clasepagos.PagoTarjeta;
import clasepagos.PagoTransferencia;
import claseseguridad.Cliente;
import claseseguridad.Permiso;
import claseseguridad.TipoCliente;
import claseseguridad.Usuario;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Main {

    public static void main(String[] args) {
        ConfiguracionSistema config = ConfiguracionSistema.getInstancia();
        config.setNombreTienda("A-Store");
        config.setMoneda("MXN");
        config.setTasaImpuestoDefault(0.16);

        RepositorioCategoriaJson repositorioCategorias = new RepositorioCategoriaJson("data/categorias.json");
        RepositorioProductoJson repositorioProductos = new RepositorioProductoJson("data/productos.json", repositorioCategorias);
        RepositorioUsuarioJson repositorioUsuarios = new RepositorioUsuarioJson("data/usuarios.json");
        RepositorioCuponJson repositorioCupones = new RepositorioCuponJson("data/cupones.json");
        RepositorioPedidoJson repositorioPedidos = new RepositorioPedidoJson("data/pedidos.json");
        ServicioClientes servicioClientes = new ServicioClientes(repositorioUsuarios);
        ServicioPedidos servicioPedidos = new ServicioPedidos(repositorioProductos, repositorioPedidos, new ServicioPagos());

        System.out.println("========================================");
        System.out.println(" " + config.getNombreTienda() + " - Universidad Anahuac Cancun");
        System.out.println("========================================");
        System.out.println("Sistema iniciado correctamente.");
        System.out.println("Moneda: " + config.getMoneda());
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        MenuAdministrador menuAdministrador = new MenuAdministrador(scanner, repositorioProductos, repositorioCategorias, repositorioCupones);


        if (!servicioClientes.hayUsuarios()) {
            crearAdministradorInicial(scanner, servicioClientes);
        }

        Usuario usuarioActual = pantallaInicio(scanner, servicioClientes);
        CarritoCompra carrito = nuevoCarrito(usuarioActual);
        boolean salir = usuarioActual == null;
        if (salir) {
            System.out.println("Hasta luego.");
        }

        while (!salir) {
            mostrarMenu(usuarioActual);
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    mostrarCatalogo(repositorioProductos);
                    break;
                case "2":
                    agregarAlCarrito(scanner, repositorioProductos, carrito);
                    break;
                case "3":
                    mostrarCarrito(carrito);
                    break;
                case "4":
                    eliminarDelCarrito(scanner, carrito);
                    break;
                case "5":
                    aplicarCupon(scanner, repositorioCupones, carrito);
                    break;
                case "6":
                    carrito = finalizarCompra(scanner, carrito, usuarioActual, servicioPedidos);
                    break;
                case "7":
                    verMisPedidos(usuarioActual, servicioPedidos);
                    break;
                case "8":
                    servicioClientes.cerrarSesion(usuarioActual);
                    System.out.println("Sesion cerrada.");
                    System.out.println();
                    usuarioActual = pantallaInicio(scanner, servicioClientes);
                    if (usuarioActual == null) {
                        salir = true;
                        System.out.println("Hasta luego.");
                    } else {
                        carrito = nuevoCarrito(usuarioActual);
                    }
                    break;
                case "9":
                    verUsuarios(usuarioActual, servicioClientes);
                    break;
                case "10":
                    menuAdministrador.mostrar(usuarioActual);
                    break;
                case "0":
                    servicioClientes.cerrarSesion(usuarioActual);
                    salir = true;
                    System.out.println("Hasta luego.");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
            System.out.println();
        }

        scanner.close();
    }

    private static Usuario pantallaInicio(Scanner scanner, ServicioClientes servicioClientes) {
        while (true) {
            System.out.println("1. Iniciar sesion");
            System.out.println("2. Registrarse");
            System.out.println("3. Seguir como invitado");
            System.out.println("0. Salir");
            System.out.print("Elige una opcion: ");
            String opcion = scanner.nextLine().trim();

            Usuario usuario = null;
            switch (opcion) {
                case "1":
                    usuario = iniciarSesion(scanner, servicioClientes);
                    break;
                case "2":
                    usuario = registrarse(scanner, servicioClientes);
                    break;
                case "3":
                    usuario = crearInvitado();
                    break;
                case "0":
                    return null;
                default:
                    System.out.println("Opcion invalida.");
            }
            System.out.println();
            if (usuario != null) {
                return usuario;
            }
        }
    }

    private static Usuario iniciarSesion(Scanner scanner, ServicioClientes servicioClientes) {
        System.out.print("Correo: ");
        String email = scanner.nextLine();
        System.out.print("Contrasena: ");
        String password = scanner.nextLine();

        Optional<Usuario> usuario = servicioClientes.iniciarSesion(email, password);
        if (usuario.isEmpty()) {
            System.out.println("Correo o contrasena incorrectos.");
            return null;
        }
        System.out.println("Bienvenido, " + usuario.get().getNombre() + ".");
        return usuario.get();
    }

    private static Usuario registrarse(Scanner scanner, ServicioClientes servicioClientes) {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Correo: ");
        String email = scanner.nextLine();
        System.out.print("Contrasena (min. 8 caracteres, con letras y numeros): ");
        String password = scanner.nextLine();

        try {
            Cliente cliente = servicioClientes.registrarCliente(nombre, email, password);
            cliente.iniciarSesion(password);
            System.out.println("Cuenta creada. Bienvenido, " + cliente.getNombre() + ".");
            return cliente;
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo registrar: " + e.getMessage());
            return null;
        }
    }

    private static Cliente crearInvitado() {
        Cliente invitado = new Cliente();
        invitado.setNombre("Invitado");
        invitado.setTipo(TipoCliente.INVITADO);
        System.out.println("Entraste como invitado.");
        return invitado;
    }

    private static void crearAdministradorInicial(Scanner scanner, ServicioClientes servicioClientes) {
        System.out.println("No hay usuarios registrados. Crea la cuenta del administrador inicial.");
        while (true) {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Correo: ");
            String email = scanner.nextLine();
            System.out.print("Contrasena (min. 8 caracteres, con letras y numeros): ");
            String password = scanner.nextLine();
            try {
                servicioClientes.registrarAdministrador(nombre, email, password);
                System.out.println("Administrador creado. Ya puedes iniciar sesion.");
                System.out.println();
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static CarritoCompra nuevoCarrito(Usuario usuario) {
        CarritoCompra carrito = new CarritoCompra();
        if (usuario instanceof Cliente) {
            carrito.setCliente((Cliente) usuario);
        }
        return carrito;
    }

    private static void verUsuarios(Usuario usuarioActual, ServicioClientes servicioClientes) {
        try {
            usuarioActual.verificarPermiso(new Permiso(Permiso.GESTIONAR_USUARIOS));
        } catch (AccesoDenegadoException e) {
            System.out.println("Acceso denegado: " + e.getMessage());
            return;
        }
        for (Usuario usuario : servicioClientes.listarUsuarios()) {
            System.out.printf("%d. %s <%s> [%s]%s%n",
                    usuario.getId(), usuario.getNombre(), usuario.getEmail(),
                    descripcionRol(usuario), usuario.isActivo() ? "" : " (inactivo)");
        }
    }

    private static String descripcionRol(Usuario usuario) {
        return usuario.getRol() == null ? "INVITADO" : usuario.getRol().getNombre();
    }

    private static void mostrarMenu(Usuario usuarioActual) {
        System.out.println("Sesion: " + usuarioActual.getNombre() + " [" + descripcionRol(usuarioActual) + "]");
        System.out.println("1. Ver catalogo");
        System.out.println("2. Agregar producto al carrito");
        System.out.println("3. Ver carrito");
        System.out.println("4. Eliminar producto del carrito");
        System.out.println("5. Aplicar cupon de descuento");
        System.out.println("6. Finalizar compra");
        System.out.println("7. Mis pedidos");
        System.out.println("8. Cerrar sesion");
        if (usuarioActual.isSesionActiva() && usuarioActual.tienePermiso(new Permiso(Permiso.GESTIONAR_USUARIOS))) {
            System.out.println("9. Ver usuarios registrados");
        }
        if (usuarioActual.isSesionActiva() && usuarioActual.tienePermiso(new Permiso(Permiso.GESTIONAR_PRODUCTOS))) {
            System.out.println("10. Panel de administrador");
        }
        System.out.println("0. Salir");
        System.out.print("Elige una opcion: ");
    }

    private static List<Producto> productosActivos(RepositorioProductoJson repositorioProductos) {
        return repositorioProductos.listarTodos().stream()
                .filter(Producto::isActivo)
                .collect(Collectors.toList());
    }

    private static void mostrarCatalogo(RepositorioProductoJson repositorioProductos) {
              List<Producto> productos = productosActivos(repositorioProductos);
        if (productos.isEmpty()) {
            System.out.println("El catalogo esta vacio.");
            return;
        }
        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            String categoria = producto.getCategoria() == null ? "Sin categoria" : producto.getCategoria().getNombre();
            System.out.printf("%d. [%s] %s - $%.2f MXN - Stock: %d%n",
                    i + 1, categoria, producto.getNombre(), producto.getPrecioUnitario(), producto.getStock());

            List<VarianteProducto> variantes = producto.getVariantes();
            if (!variantes.isEmpty()) {
                for (VarianteProducto variante : variantes) {
                    System.out.printf("    - %s | color: %s | talla: %s%n",
                            variante.getSku(), variante.getColor(), variante.getTalla());
                }
            }
        }
    }

    private static void agregarAlCarrito(Scanner scanner, RepositorioProductoJson repositorioProductos, CarritoCompra carrito) {
                  List<Producto> productos = productosActivos(repositorioProductos);
        if (productos.isEmpty()) {
            System.out.println("El catalogo esta vacio.");
            return;
        }

        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            System.out.printf("%d. %s - $%.2f MXN%n", i + 1, producto.getNombre(), producto.getPrecioUnitario());
        }
        System.out.print("Numero de producto a agregar (0 para cancelar): ");
        int seleccion = leerEntero(scanner);
        if (seleccion == 0) {
            return;
        }
        if (seleccion < 1 || seleccion > productos.size()) {
            System.out.println("Numero invalido.");
            return;
        }
        Producto producto = productos.get(seleccion - 1);

        VarianteProducto variante = null;
        List<VarianteProducto> variantes = producto.getVariantes();
        if (!variantes.isEmpty()) {
            for (int i = 0; i < variantes.size(); i++) {
                VarianteProducto v = variantes.get(i);
                System.out.printf("%d. color: %s - talla: %s%n", i + 1, v.getColor(), v.getTalla());
            }
            System.out.print("Elige una variante (color/talla): ");
            int seleccionVariante = leerEntero(scanner);
            if (seleccionVariante < 1 || seleccionVariante > variantes.size()) {
                System.out.println("Variante invalida, se cancela.");
                return;
            }
            variante = variantes.get(seleccionVariante - 1);
        }

        System.out.print("Cantidad: ");
        int cantidad = leerEntero(scanner);
        if (cantidad <= 0) {
            System.out.println("Cantidad invalida.");
            return;
        }

        try {
            carrito.agregarProducto(producto, variante, cantidad);
            System.out.println("Producto agregado al carrito.");
        } catch (StockInsuficienteException e) {
            System.out.println("No se pudo agregar: " + e.getMessage());
        }
    }

    private static void mostrarCarrito(CarritoCompra carrito) {
        List<ItemCarrito> items = carrito.getItems();
        if (items.isEmpty()) {
            System.out.println("El carrito esta vacio.");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            ItemCarrito item = items.get(i);
            String variante = descripcionVariante(item.getVariante());
            System.out.printf("%d. %s%s x%d - $%.2f c/u - Subtotal: $%.2f%n",
                    i + 1, item.getProducto().getNombre(), variante, item.getCantidad(),
                    item.getPrecioUnitario(), item.subtotal());
        }
        System.out.printf("Subtotal: $%.2f MXN%n", carrito.calcularTotal());
        if (carrito.getCupon() != null) {
            System.out.printf("Cupon %s: -$%.2f MXN%n", carrito.getCupon().getCodigo(),
                    carrito.calcularTotal() - carrito.totalConDescuento());
        }
        System.out.printf("Total: $%.2f MXN%n", carrito.totalConDescuento());
    }

    private static void eliminarDelCarrito(Scanner scanner, CarritoCompra carrito) {
        List<ItemCarrito> items = carrito.getItems();
        if (items.isEmpty()) {
            System.out.println("El carrito esta vacio.");
            return;
        }
        mostrarCarrito(carrito);
        System.out.print("Numero de item a eliminar (0 para cancelar): ");
        int seleccion = leerEntero(scanner);
        if (seleccion == 0) {
            return;
        }
        if (seleccion < 1 || seleccion > items.size()) {
            System.out.println("Numero invalido.");
            return;
        }
        carrito.eliminarProducto(seleccion - 1);
        System.out.println("Producto eliminado del carrito.");
    }

    private static void aplicarCupon(Scanner scanner, RepositorioCuponJson repositorioCupones, CarritoCompra carrito) {
        if (carrito.getCupon() != null) {
            System.out.println("Ya tienes el cupon " + carrito.getCupon().getCodigo() + " aplicado.");
            System.out.print("Quitarlo? (s/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                carrito.quitarCupon();
                System.out.println("Cupon quitado.");
            }
            return;
        }
        System.out.print("Codigo del cupon (Enter para cancelar): ");
        String codigo = scanner.nextLine().trim();
        if (codigo.isEmpty()) {
            return;
        }
        Optional<Cupon> cupon = repositorioCupones.buscarPorCodigo(codigo);
        if (cupon.isEmpty()) {
            System.out.println("Ese cupon no existe.");
            return;
        }
        try {
            carrito.aplicarCupon(cupon.get());
            System.out.printf("Cupon %s aplicado. Descuento actual: $%.2f MXN%n", cupon.get().getCodigo(),
                    carrito.calcularTotal() - carrito.totalConDescuento());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static CarritoCompra finalizarCompra(Scanner scanner, CarritoCompra carrito, Usuario usuario,
                                                 ServicioPedidos servicioPedidos) {
        if (!(usuario instanceof Cliente)) {
            System.out.println("Las cuentas de " + descripcionRol(usuario).toLowerCase()
                    + " no pueden comprar. Entra con una cuenta de cliente.");
            return carrito;
        }
        try {
            carrito.validarNoVacio();
        } catch (CarritoVacioException e) {
            System.out.println("No se puede finalizar la compra: " + e.getMessage());
            return carrito;
        }

        mostrarCarrito(carrito);
        double total = carrito.totalConDescuento();
        EstrategiaPago pago = elegirMetodoPago(scanner, total);
        if (pago == null) {
            System.out.println("Compra cancelada.");
            return carrito;
        }

        try {
            Pedido pedido = servicioPedidos.finalizarCompra(carrito, pago);
            System.out.println("Pago aprobado. Pedido #" + pedido.getId() + " registrado.");
            imprimirPedido(pedido);
            if (pago instanceof PagoEfectivo && ((PagoEfectivo) pago).getCambio() > 0) {
                System.out.printf("Cambio: $%.2f MXN%n", ((PagoEfectivo) pago).getCambio());
            }
            CarritoCompra nuevoCarrito = new CarritoCompra();
            nuevoCarrito.setCliente(carrito.getCliente());
            return nuevoCarrito;
        } catch (CarritoVacioException | StockInsuficienteException | PagoInvalidoException
                 | IllegalArgumentException e) {
            System.out.println("No se pudo completar la compra: " + e.getMessage());
            return carrito;
        }
    }

    private static EstrategiaPago elegirMetodoPago(Scanner scanner, double total) {
        System.out.printf("Total a pagar: $%.2f MXN%n", total);
        System.out.println("Metodo de pago:");
        System.out.println("1. Tarjeta");
        System.out.println("2. Transferencia");
        System.out.println("3. PayPal");
        System.out.println("4. Efectivo (al recoger)");
        System.out.println("0. Cancelar");
        System.out.print("Elige una opcion: ");
        String opcion = scanner.nextLine().trim();

        switch (opcion) {
            case "1":
                PagoTarjeta tarjeta = new PagoTarjeta();
                System.out.print("Numero de tarjeta: ");
                String numero = scanner.nextLine();
                System.out.print("Mes de vencimiento (1-12): ");
                int mes = leerEntero(scanner);
                System.out.print("Anio de vencimiento (ej. 2028): ");
                int anio = leerEntero(scanner);
                System.out.print("CVV: ");
                String cvv = scanner.nextLine();
                tarjeta.setDatosTarjeta(numero, mes, anio, cvv);
                return tarjeta;
            case "2":
                PagoTransferencia transferencia = new PagoTransferencia();
                System.out.print("Referencia bancaria (6 a 20 letras o numeros): ");
                transferencia.setReferenciaBancaria(scanner.nextLine());
                return transferencia;
            case "3":
                PagoPaypal paypal = new PagoPaypal();
                System.out.print("Correo de tu cuenta PayPal: ");
                paypal.setCuentaPaypal(scanner.nextLine());
                return paypal;
            case "4":
                PagoEfectivo efectivo = new PagoEfectivo();
                System.out.print("Monto con el que pagaras: ");
                try {
                    efectivo.setMontoRecibido(Double.parseDouble(scanner.nextLine().trim()));
                } catch (NumberFormatException e) {
                    efectivo.setMontoRecibido(0);
                }
                return efectivo;
            default:
                return null;
        }
    }

    private static void verMisPedidos(Usuario usuario, ServicioPedidos servicioPedidos) {
        boolean registrado = usuario instanceof Cliente
                && ((Cliente) usuario).getTipo() == TipoCliente.REGISTRADO && usuario.getId() != 0;
        if (!registrado) {
            System.out.println("Inicia sesion con una cuenta de cliente para ver tus pedidos.");
            return;
        }
        List<Pedido> pedidos = servicioPedidos.pedidosDeCliente(usuario.getId());
        if (pedidos.isEmpty()) {
            System.out.println("Aun no tienes pedidos.");
            return;
        }
        for (Pedido pedido : pedidos) {
            imprimirPedido(pedido);
            System.out.println();
        }
    }

    private static void imprimirPedido(Pedido pedido) {
        System.out.printf("Pedido #%d - %s - %s%n", pedido.getId(),
                pedido.getFecha() == null ? "" : pedido.getFecha().toString().replace('T', ' ').substring(0, 16),
                pedido.getEstado());
        for (DetallePedido detalle : pedido.getDetalles()) {
            System.out.printf("  - %s%s x%d - $%.2f%n", detalle.getProducto().getNombre(),
                    descripcionVariante(detalle.getVariante()), detalle.getCantidad(), detalle.getSubtotal());
        }
        if (pedido.getCuponCodigo() != null) {
            System.out.printf("  Cupon %s: -$%.2f%n", pedido.getCuponCodigo(), pedido.getDescuento());
        }
        System.out.printf("  Total: $%.2f MXN | Pago: %s%s%n", pedido.getTotal(), pedido.getMetodoPago(),
                pedido.getReferenciaPago() == null ? "" : " (" + pedido.getReferenciaPago() + ")");
    }

    private static String descripcionVariante(VarianteProducto variante) {
        if (variante == null) {
            return "";
        }
        return String.format(" (color: %s, talla: %s)", variante.getColor(), variante.getTalla());
    }

    private static int leerEntero(Scanner scanner) {
        String linea = scanner.nextLine().trim();
        try {
            return Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
