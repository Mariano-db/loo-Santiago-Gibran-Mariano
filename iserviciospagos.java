public interface iserviciospagos {

    void procesarPago(orden orden, estrategiapago estrategiaPago, double monto);
}