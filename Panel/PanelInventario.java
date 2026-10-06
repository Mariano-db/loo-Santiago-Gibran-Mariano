package Panel;

import Inventario.Existencia;
import Inventario.MovimientoInventario;
import Servicios.Almacen;

import java.util.List;
import java.util.Optional;

public class PanelInventario {

    private final Entrada in;
    private final Almacen almacen;

    public PanelInventario(Entrada in, Almacen almacen) {
        this.in = in;
        this.almacen = almacen;
    }

    public void menu() {
        boolean volver = false;
        while (!volver) {
            System.out.println("--- Inventario ---");
            System.out.println("1. Ver existencias");
            System.out.println("2. Ver solo lo que esta en o bajo el minimo");
            System.out.println("3. Ajustar stock (conteo fisico)");
            System.out.println("4. Cambiar stock minimo");
            System.out.println("5. Ultimos movimientos");
            System.out.println("0. Volver");
            switch (in.texto("Elige una opcion: ")) {
                case "1": listar(almacen.servicioInventario.listar()); break;
                case "2": listar(almacen.servicioInventario.bajoMinimo()); break;
                case "3": ajustar(); break;
                case "4": cambiarMinimo(); break;
                case "5": movimientos(); break;
                case "0": volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private void listar(List<Existencia> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay existencias que mostrar.");
            return;
        }
        for (Existencia e : lista) {
            System.out.printf("[%d] %-45s %4d uds (minimo %d)%s%n", e.getId(),
                    almacen.servicioInventario.describir(e.getProductoId(), e.getVarianteId()),
                    e.calcularDisponible(), e.getStockMinimo(), e.estaBajoMinimo() ? "  << BAJO" : "");
        }
    }

    private Existencia elegir() {
        listar(almacen.servicioInventario.listar());
        Long id = in.id("Id de la existencia (vacio para cancelar): ");
        Optional<Existencia> e = almacen.servicioInventario.listar().stream()
                .filter(x -> x.getId().equals(id)).findFirst();
        if (e.isEmpty()) {
            System.out.println("Existencia no encontrada.");
            return null;
        }
        return e.get();
    }

    private void ajustar() {
        Existencia e = elegir();
        if (e == null) {
            return;
        }
        Integer nuevo = in.entero("Stock real contado: ");
        if (nuevo == null || nuevo < 0) {
            System.out.println("El stock debe ser un numero no negativo.");
            return;
        }
        String motivo = in.texto("Motivo del ajuste: ");
        almacen.servicioInventario.ajustar(e.getProductoId(), e.getVarianteId(), nuevo,
                motivo.isBlank() ? "Ajuste manual" : motivo);
        System.out.println("Stock ajustado.");
    }

    private void cambiarMinimo() {
        Existencia e = elegir();
        if (e == null) {
            return;
        }
        Integer minimo = in.entero("Nuevo stock minimo: ");
        if (minimo == null || minimo < 0) {
            System.out.println("El minimo debe ser un numero no negativo.");
            return;
        }
        almacen.servicioInventario.establecerMinimo(e.getProductoId(), e.getVarianteId(), minimo);
        System.out.println("Stock minimo actualizado.");
    }

    private void movimientos() {
        List<MovimientoInventario> lista = almacen.servicioInventario.ultimosMovimientos(15);
        if (lista.isEmpty()) {
            System.out.println("Sin movimientos.");
            return;
        }
        for (MovimientoInventario m : lista) {
            System.out.printf("%s  %-8s %+4d -> %4d  %s  [%s%s]%n", m.getFecha().toLocalDate(), m.getTipo(),
                    m.getCantidad(), m.getStockResultante(), m.getDescripcion(), m.getMotivo(),
                    m.getReferencia() == null ? "" : ", " + m.getReferencia());
        }
    }
}
