package Panel;

import Servicios.Almacen;
import claseseguridad.Administrador;
import claseseguridad.Usuario;

import java.util.Optional;

public class PanelUsuarios {

    private final Entrada in;
    private final Almacen almacen;

    public PanelUsuarios(Entrada in, Almacen almacen) {
        this.in = in;
        this.almacen = almacen;
    }

    public void menu() {
        boolean volver = false;
        while (!volver) {
            System.out.println("--- Usuarios ---");
            System.out.println("1. Listar");
            System.out.println("2. Activar / desactivar cuenta");
            System.out.println("0. Volver");
            switch (in.texto("Elige una opcion: ")) {
                case "1": listar(); break;
                case "2": alternarCuenta(); break;
                case "0": volver = true; break;
                default: System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private void listar() {
        for (Usuario u : almacen.usuarios.listarTodos()) {
            System.out.printf("[%d] %s (%s) - %s - %s - %s%n", u.getId(), u.getNombre(), u.getUsername(), u.getEmail(),
                    u.getRol().getNombre(), u.isActivo() ? "activo" : "DESACTIVADO");
        }
    }

    private void alternarCuenta() {
        listar();
        Long id = in.id("Id del usuario (vacio para cancelar): ");
        Optional<Usuario> encontrado = id == null ? Optional.empty() : almacen.usuarios.buscarPorId(id);
        if (encontrado.isEmpty()) {
            System.out.println("Usuario no encontrado.");
            return;
        }
        Usuario u = encontrado.get();
        if (u instanceof Administrador) {
            System.out.println("Las cuentas de administrador no se pueden desactivar desde aqui.");
            return;
        }
        if (u.isActivo()) {
            u.desactivar();
        } else {
            u.activar();
        }
        almacen.usuarios.guardar(u);
        System.out.println("Cuenta de " + u.getUsername() + (u.isActivo() ? " activada." : " desactivada."));
    }
}
