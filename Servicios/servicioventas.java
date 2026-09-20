public class servicioventas implements iserviciosinventario {

    private iserviciosinventario servicioInventario;

    public servicioventas() {
    }

    @Override
    public Orden confirmarVenta(Object... args) {
        return null;
    }

    @Override
    public void anularVenta(orden orden, usuario usuario) {
    }

    public iserviciosinventario getServicioInventario() {
        return servicioInventario;
    }

    public void setServicioInventario(iserviciosinventario servicioInventario) {
        this.servicioInventario = servicioInventario;
    }
}
