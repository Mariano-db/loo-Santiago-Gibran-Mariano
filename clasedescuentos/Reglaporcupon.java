package astore.descuentos;

public class Reglaporcupon extends ReglaDescuento {

    private Cupon cupon;

    public Reglaporcupon() {
    }

    public Cupon getCupon() {
        return cupon;
    }

    public void setCupon(Cupon cupon) {
        this.cupon = cupon;
    }
}
