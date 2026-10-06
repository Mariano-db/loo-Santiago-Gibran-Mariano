package Panel;

import java.time.LocalDate;
import java.util.Scanner;

public class Entrada {

    private final Scanner scanner;

    public Entrada(Scanner scanner) {
        this.scanner = scanner;
    }

    public String texto(String mensaje) {
        System.out.print(mensaje);
        try {
            return scanner.nextLine().trim();
        } catch (java.util.NoSuchElementException e) {
            System.out.println();
            System.out.println("Entrada cerrada, hasta luego.");
            System.exit(0);
            return "";
        }
    }

    public Integer entero(String mensaje) {
        try {
            return Integer.parseInt(texto(mensaje));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Long id(String mensaje) {
        try {
            return Long.parseLong(texto(mensaje));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double decimal(String mensaje) {
        try {
            double v = Double.parseDouble(texto(mensaje).replace(",", "."));
            return Double.isFinite(v) ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean confirmar(String accion) {
        return texto(accion + "? (s/n): ").equalsIgnoreCase("s");
    }

    public LocalDate fechaOpcional(String mensaje) {
        String t = texto(mensaje);
        return t.isEmpty() ? null : LocalDate.parse(t);
    }
}
