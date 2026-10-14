package clasepagos;

public interface EstrategiaPago {

    boolean procesar(double monto);

    String tipo();
}
