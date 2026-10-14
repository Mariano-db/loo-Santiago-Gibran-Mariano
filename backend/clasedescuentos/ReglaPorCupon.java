package clasedescuentos;

public class ReglaPorCupon extends ReglaDescuento {

    private Cupon cupon;

    public ReglaPorCupon() {
    }

    @Override
    public boolean aplica() {
        return false;
    }

    public Cupon getCupon() {
        return cupon;
    }

    public void setCupon(Cupon cupon) {
        this.cupon = cupon;
    }
}
