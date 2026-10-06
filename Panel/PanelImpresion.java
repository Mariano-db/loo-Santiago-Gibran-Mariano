package Panel;

import Servicios.Almacen;
import Ventas.Pedido;
import claseimpresion.EstadoImpresion;
import claseimpresion.EstadoRecogida;
import claseimpresion.Recogida;
import claseimpresion.SolicitudImpresion;
import claseseguridad.Cliente;

import java.util.List;
import java.util.Optional;

public class PanelImpresion {

    private final Entrada in;
    private final Almacen almacen;

    public PanelImpresion(Entrada in, Almacen almacen) {
        this.in = in;
        this.almacen = almacen;
    }

    public void menuCliente(Cliente cliente) {
        boolean volver = false;
        while (!volver) {
            System.out.println("--- Impresiones ---");
            System.out.println("1. Solicitar una impresion");
            System.out.println("2. Ver mis solicitudes");
            System.out.println("3. Cancelar una solicitud pendiente");
            System.out.println("0. Volver");
            switch (in.texto("Elige una opcion: ")) {
                case "1": solicitar(cliente); break;
                case "2": listar(almacen.impresiones.listarPorCliente(cliente.getId())); break;
                case "3": cancelar(cliente); break;
                case "0": volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private void solicitar(Cliente cliente) {
        SolicitudImpresion s = new SolicitudImpresion();
        s.setCliente(cliente);
        s.setArchivo(in.texto("Nombre del archivo a imprimir: "));
        Integer copias = in.entero("Numero de copias: ");
        s.setCantidad(copias == null ? 0 : copias);
        s.setEspecificaciones(in.texto("Especificaciones (tamano, color, doble cara...): "));
        try {
            s.enviar();
            almacen.impresiones.guardar(s);
            System.out.println("Solicitud #" + s.getId() + " enviada. Entrega el archivo en la tienda.");
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo enviar: " + e.getMessage());
        }
    }

    private void cancelar(Cliente cliente) {
        List<SolicitudImpresion> mias = almacen.impresiones.listarPorCliente(cliente.getId());
        listar(mias);
        Long id = in.id("Id de la solicitud a cancelar (vacio para volver): ");
        Optional<SolicitudImpresion> s = mias.stream().filter(x -> x.getId().equals(id)).findFirst();
        if (s.isEmpty()) {
            return;
        }
        if (s.get().getEstado() != EstadoImpresion.PENDIENTE) {
            System.out.println("Solo se pueden cancelar solicitudes pendientes.");
            return;
        }
        s.get().cancelar();
        almacen.impresiones.guardar(s.get());
        System.out.println("Solicitud cancelada.");
    }

    private void listar(List<SolicitudImpresion> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay solicitudes.");
            return;
        }
        for (SolicitudImpresion s : lista) {
            String quien = s.getCliente() == null ? "" : " - " + s.getCliente().getNombre();
            System.out.printf("[%d] %s x%d - %s - %s%s%n", s.getId(), s.getArchivo(), s.getCantidad(), s.getEstado(),
                    s.getEspecificaciones().isEmpty() ? "sin especificaciones" : s.getEspecificaciones(), quien);
        }
    }

    public void menuAdmin() {
        boolean volver = false;
        while (!volver) {
            System.out.println("--- Impresiones y recogidas ---");
            System.out.println("1. Ver solicitudes de impresion");
            System.out.println("2. Cambiar estado de una solicitud");
            System.out.println("3. Ver recogidas de pedidos");
            System.out.println("4. Marcar recogida como lista");
            System.out.println("5. Registrar entrega de un pedido");
            System.out.println("0. Volver");
            switch (in.texto("Elige una opcion: ")) {
                case "1": listar(almacen.impresiones.listarTodos()); break;
                case "2": cambiarEstadoImpresion(); break;
                case "3": listarRecogidas(); break;
                case "4": avanzarRecogida(false); break;
                case "5": avanzarRecogida(true); break;
                case "0": volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private void cambiarEstadoImpresion() {
        listar(almacen.impresiones.listarTodos());
        Long id = in.id("Id de la solicitud (vacio para cancelar): ");
        Optional<SolicitudImpresion> encontrada = id == null ? Optional.empty() : almacen.impresiones.buscarPorId(id);
        if (encontrada.isEmpty()) {
            System.out.println("Solicitud no encontrada.");
            return;
        }
        SolicitudImpresion s = encontrada.get();
        EstadoImpresion[] estados = EstadoImpresion.values();
        for (int i = 0; i < estados.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, estados[i]);
        }
        Integer opcion = in.entero("Nuevo estado: ");
        if (opcion == null || opcion < 1 || opcion > estados.length) {
            System.out.println("Opcion invalida.");
            return;
        }
        try {
            s.cambiarEstado(estados[opcion - 1]);
            almacen.impresiones.guardar(s);
            System.out.println("Solicitud #" + s.getId() + " ahora esta " + s.getEstado() + ".");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listarRecogidas() {
        List<Recogida> lista = almacen.recogidas.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay recogidas.");
            return;
        }
        for (Recogida r : lista) {
            Pedido p = r.getPedido();
            EstadoRecogida estado = r.estadoActual();
            System.out.printf("[%d] Pedido #%d (%s) - %s - limite: %s%s%n", r.getId(), p == null ? 0 : p.getId(),
                    p == null || p.getCliente() == null ? "?" : p.getCliente().getNombre(), estado,
                    r.getFechaLimite().toLocalDate(),
                    r.getFechaEntrega() == null ? "" : " - entregado: " + r.getFechaEntrega().toLocalDate());
        }
    }

    private void avanzarRecogida(boolean entrega) {
        listarRecogidas();
        Long id = in.id("Id de la recogida (vacio para cancelar): ");
        Optional<Recogida> encontrada = id == null ? Optional.empty() : almacen.recogidas.buscarPorId(id);
        if (encontrada.isEmpty()) {
            System.out.println("Recogida no encontrada.");
            return;
        }
        Recogida r = encontrada.get();
        try {
            if (entrega) {
                almacen.servicioRecogidas.registrarEntrega(r);
                System.out.println("Entrega registrada; el pedido quedo ENTREGADO.");
            } else {
                almacen.servicioRecogidas.marcarLista(r);
                System.out.println("Recogida lista; el cliente tiene " + Servicios.ServicioRecogidas.DIAS_PARA_RECOGER + " dias para pasar por su pedido.");
            }
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
}
