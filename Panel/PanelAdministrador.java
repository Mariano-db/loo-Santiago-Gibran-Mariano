package Panel;

import Exepciones.AccesoDenegadoException;
import Servicios.Almacen;
import claseseguridad.Administrador;
import claseseguridad.Usuario;

import java.util.List;
import java.util.Scanner;

public class PanelAdministrador {

    private final Entrada in;
    private final Almacen almacen;

    public PanelAdministrador(Scanner scanner, Usuario administrador, Almacen almacen) {
        if (!(administrador instanceof Administrador) || !administrador.isActivo()) {
            throw new AccesoDenegadoException("Solo un administrador puede abrir este panel.");
        }
        this.in = new Entrada(scanner);
        this.almacen = almacen;
    }

    public void ejecutar() {
        mostrarAlertasDeStock();
        boolean salir = false;
        while (!salir) {
            System.out.println("=== Panel de administrador ===");
            System.out.println("1. Productos");
            System.out.println("2. Categorias");
            System.out.println("3. Cupones");
            System.out.println("4. Pedidos");
            System.out.println("5. Usuarios");
            System.out.println("6. Facturas");
            System.out.println("7. Reportes");
            System.out.println("8. Proveedores y compras");
            System.out.println("9. Impresiones y recogidas");
            System.out.println("10. Devoluciones");
            System.out.println("11. Inventario");
            System.out.println("0. Cerrar sesion");
            switch (in.texto("Elige una opcion: ")) {
                case "1": new PanelProductos(in, almacen).menuProductos(); break;
                case "2": new PanelProductos(in, almacen).menuCategorias(); break;
                case "3": new PanelCupones(in, almacen).menu(); break;
                case "4": new PanelPedidos(in, almacen).menu(); break;
                case "5": new PanelUsuarios(in, almacen).menu(); break;
                case "6": new PanelFacturacion(in, almacen).menuAdmin(); break;
                case "7": new PanelReportes(in, almacen).menu(); break;
                case "8": new PanelCompras(in, almacen).menu(); break;
                case "9": new PanelImpresion(in, almacen).menuAdmin(); break;
                case "10": new PanelDevoluciones(in, almacen).menu(); break;
                case "11": new PanelInventario(in, almacen).menu(); break;
                case "0":
                    salir = true;
                    System.out.println("Sesion cerrada.");
                    break;
                default: System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private void mostrarAlertasDeStock() {
        List<String> alertas = almacen.servicioInventario.getNotificador().tomarAlertas();
        if (!alertas.isEmpty()) {
            System.out.println("*** Alertas de stock bajo desde la ultima vez ***");
            alertas.forEach(a -> System.out.println("  " + a));
            System.out.println();
        }
    }
}
