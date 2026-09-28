public class reporteinventario extends reporte {

    private inventario inventario;
    private list<producto> catalogo = new arraylist<>();

    public reporteinventario() {
    }

    @override
    protected string generarcuerpo() {
        return null;
    }

    public inventario getinventario() {
        return inventario;
    }

    public void setinventario(inventario inventario) {
        this.inventario = inventario;
    }

    public list<producto> getcatalogo() {
        return catalogo;
    }

    public void setcatalogo(list<producto> catalogo) {
        this.catalogo = catalogo;
    }
}