public interface iserviciosventas {

    Orden confirmarVenta(Object... args);

    void anularVenta(orden orden, usuario usuario);
}
