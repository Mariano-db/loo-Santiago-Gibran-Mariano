package com.anahuac.astore.seguridad;

import java.util.List;


public class DemoSeguridad {

    public static void main(String[] args) {
     
        Cliente ana     = new Cliente("C-01", "Ana Lopez", "ana",  "ana@astore.com",   "clave123", TipoCliente.REGISTRADO);
        Vendedor Montufar   = new Vendedor("V-01", "Carlos Montufar", "Montufar", "montu08@astore.com", "vende123", "Matutino");
        Administrador root = new Administrador("A-01", "Root", "admin", "admin@astore.com", "admin123");

        // POLIMORFISMO: los tratamos a todos como "Usuario" y cada uno
        // responde segun su tipo real.
        List<Usuario> usuarios = List.of(ana, Montufar, root);

        System.out.println("===== Usuarios del sistema =====");
        for (Usuario u : usuarios) {
            System.out.println(" - " + u);   // usa toString() -> descripcionRol() polimorfico
        }

        System.out.println("\n===== Verificacion de permisos =====");
        for (Usuario u : usuarios) {
            System.out.printf("%-8s | registrar venta: %-5s | generar reportes: %-5s%n",
                    u.getUsername(),
                    u.verificarPermiso(Permiso.REGISTRAR_VENTA),
                    u.verificarPermiso(Permiso.GENERAR_REPORTES));
        }

        System.out.println("\n===== Autenticacion (ENCAPSULAMIENTO) =====");
        System.out.println("root con clave correcta: " + root.autenticar("admin123"));
        System.out.println("root con clave incorrecta: " + root.autenticar("hackeo"));

        System.out.println("\n===== Sobrecarga (POLIMORFISMO) =====");
        System.out.println("Montufar puede 'APLICAR_DESCUENTO' (por texto): "
                + Montufar.verificarPermiso("APLICAR_DESCUENTO"));

        System.out.println("\n===== Regla de fidelidad =====");
        ana.acumularPuntos(250.0);   // 200 / 20 = 12 puntos
        System.out.println("Ana gasto $200 -> puntos: " + ana.getPuntosFidelidad());

        System.out.println("\n===== Usuario desactivado =====");
        Montufar.desactivar();
        System.out.println("Montufar desactivado, autentica: " + Montufar.autenticar("vende123"));
        System.out.println("Montufar desactivado, permiso venta: " + Montufar.verificarPermiso(Permiso.REGISTRAR_VENTA));
    }

    public static class Seguridad {

        public static boolean tienePermiso(Usuario usuario, Permiso permiso) {
            if (usuario == null || permiso == null) {
                return false;
            }

            Rol rol = usuario.getRol();
            if (rol == null) {
                return false;
            }

            return rol.tienePermiso(permiso);
        }

        public static boolean puedeComprar(Usuario usuario) {
            return tienePermiso(usuario, Permiso.REGISTRAR_VENTA) ||
                   usuario instanceof Cliente;
        }
    }
}
