package Panel;

import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Servicios.Almacen;
import claseprovedores.DetalleOrdenCompra;
import claseprovedores.OrdenCompra;
import claseprovedores.Proveedor;
import claseprovedores.Suministro;

import java.util.List;
import java.util.Optional;

public class PanelCompras {

    private final Entrada in;
    private final Almacen almacen;

    public PanelCompras(Entrada in, Almacen almacen) {
        this.in = in;
        this.almacen = almacen;
    }

    public void menu() {
        boolean volver = false;
        while (!volver) {
            System.out.println("--- Proveedores y compras ---");
            System.out.println("1. Listar proveedores");
            System.out.println("2. Crear proveedor");
            System.out.println("3. Activar / desactivar proveedor");
            System.out.println("4. Registrar lo que surte un proveedor (producto y costo)");
            System.out.println("5. Listar ordenes de compra");
            System.out.println("6. Crear orden de compra");
            System.out.println("7. Enviar / recibir / cancelar una orden");
            System.out.println("0. Volver");
            switch (in.texto("Elige una opcion: ")) {
                case "1": listarProveedores(); break;
                case "2": crearProveedor(); break;
                case "3": alternarProveedor(); break;
                case "4": registrarSuministro(); break;
                case "5": listarOrdenes(); break;
                case "6": crearOrden(); break;
                case "7": avanzarOrden(); break;
                case "0": volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private void listarProveedores() {
        List<Proveedor> lista = almacen.proveedores.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay proveedores.");
            return;
        }
        for (Proveedor p : lista) {
            System.out.printf("[%d] %s - tel: %s - %s - %s%n", p.getId(), p.getNombre(), p.getTelefono(), p.getCorreo(),
                    p.isActivo() ? "activo" : "INACTIVO");
            for (Suministro s : p.getSuministros()) {
                System.out.printf("      surte: %s a $%.2f (restock %d dias)%n", s.getProducto().getNombre(),
                        s.getCosto(), s.getTiempoRestockDias());
            }
        }
    }

    private void crearProveedor() {
        String nombre = in.texto("Nombre: ");
        String telefono = in.texto("Telefono: ");
        String correo = in.texto("Correo: ");
        if (nombre.isBlank()) {
            System.out.println("El nombre no puede estar vacio.");
            return;
        }
        if (!correo.isEmpty() && !correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            System.out.println("El correo no tiene un formato valido.");
            return;
        }
        Proveedor p = new Proveedor();
        p.setNombre(nombre);
        p.setTelefono(telefono);
        p.setCorreo(correo);
        p.activar();
        almacen.proveedores.guardar(p);
        System.out.println("Proveedor creado con id " + p.getId() + ".");
    }

    private Proveedor elegirProveedor(boolean soloActivos) {
        listarProveedores();
        Long id = in.id("Id del proveedor (vacio para cancelar): ");
        Optional<Proveedor> p = id == null ? Optional.empty() : almacen.proveedores.buscarPorId(id);
        if (p.isEmpty()) {
            System.out.println("Proveedor no encontrado.");
            return null;
        }
        if (soloActivos && !p.get().isActivo()) {
            System.out.println("Ese proveedor esta inactivo.");
            return null;
        }
        return p.get();
    }

    private void alternarProveedor() {
        Proveedor p = elegirProveedor(false);
        if (p == null) {
            return;
        }
        if (p.isActivo()) {
            p.desactivar();
        } else {
            p.activar();
        }
        almacen.proveedores.guardar(p);
        System.out.println(p.getNombre() + (p.isActivo() ? " activado." : " desactivado."));
    }

    private Producto elegirProducto() {
        for (Producto p : almacen.productos.listarTodos()) {
            System.out.printf("  [%d] %s (stock %d)%n", p.getId(), p.getNombre(), p.getStock());
        }
        Long id = in.id("Id del producto: ");
        Optional<Producto> p = id == null ? Optional.empty() : almacen.productos.buscarPorId(id);
        if (p.isEmpty()) {
            System.out.println("Producto no encontrado.");
            return null;
        }
        return p.get();
    }

    private void registrarSuministro() {
        Proveedor proveedor = elegirProveedor(false);
        if (proveedor == null) {
            return;
        }
        Producto producto = elegirProducto();
        if (producto == null) {
            return;
        }
        Double costo = in.decimal("Costo por unidad (MXN): ");
        Integer dias = in.entero("Dias de reabastecimiento: ");
        if (costo == null || costo < 0 || dias == null || dias < 0) {
            System.out.println("Costo y dias deben ser numeros no negativos.");
            return;
        }
        Suministro s = new Suministro();
        s.setProducto(producto);
        s.actualizarCosto(costo);
        s.actualizarTiempoRestock(dias);
        proveedor.registrarSuministro(s);
        almacen.proveedores.guardar(proveedor);
        System.out.println("Suministro registrado.");
    }

    private void listarOrdenes() {
        List<OrdenCompra> lista = almacen.ordenesCompra.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay ordenes de compra.");
            return;
        }
        for (OrdenCompra o : lista) {
            System.out.printf("[%d] %s - %s - %s - Total: $%.2f%n", o.getId(), o.getFecha().toLocalDate(),
                    o.getProveedor() == null ? "(proveedor eliminado)" : o.getProveedor().getNombre(), o.getEstado(),
                    o.getTotal());
            for (DetalleOrdenCompra d : o.getDetalles()) {
                String variante = d.getVariante() == null ? ""
                        : " (" + d.getVariante().getColor() + ", " + d.getVariante().getTalla() + ")";
                System.out.printf("      %dx %s%s a $%.2f%n", d.getCantidad(), d.getProducto().getNombre(), variante,
                        d.getCostoUnitario());
            }
        }
    }

    private void crearOrden() {
        Proveedor proveedor = elegirProveedor(true);
        if (proveedor == null) {
            return;
        }
        OrdenCompra orden = new OrdenCompra();
        orden.setProveedor(proveedor);
        do {
            Producto producto = elegirProducto();
            if (producto == null) {
                break;
            }
            VarianteProducto variante = null;
            if (!producto.getVariantes().isEmpty()) {
                List<VarianteProducto> vs = producto.getVariantes();
                for (int i = 0; i < vs.size(); i++) {
                    System.out.printf("  %d. %s, %s%n", i + 1, vs.get(i).getColor(), vs.get(i).getTalla());
                }
                Integer opcion = in.entero("Variante: ");
                if (opcion == null || opcion < 1 || opcion > vs.size()) {
                    System.out.println("Variante invalida, no se agrego la linea.");
                    continue;
                }
                variante = vs.get(opcion - 1);
            }
            Suministro s = proveedor.suministroDe(producto.getId());
            Integer cantidad = in.entero("Cantidad: ");
            Double costo = s != null ? (Double) s.getCosto()
                    : in.decimal("Este proveedor no tiene costo registrado para el producto. Costo por unidad: ");
            if (cantidad == null || costo == null) {
                System.out.println("Datos invalidos, no se agrego la linea.");
                continue;
            }
            try {
                orden.agregarDetalle(producto, variante, cantidad, costo);
                System.out.printf("Agregado. Total actual: $%.2f%n", orden.getTotal());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } while (in.confirmar("Agregar otro producto"));

        if (orden.getDetalles().isEmpty()) {
            System.out.println("La orden no tiene productos, no se creo.");
            return;
        }
        almacen.ordenesCompra.guardar(orden);
        System.out.println("Orden #" + orden.getId() + " creada en BORRADOR.");
    }

    private void avanzarOrden() {
        listarOrdenes();
        Long id = in.id("Id de la orden (vacio para cancelar): ");
        Optional<OrdenCompra> encontrada = id == null ? Optional.empty() : almacen.ordenesCompra.buscarPorId(id);
        if (encontrada.isEmpty()) {
            System.out.println("Orden no encontrada.");
            return;
        }
        OrdenCompra orden = encontrada.get();
        System.out.println("1. Enviar al proveedor");
        System.out.println("2. Marcar como recibida (suma el stock)");
        System.out.println("3. Cancelar");
        try {
            switch (in.texto("Elige una opcion: ")) {
                case "1":
                    orden.enviar();
                    almacen.ordenesCompra.guardar(orden);
                    System.out.println("Orden enviada.");
                    break;
                case "2":
                    almacen.servicioCompras.recibir(orden);
                    System.out.println("Orden recibida; el stock de los productos se actualizo.");
                    break;
                case "3":
                    if (in.confirmar("Cancelar la orden #" + orden.getId())) {
                        orden.cancelar();
                        almacen.ordenesCompra.guardar(orden);
                        System.out.println("Orden cancelada.");
                    }
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
}
