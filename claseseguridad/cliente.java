public class cliente extends usuario {

    private string rfc;
    private tipocliente tipo;
    private int puntosfidelidad;
    private list<orden> historialordenes = new arraylist<>();
    private carritocompra carrito;

    public cliente() {
    }

    public void registrarcompra(orden orden) {
        historialordenes.add(orden);
    }

    public double totalgastadohistorico() {
        return 0;
    }

    public list<producto> verproductos() {
        return new arraylist<>();
    }

    public list<producto> buscarproductos() {
        return new arraylist<>();
    }

    public void agregaralcarrito() {
    }

    public string getrfc() {
        return rfc;
    }

    public void setrfc(string rfc) {
        this.rfc = rfc;
    }

    public tipocliente gettipo() {
        return tipo;
    }

    public void settipo(tipocliente tipo) {
        this.tipo = tipo;
    }

    public int getpuntosfidelidad() {
        return puntosfidelidad;
    }

    public void setpuntosfidelidad(int puntosfidelidad) {
        this.puntosfidelidad = puntosfidelidad;
    }

    public list<orden> gethistorialordenes() {
        return historialordenes;
    }

    public void sethistorialordenes(list<orden> historialordenes) {
        this.historialordenes = historialordenes;
    }

    public carritocompra getcarrito() {
        return carrito;
    }

    public void setcarrito(carritocompra carrito) {
        this.carrito = carrito;
    }
}