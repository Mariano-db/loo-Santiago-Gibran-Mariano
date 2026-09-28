public class reporteventas extends reporte {

    private list<orden> ordenes = new arraylist<>();

    public reporteventas() {
    }

    @override
    protected string generarcuerpo() {
        return null;
    }

    public list<orden> getordenes() {
        return ordenes;
    }

    public void setordenes(list<orden> ordenes) {
        this.ordenes = ordenes;
    }
}