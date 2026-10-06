package Panel;

import Facturacion.DatosFiscales;
import Facturacion.Factura;
import Servicios.Almacen;
import Ventas.EstadoPedido;
import Ventas.Pedido;
import claseseguridad.Cliente;

import java.util.List;
import java.util.Optional;

public class PanelFacturacion {

    private final Entrada in;
    private final Almacen almacen;

    public PanelFacturacion(Entrada in, Almacen almacen) {
        this.in = in;
        this.almacen = almacen;
    }

    public void menuCliente(Cliente cliente) {
        boolean volver = false;
        while (!volver) {
            System.out.println("--- Mis facturas ---");
            System.out.println("1. Solicitar factura de un pedido");
            System.out.println("2. Ver mis facturas");
            System.out.println("0. Volver");
            switch (in.texto("Elige una opcion: ")) {
                case "1": solicitar(cliente); break;
                case "2": verFacturas(cliente); break;
                case "0": volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private void solicitar(Cliente cliente) {
        List<Pedido> facturables = new java.util.ArrayList<>();
        for (Pedido p : almacen.pedidos.listarPorCliente(cliente.getId())) {
            if (p.getEstado() != EstadoPedido.PENDIENTE && p.getEstado() != EstadoPedido.CANCELADO
                    && almacen.servicioFacturacion.buscarVigentePorPedido(p.getId()).isEmpty()) {
                facturables.add(p);
            }
        }
        if (facturables.isEmpty()) {
            System.out.println("No tienes pedidos pagados pendientes de facturar.");
            return;
        }
        for (Pedido p : facturables) {
            System.out.printf("  Pedido #%d - %s - $%.2f MXN%n", p.getId(), p.getFecha().toLocalDate(), p.getTotal());
        }
        Long id = in.id("Numero de pedido a facturar (vacio para cancelar): ");
        Optional<Pedido> elegido = facturables.stream().filter(p -> p.getId().equals(id)).findFirst();
        if (elegido.isEmpty()) {
            System.out.println("Pedido no valido.");
            return;
        }

        DatosFiscales datos = new DatosFiscales();
        datos.setRfc(in.texto("RFC" + (cliente.getRfc() == null ? "" : " [" + cliente.getRfc() + "]") + ": "));
        if (datos.getRfc().isEmpty() && cliente.getRfc() != null) {
            datos.setRfc(cliente.getRfc());
        }
        datos.setNombreRazonSocial(in.texto("Nombre o razon social: "));
        datos.setRegimenFiscal(in.texto("Regimen fiscal (ej. 612 Personas fisicas con actividad empresarial): "));
        datos.setCodigoPostal(in.texto("Codigo postal fiscal: "));
        datos.setUsoCFDI(in.texto("Uso de CFDI (ej. G03 Gastos en general): "));
        try {
            Factura f = almacen.servicioFacturacion.solicitar(cliente, elegido.get(), datos);
            System.out.println("Factura " + f.folio() + " solicitada. La tienda la emitira en breve.");
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo solicitar: " + e.getMessage());
        }
    }

    private void verFacturas(Cliente cliente) {
        List<Factura> facturas = almacen.servicioFacturacion.listarPorCliente(cliente);
        if (facturas.isEmpty()) {
            System.out.println("Aun no tienes facturas.");
            return;
        }
        for (Factura f : facturas) {
            System.out.printf("%s - Pedido #%d - %s - $%.2f%n", f.folio(), f.getPedido().getId(), f.getEstado(), f.getTotal());
        }
        Long id = in.id("Id de factura para ver el detalle (vacio para volver): ");
        if (id == null) {
            return;
        }
        for (Factura f : facturas) {
            if (f.getId().equals(id)) {
                System.out.println(f.aTexto());
                return;
            }
        }
        System.out.println("Factura no encontrada.");
    }

    public void menuAdmin() {
        boolean volver = false;
        while (!volver) {
            System.out.println("--- Facturas ---");
            System.out.println("1. Listar");
            System.out.println("2. Emitir una factura solicitada");
            System.out.println("3. Cancelar una factura");
            System.out.println("4. Ver detalle");
            System.out.println("0. Volver");
            switch (in.texto("Elige una opcion: ")) {
                case "1": listar(); break;
                case "2": cambiar(true); break;
                case "3": cambiar(false); break;
                case "4": detalle(); break;
                case "0": volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private void listar() {
        List<Factura> facturas = almacen.servicioFacturacion.listarTodas();
        if (facturas.isEmpty()) {
            System.out.println("No hay facturas.");
            return;
        }
        for (Factura f : facturas) {
            System.out.printf("[%d] %s - Pedido #%d - %s (%s) - $%.2f - %s%n", f.getId(), f.folio(),
                    f.getPedido() == null ? 0 : f.getPedido().getId(), f.getDatosFiscales().getNombreRazonSocial(),
                    f.getDatosFiscales().getRfc(), f.getTotal(), f.getEstado());
        }
    }

    private Factura elegir() {
        listar();
        Long id = in.id("Id de la factura (vacio para cancelar): ");
        Optional<Factura> f = id == null ? Optional.empty() : almacen.servicioFacturacion.buscarPorId(id);
        if (f.isEmpty()) {
            System.out.println("Factura no encontrada.");
            return null;
        }
        return f.get();
    }

    private void cambiar(boolean emitir) {
        Factura f = elegir();
        if (f == null) {
            return;
        }
        try {
            if (emitir) {
                almacen.servicioFacturacion.emitir(f);
                System.out.println("Factura " + f.folio() + " emitida.");
            } else if (in.confirmar("Cancelar la factura " + f.folio())) {
                almacen.servicioFacturacion.cancelar(f);
                System.out.println("Factura " + f.folio() + " cancelada.");
            }
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void detalle() {
        Factura f = elegir();
        if (f != null) {
            System.out.println(f.aTexto());
        }
    }
}
