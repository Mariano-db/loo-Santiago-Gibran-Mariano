public class reporteclientes extends reporte {

    private list<cliente> clientes = new arraylist<>();

    public reporteclientes() {
    }

    @override
    protected string generarcuerpo() {
        return null;
    }

    public list<cliente> getclientes() {
        return clientes;
    }

    public void setclientes(list<cliente> clientes) {
        this.clientes = clientes;
    }
}