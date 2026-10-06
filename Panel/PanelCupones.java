package Panel;

import Servicios.Almacen;
import clasedescuentos.Cupon;
import clasedescuentos.TipoDescuento;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class PanelCupones {

    private final Entrada in;
    private final Almacen almacen;

    public PanelCupones(Entrada in, Almacen almacen) {
        this.in = in;
        this.almacen = almacen;
    }

    public void menu() {
        boolean volver = false;
        while (!volver) {
            System.out.println("--- Cupones ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Activar / desactivar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");
            switch (in.texto("Elige una opcion: ")) {
                case "1": listar(); break;
                case "2": crear(); break;
                case "3": alternar(); break;
                case "4": eliminar(); break;
                case "0": volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private void listar() {
        List<Cupon> lista = almacen.cupones.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay cupones.");
            return;
        }
        for (Cupon c : lista) {
            System.out.printf("[%d] %s - min: $%.2f - usos: %d/%s - vigencia: %s a %s - %s%n", c.getId(),
                    c.descripcion(), c.getCompraMinima(), c.getUsosActuales(),
                    c.getUsosMaximos() == 0 ? "ilimitado" : String.valueOf(c.getUsosMaximos()),
                    c.getFechaInicio() == null ? "siempre" : c.getFechaInicio(),
                    c.getFechaFin() == null ? "siempre" : c.getFechaFin(),
                    c.esValido() ? "VIGENTE" : "no disponible");
        }
    }

    private void crear() {
        String codigo = in.texto("Codigo (sin espacios): ").toUpperCase();
        if (!codigo.matches("[A-Z0-9_-]{3,20}")) {
            System.out.println("El codigo debe tener de 3 a 20 letras, numeros, - o _.");
            return;
        }
        if (almacen.cupones.buscarPorCodigo(codigo).isPresent()) {
            System.out.println("Ya existe un cupon con ese codigo.");
            return;
        }
        TipoDescuento tipo = pedirTipo();
        if (tipo == null) {
            return;
        }
        Double valor = in.decimal(tipo == TipoDescuento.PORCENTAJE ? "Porcentaje (1-100): " : "Monto (MXN): ");
        if (valor == null || valor <= 0 || (tipo == TipoDescuento.PORCENTAJE && valor > 100)) {
            System.out.println("Valor invalido.");
            return;
        }
        Double minima = in.decimal("Compra minima (0 para ninguna): ");
        Integer usos = in.entero("Usos maximos (0 = ilimitado): ");
        if (minima == null || minima < 0 || usos == null || usos < 0) {
            System.out.println("Compra minima y usos deben ser numeros no negativos.");
            return;
        }
        try {
            LocalDate inicio = in.fechaOpcional("Fecha de inicio AAAA-MM-DD (vacio = sin limite): ");
            LocalDate fin = in.fechaOpcional("Fecha de fin AAAA-MM-DD (vacio = sin limite): ");
            if (inicio != null && fin != null && fin.isBefore(inicio)) {
                System.out.println("La fecha de fin no puede ser anterior a la de inicio.");
                return;
            }
            Cupon c = new Cupon();
            c.setCodigo(codigo);
            c.setTipo(tipo);
            c.setValor(valor);
            c.setCompraMinima(minima);
            c.setUsosMaximos(usos);
            c.setFechaInicio(inicio);
            c.setFechaFin(fin);
            c.setActivo(true);
            almacen.cupones.guardar(c);
            System.out.println("Cupon creado: " + c.descripcion());
        } catch (DateTimeParseException e) {
            System.out.println("Fecha invalida, usa el formato AAAA-MM-DD.");
        }
    }

    private TipoDescuento pedirTipo() {
        String opcion = in.texto("Tipo (1 = porcentaje, 2 = monto fijo): ");
        if (opcion.equals("1")) {
            return TipoDescuento.PORCENTAJE;
        }
        if (opcion.equals("2")) {
            return TipoDescuento.MONTO_FIJO;
        }
        System.out.println("Tipo invalido.");
        return null;
    }

    private Cupon elegir() {
        listar();
        Long id = in.id("Id del cupon (vacio para cancelar): ");
        Optional<Cupon> c = id == null ? Optional.empty() : almacen.cupones.buscarPorId(id);
        if (c.isEmpty()) {
            System.out.println("Cupon no encontrado.");
        }
        return c.orElse(null);
    }

    private void alternar() {
        Cupon c = elegir();
        if (c == null) {
            return;
        }
        c.setActivo(!c.isActivo());
        almacen.cupones.guardar(c);
        System.out.println("Cupon " + c.getCodigo() + (c.isActivo() ? " activado." : " desactivado."));
    }

    private void eliminar() {
        Cupon c = elegir();
        if (c != null && in.confirmar("Eliminar el cupon " + c.getCodigo())) {
            almacen.cupones.eliminar(c.getId());
            System.out.println("Cupon eliminado.");
        }
    }
}
