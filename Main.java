import Panel.Entrada;
import Panel.MenuAcceso;
import Panel.MenuCliente;
import Panel.PanelAdministrador;
import Servicios.Almacen;
import Utils.ConfiguracionSistema;
import claseseguridad.Administrador;
import claseseguridad.Cliente;
import claseseguridad.Usuario;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ConfiguracionSistema config = ConfiguracionSistema.getInstancia();
        config.setNombreTienda("A-Store");
        config.setMoneda("MXN");
        config.setTasaImpuestoDefault(0.16);

        Almacen almacen = new Almacen("data");

        System.out.println("========================================");
        System.out.println(" " + config.getNombreTienda() + " - Universidad Anahuac Cancun");
        System.out.println("========================================");
        System.out.println("Sistema iniciado correctamente.");
        System.out.println("Moneda: " + config.getMoneda());
        System.out.println();

        try (Scanner scanner = new Scanner(System.in)) {
            Entrada entrada = new Entrada(scanner);
            MenuAcceso acceso = new MenuAcceso(entrada, almacen.servicioUsuarios);

            Usuario usuario = acceso.pedirUsuario();
            while (usuario != null) {
                if (usuario instanceof Administrador) {
                    new PanelAdministrador(scanner, usuario, almacen).ejecutar();
                } else {
                    new MenuCliente(entrada, almacen, (Cliente) usuario).ejecutar();
                }
                usuario.cerrarSesion();
                usuario = acceso.pedirUsuario();
            }
            System.out.println("Hasta luego.");
        }
    }
}
