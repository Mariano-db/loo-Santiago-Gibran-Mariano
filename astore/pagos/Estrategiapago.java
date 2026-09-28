package astore.pagos;

public interface Estrategiapago {

    boolean procesar(double monto);

    String tipo();
}
